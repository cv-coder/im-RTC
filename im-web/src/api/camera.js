class ImCamera {
	constructor() {
		this.stream = null;
	}
}

function buildMediaError(error, actionText) {
	const name = error && error.name ? error.name : "UnknownError";
	const detail = error && error.message ? error.message : "";
	let message = actionText + "失败";
	switch (name) {
		case "NotAllowedError":
		case "PermissionDeniedError":
			message = actionText + "失败: 浏览器或系统拒绝了麦克风/摄像头权限";
			break;
		case "NotFoundError":
		case "DevicesNotFoundError":
			message = actionText + "失败: 未检测到可用的音视频设备";
			break;
		case "NotReadableError":
		case "TrackStartError":
			message = actionText + "失败: 设备可能被其他应用占用";
			break;
		case "OverconstrainedError":
		case "ConstraintNotSatisfiedError":
			message = actionText + "失败: 当前设备不支持请求的采集参数";
			break;
		case "SecurityError":
		case "TypeError":
			message = actionText + "失败: 请在 https 或 localhost 环境下访问";
			break;
		case "NotSupportedError":
			message = actionText + "失败: 当前浏览器不支持媒体采集";
			break;
		default:
			break;
	}
	return {
		code: 0,
		name,
		message,
		detail,
		raw: error
	};
}

ImCamera.prototype.isEnable = function () {
	return !!navigator && !!navigator.mediaDevices && !!navigator.mediaDevices.getUserMedia;
}

ImCamera.prototype.openVideo = function () {
	return new Promise((resolve, reject) => {
		if (!this.isEnable()) {
			reject(buildMediaError({ name: "NotSupportedError" }, "打开摄像头"));
			return;
		}
		if (this.stream) {
			this.close();
		}
		const constraints = {
			video: true,
			audio: {
				echoCancellation: true,
				noiseSuppression: true
			}
		};
		navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
			this.stream = stream;
			resolve(stream);
		}).catch((error) => {
			const mediaError = buildMediaError(error, "打开摄像头");
			console.error("打开摄像头失败:", mediaError.name, mediaError.detail, mediaError.raw);
			reject(mediaError);
		});
	});
}

ImCamera.prototype.openAudio = function () {
	return new Promise((resolve, reject) => {
		if (!this.isEnable()) {
			reject(buildMediaError({ name: "NotSupportedError" }, "打开麦克风"));
			return;
		}
		if (this.stream) {
			this.close();
		}
		const constraints = {
			video: false,
			audio: {
				echoCancellation: true,
				noiseSuppression: true
			}
		};
		navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
			this.stream = stream;
			resolve(stream);
		}).catch((error) => {
			const mediaError = buildMediaError(error, "打开麦克风");
			console.error("打开麦克风失败:", mediaError.name, mediaError.detail, mediaError.raw);
			reject(mediaError);
		});
	});
}

ImCamera.prototype.close = function () {
	if (this.stream) {
		this.stream.getTracks().forEach((track) => {
			track.stop();
		});
		this.stream = null;
	}
}

export default ImCamera;
