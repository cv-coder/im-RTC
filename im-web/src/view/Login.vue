<template>
	<div class="login-view">
		<!-- 动态背景气泡 -->
		<div class="bg-bubbles">
			<div class="bubble" v-for="n in 8" :key="n" :class="`bubble-${n}`"></div>
		</div>

		<!-- 背景装饰圆 -->
		<div class="bg-glow bg-glow-1"></div>
		<div class="bg-glow bg-glow-2"></div>
		<div class="bg-glow bg-glow-3"></div>

		<div class="login-wrapper">
			<!-- 左侧品牌区 -->
			<div class="brand-panel">
				<div class="brand-inner">
					<div class="brand-icon">
						<img src="../../public/logo.png" alt="logo" />
					</div>
					<h1 class="brand-title">LWF<span>IM</span></h1>
					<p class="brand-tagline">随时随地，连接你我</p>
					<div class="brand-features">
						<div class="feature-item">
							<span class="feature-dot"></span>
							<span>实时消息推送</span>
						</div>
						<div class="feature-item">
							<span class="feature-dot"></span>
							<span>音视频通话</span>
						</div>
						<div class="feature-item">
							<span class="feature-dot"></span>
							<span>群组聊天</span>
						</div>
					</div>
				</div>
			</div>

			<!-- 右侧登录卡片 -->
			<div class="card-panel">
				<div class="login-card">
					<div class="card-header">
						<h2 class="card-title">欢迎回来</h2>
						<p class="card-subtitle">登录您的账号继续使用</p>
					</div>

					<el-form
						class="login-form"
						:model="loginForm"
						:rules="rules"
						ref="loginForm"
						@keyup.enter.native="submitForm('loginForm')"
					>
						<el-form-item prop="userName">
							<div class="input-wrapper">
								<span class="input-icon">
									<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
										<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
										<circle cx="12" cy="7" r="4"/>
									</svg>
								</span>
								<el-input
									v-model="loginForm.userName"
									autocomplete="off"
									placeholder="请输入用户名"
								></el-input>
							</div>
						</el-form-item>

						<el-form-item prop="password">
							<div class="input-wrapper">
								<span class="input-icon">
									<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
										<rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
										<path d="M7 11V7a5 5 0 0 1 10 0v4"/>
									</svg>
								</span>
								<el-input
									type="password"
									v-model="loginForm.password"
									autocomplete="off"
									placeholder="请输入密码"
									show-password
								></el-input>
							</div>
						</el-form-item>

						<el-form-item>
							<button class="login-btn" @click.prevent="submitForm('loginForm')" :class="{ loading: isLoading }">
								<span class="btn-text">{{ isLoading ? '登录中...' : '立即登录' }}</span>
								<span class="btn-arrow">
									<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
										<line x1="5" y1="12" x2="19" y2="12"/>
										<polyline points="12 5 19 12 12 19"/>
									</svg>
								</span>
							</button>
						</el-form-item>
					</el-form>

					<div class="card-footer">
						<span>还没有账号？</span>
						<router-link to="/register" class="register-link">立即注册</router-link>
					</div>
				</div>
			</div>
		</div>

		<icp></icp>
	</div>
</template>

<script>
import Icp from '../components/common/Icp.vue'
export default {
	name: "login",
	components: {
		Icp
	},
	data() {
		var checkUsername = (rule, value, callback) => {
			if (!value) {
				return callback(new Error('请输入用户名'));
			}
			callback();
		};
		var checkPassword = (rule, value, callback) => {
			if (value === '') {
				callback(new Error('请输入密码'));
			}
			callback();
		};
		return {
			isLoading: false,
			loginForm: {
				terminal: this.$enums.TERMINAL_TYPE.WEB,
				userName: '',
				password: ''
			},
			rules: {
				userName: [{ validator: checkUsername, trigger: 'blur' }],
				password: [{ validator: checkPassword, trigger: 'blur' }]
			}
		};
	},
	methods: {
		submitForm(formName) {
			this.$refs[formName].validate((valid) => {
				if (valid) {
					this.isLoading = true;
					this.$http({
						url: "/login",
						method: 'post',
						data: this.loginForm
					})
					.then((data) => {
						this.setCookie('username', this.loginForm.userName);
						this.setCookie('password', this.loginForm.password);
						sessionStorage.setItem("accessToken", data.accessToken);
						sessionStorage.setItem("refreshToken", data.refreshToken);
						this.$message.success("登录成功");
						this.$router.push("/home/chat");
					})
					.finally(() => {
						this.isLoading = false;
					});
				}
			});
		},
		resetForm(formName) {
			this.$refs[formName].resetFields();
		},
		getCookie(name) {
			let reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			let arr = document.cookie.match(reg);
			if (arr) {
				return unescape(arr[2]);
			}
			return '';
		},
		setCookie(name, value) {
			document.cookie = name + "=" + escape(value);
		}
	},
	mounted() {
		this.loginForm.userName = this.getCookie("username");
		this.loginForm.password = this.getCookie("password");
	}
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800;900&display=swap');

/* ===== 全局变量 ===== */
.login-view {
	--primary: #6C63FF;
	--primary-light: #8B85FF;
	--primary-dark: #5549E8;
	--accent: #FF6584;
	--bg-deep: #0F0E1A;
	--bg-card: rgba(255,255,255,0.06);
	--glass-border: rgba(255,255,255,0.12);
	--text-primary: #FFFFFF;
	--text-secondary: rgba(255,255,255,0.55);
	--text-muted: rgba(255,255,255,0.35);

	font-family: 'Nunito', -apple-system, sans-serif;
	width: 100%;
	height: 100%;
	min-height: 100vh;
	background: var(--bg-deep);
	overflow: hidden;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* ===== 背景光晕 ===== */
.bg-glow {
	position: absolute;
	border-radius: 50%;
	filter: blur(80px);
	pointer-events: none;
	animation: glow-float 8s ease-in-out infinite alternate;
}
.bg-glow-1 {
	width: 500px; height: 500px;
	background: radial-gradient(circle, rgba(108,99,255,0.35) 0%, transparent 70%);
	top: -100px; left: -100px;
	animation-duration: 10s;
}
.bg-glow-2 {
	width: 400px; height: 400px;
	background: radial-gradient(circle, rgba(255,101,132,0.25) 0%, transparent 70%);
	bottom: -80px; right: 5%;
	animation-duration: 7s;
	animation-delay: -3s;
}
.bg-glow-3 {
	width: 300px; height: 300px;
	background: radial-gradient(circle, rgba(67,205,255,0.2) 0%, transparent 70%);
	top: 40%; right: 30%;
	animation-duration: 12s;
	animation-delay: -6s;
}
@keyframes glow-float {
	0%   { transform: translate(0, 0) scale(1); }
	100% { transform: translate(30px, 40px) scale(1.1); }
}

/* ===== 浮动气泡 ===== */
.bg-bubbles {
	position: absolute;
	inset: 0;
	pointer-events: none;
	overflow: hidden;
}
.bubble {
	position: absolute;
	border-radius: 50%;
	background: rgba(108,99,255,0.08);
	border: 1px solid rgba(108,99,255,0.15);
	animation: bubble-rise linear infinite;
}
@for $i from 1 through 8 {
	.bubble-#{$i} {
		$size: (30 + $i * 15) * 1px;
		width: $size; height: $size;
		left: ($i * 11) * 1%;
		bottom: -200px;
		animation-duration: (10 + $i * 3) * 1s;
		animation-delay: ($i * -2) * 1s;
	}
}
@keyframes bubble-rise {
	0%   { transform: translateY(0) rotate(0deg); opacity: 0; }
	10%  { opacity: 1; }
	90%  { opacity: 0.6; }
	100% { transform: translateY(-110vh) rotate(720deg); opacity: 0; }
}

/* ===== 主布局 ===== */
.login-wrapper {
	position: relative;
	z-index: 10;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 0;
	width: 900px;
	max-width: 95vw;
	min-height: 520px;
	border-radius: 28px;
	overflow: hidden;
	box-shadow:
		0 40px 80px rgba(0,0,0,0.5),
		0 0 0 1px var(--glass-border),
		inset 0 1px 0 rgba(255,255,255,0.1);
	animation: card-enter 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}
@keyframes card-enter {
	from { opacity: 0; transform: translateY(40px) scale(0.96); }
	to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ===== 左侧品牌面板 ===== */
.brand-panel {
	flex: 1;
	background: linear-gradient(145deg, rgba(108,99,255,0.9) 0%, rgba(67,56,202,0.95) 100%);
	padding: 60px 50px;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	overflow: hidden;

	&::before {
		content: '';
		position: absolute;
		top: -60px; right: -60px;
		width: 250px; height: 250px;
		background: rgba(255,255,255,0.08);
		border-radius: 50%;
	}
	&::after {
		content: '';
		position: absolute;
		bottom: -80px; left: -40px;
		width: 300px; height: 300px;
		background: rgba(255,255,255,0.05);
		border-radius: 50%;
	}
}
.brand-inner {
	position: relative;
	z-index: 2;
	text-align: center;
}
.brand-icon {
	width: 72px; height: 72px;
	background: rgba(255,255,255,0.2);
	border-radius: 20px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 24px;
	backdrop-filter: blur(10px);
	border: 1px solid rgba(255,255,255,0.3);
	box-shadow: 0 8px 32px rgba(0,0,0,0.2);

	img {
		width: 44px; height: 44px;
		object-fit: contain;
		filter: brightness(10);
	}
}
.brand-title {
	font-size: 48px;
	font-weight: 900;
	color: #fff;
	letter-spacing: -1px;
	margin: 0 0 10px;
	line-height: 1;

	span {
		color: rgba(255,255,255,0.7);
		font-weight: 400;
	}
}
.brand-tagline {
	color: rgba(255,255,255,0.8);
	font-size: 15px;
	font-weight: 600;
	letter-spacing: 3px;
	margin: 0 0 40px;
	text-transform: uppercase;
}
.brand-features {
	display: flex;
	flex-direction: column;
	gap: 14px;
	text-align: left;
}
.feature-item {
	display: flex;
	align-items: center;
	gap: 12px;
	color: rgba(255,255,255,0.9);
	font-size: 14px;
	font-weight: 600;
	animation: feature-slide 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;

	&:nth-child(1) { animation-delay: 0.3s; }
	&:nth-child(2) { animation-delay: 0.45s; }
	&:nth-child(3) { animation-delay: 0.6s; }
}
@keyframes feature-slide {
	from { opacity: 0; transform: translateX(-20px); }
	to   { opacity: 1; transform: translateX(0); }
}
.feature-dot {
	width: 8px; height: 8px;
	background: rgba(255,255,255,0.9);
	border-radius: 50%;
	flex-shrink: 0;
	box-shadow: 0 0 8px rgba(255,255,255,0.6);
}

/* ===== 右侧卡片 ===== */
.card-panel {
	flex: 1;
	background: rgba(20, 19, 35, 0.95);
	backdrop-filter: blur(20px);
	padding: 60px 50px;
	display: flex;
	align-items: center;
	justify-content: center;
}
.login-card {
	width: 100%;
	max-width: 320px;
}
.card-header {
	margin-bottom: 36px;
	animation: fade-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) 0.2s both;
}
.card-title {
	font-size: 30px;
	font-weight: 800;
	color: var(--text-primary);
	margin: 0 0 8px;
	letter-spacing: -0.5px;
}
.card-subtitle {
	color: var(--text-secondary);
	font-size: 14px;
	font-weight: 600;
	margin: 0;
}
@keyframes fade-up {
	from { opacity: 0; transform: translateY(16px); }
	to   { opacity: 1; transform: translateY(0); }
}

/* ===== 表单样式覆盖 ===== */
.login-form {
	animation: fade-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) 0.35s both;

	::v-deep .el-form-item {
		margin-bottom: 20px;
	}
	::v-deep .el-form-item__content {
		line-height: normal;
	}
	::v-deep .el-form-item__error {
		color: var(--accent);
		font-size: 12px;
		padding-top: 4px;
	}
	::v-deep .el-input__inner {
		background: rgba(255,255,255,0.05) !important;
		border: 1.5px solid var(--glass-border) !important;
		border-radius: 12px !important;
		color: var(--text-primary) !important;
		height: 50px !important;
		padding: 0 16px 0 48px !important;
		font-size: 14px !important;
		font-family: 'Nunito', sans-serif !important;
		font-weight: 600 !important;
		transition: all 0.25s ease !important;

		&::placeholder {
			color: var(--text-muted) !important;
			font-weight: 500 !important;
		}
		&:focus {
			background: rgba(108,99,255,0.08) !important;
			border-color: var(--primary-light) !important;
			box-shadow: 0 0 0 3px rgba(108,99,255,0.15) !important;
		}
	}
	::v-deep .el-input__suffix {
		right: 12px;
		.el-input__suffix-inner {
			display: flex;
			align-items: center;
			height: 100%;
		}
		.el-icon-view, .el-input__icon {
			color: var(--text-muted) !important;
			font-size: 18px;
			cursor: pointer;
			transition: color 0.2s;
			&:hover { color: var(--primary-light) !important; }
		}
	}
}

/* ===== 输入框包裹 ===== */
.input-wrapper {
	position: relative;

	.input-icon {
		position: absolute;
		left: 14px;
		top: 50%;
		transform: translateY(-50%);
		z-index: 10;
		width: 20px; height: 20px;
		display: flex;
		align-items: center;
		justify-content: center;
		pointer-events: none;

		svg {
			width: 18px; height: 18px;
			color: var(--text-muted);
			transition: color 0.25s;
		}
	}

	&:focus-within .input-icon svg {
		color: var(--primary-light);
	}
}

/* ===== 登录按钮 ===== */
.login-btn {
	width: 100%;
	height: 52px;
	background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
	border: none;
	border-radius: 14px;
	color: #fff;
	font-size: 15px;
	font-weight: 800;
	font-family: 'Nunito', sans-serif;
	letter-spacing: 0.5px;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10px;
	transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
	box-shadow: 0 8px 24px rgba(108,99,255,0.4);
	position: relative;
	overflow: hidden;
	margin-top: 8px;

	&::before {
		content: '';
		position: absolute;
		inset: 0;
		background: linear-gradient(135deg, rgba(255,255,255,0.15), transparent);
		opacity: 0;
		transition: opacity 0.3s;
	}
	&:hover {
		transform: translateY(-2px);
		box-shadow: 0 12px 32px rgba(108,99,255,0.55);
		&::before { opacity: 1; }
		.btn-arrow { transform: translateX(4px); }
	}
	&:active {
		transform: translateY(0);
		box-shadow: 0 4px 12px rgba(108,99,255,0.4);
	}
	&.loading {
		opacity: 0.75;
		cursor: not-allowed;
		pointer-events: none;
	}
}
.btn-text { flex: 1; text-align: center; }
.btn-arrow {
	transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
	display: flex;
	align-items: center;
	svg {
		width: 18px; height: 18px;
	}
}

/* ===== 页脚 ===== */
.card-footer {
	margin-top: 28px;
	text-align: center;
	color: var(--text-muted);
	font-size: 13px;
	font-weight: 600;
	animation: fade-up 0.6s cubic-bezier(0.16, 1, 0.3, 1) 0.5s both;
}
.register-link {
	color: var(--primary-light);
	text-decoration: none;
	font-weight: 700;
	margin-left: 6px;
	transition: color 0.2s;

	&:hover { color: #fff; }
}

/* ===== ICP 备案 ===== */
::v-deep .icp {
	position: absolute;
	bottom: 16px;
	left: 50%;
	transform: translateX(-50%);
	color: rgba(255,255,255,0.2) !important;
	font-size: 12px;
	z-index: 20;
	white-space: nowrap;
}

/* ===== 响应式 ===== */
@media (max-width: 700px) {
	.login-wrapper {
		flex-direction: column;
		width: 92vw;
		min-height: unset;
	}
	.brand-panel {
		padding: 40px 30px 30px;
		.brand-features { display: none; }
		.brand-tagline { margin-bottom: 0; }
	}
	.card-panel {
		padding: 40px 30px;
	}
}
</style>
