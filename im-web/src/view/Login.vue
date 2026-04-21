<template>
	<div class="login-view">
		<div class="login-container">
			<!-- 左侧品牌展示区 -->
			<div class="brand-section">
				<div class="brand-content">
					<div class="logo-box">
						<img src="../../public/logo.png" alt="logo" />
					</div>
					<h1 class="brand-name">LWF 协同通信平台</h1>
					<p class="brand-desc">安全 · 高效 · 稳定 · 可靠</p>
					<ul class="feature-list">
						<li><i class="el-icon-check"></i> 企业级即时通讯</li>
						<li><i class="el-icon-check"></i> 全端多端消息同步</li>
						<li><i class="el-icon-check"></i> 高清音视频会议</li>
					</ul>
				</div>
				<!-- 底部装饰图形 -->
				<div class="brand-bg-decor"></div>
			</div>

			<!-- 右侧登录表单区 -->
			<div class="form-section">
				<div class="form-header">
					<h2>账号登录</h2>
					<p>欢迎使用 LWF 协同通信平台</p>
				</div>

				<el-form
					class="login-form"
					:model="loginForm"
					:rules="rules"
					ref="loginForm"
					@keyup.enter.native="submitForm('loginForm')"
				>
					<el-form-item prop="userName">
						<el-input
							v-model="loginForm.userName"
							autocomplete="off"
							placeholder="请输入账号"
							prefix-icon="el-icon-user"
						></el-input>
					</el-form-item>

					<el-form-item prop="password">
						<el-input
							type="password"
							v-model="loginForm.password"
							autocomplete="off"
							placeholder="请输入密码"
							prefix-icon="el-icon-lock"
							show-password
						></el-input>
					</el-form-item>

					<div class="form-options">
						<el-checkbox v-model="rememberMe">记住账号</el-checkbox>
						<router-link to="/register" class="link-btn">注册账号</router-link>
					</div>

					<el-form-item>
						<el-button 
							type="primary" 
							class="submit-btn" 
							@click.prevent="submitForm('loginForm')" 
							:loading="isLoading"
						>
							{{ isLoading ? '正在登录...' : '登 录' }}
						</el-button>
					</el-form-item>
				</el-form>
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
		return {
			isLoading: false,
			rememberMe: false,
			loginForm: {
				terminal: this.$enums.TERMINAL_TYPE.WEB,
				userName: '',
				password: ''
			},
			rules: {
				userName: [{ required: true, message: '请输入账号', trigger: 'blur' }],
				password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
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
						if (this.rememberMe) {
							this.setCookie('username', this.loginForm.userName);
							this.setCookie('password', this.loginForm.password);
						} else {
							this.setCookie('username', '');
							this.setCookie('password', '');
						}
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
		getCookie(name) {
			let reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			let arr = document.cookie.match(reg);
			return arr ? unescape(arr[2]) : '';
		},
		setCookie(name, value) {
			let days = 7;
			let exp = new Date();
			exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
			document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
		}
	},
	mounted() {
		let savedUser = this.getCookie("username");
		if (savedUser) {
			this.loginForm.userName = savedUser;
			this.loginForm.password = this.getCookie("password");
			this.rememberMe = true;
		}
	}
}
</script>

<style scoped lang="scss">
/* 政企简约风配蓝白色调 */
$primary-color: #1890ff;
$primary-hover: #40a9ff;
$bg-color: #f0f2f5;
$text-main: #333333;
$text-secondary: #666666;
$text-muted: #999999;
$border-color: #dcdfe6;

.login-view {
	width: 100vw;
	height: 100vh;
	background-color: $bg-color;
	background-image: url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%231890ff" fill-opacity="0.05"%3E%3Cpath d="M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.login-container {
	display: flex;
	width: 900px;
	height: 500px;
	background: #ffffff;
	border-radius: 8px;
	box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
	overflow: hidden;
	z-index: 10;
}

/* 左侧品牌区 */
.brand-section {
	flex: 1;
	background: linear-gradient(135deg, #0052d9 0%, $primary-color 100%);
	color: #ffffff;
	position: relative;
	padding: 50px 40px;
	display: flex;
	flex-direction: column;
	justify-content: center;
	overflow: hidden;
}

.brand-content {
	position: relative;
	z-index: 2;
}

.logo-box {
	margin-bottom: 24px;
	img {
		width: 54px;
		height: 54px;
		background: #ffffff;
		border-radius: 8px;
		padding: 8px;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	}
}

.brand-name {
	font-size: 28px;
	font-weight: 600;
	margin: 0 0 12px;
	letter-spacing: 1px;
}

.brand-desc {
	font-size: 16px;
	opacity: 0.9;
	margin: 0 0 40px;
	letter-spacing: 2px;
}

.feature-list {
	list-style: none;
	padding: 0;
	margin: 0;
	
	li {
		font-size: 15px;
		margin-bottom: 16px;
		display: flex;
		align-items: center;
		opacity: 0.85;

		i {
			margin-right: 10px;
			font-size: 18px;
			font-weight: bold;
		}
	}
}

.brand-bg-decor {
	position: absolute;
	right: -50px;
	bottom: -50px;
	width: 300px;
	height: 300px;
	background: rgba(255, 255, 255, 0.1);
	border-radius: 50%;
	z-index: 1;

	&::after {
		content: '';
		position: absolute;
		top: -40px;
		left: -40px;
		width: 200px;
		height: 200px;
		background: rgba(255, 255, 255, 0.05);
		border-radius: 50%;
	}
}

/* 右侧表单区 */
.form-section {
	width: 420px;
	padding: 50px;
	display: flex;
	flex-direction: column;
	justify-content: center;
	background: #ffffff;
}

.form-header {
	margin-bottom: 35px;
	
	h2 {
		font-size: 24px;
		color: $text-main;
		margin: 0 0 8px;
		font-weight: 500;
	}
	
	p {
		font-size: 14px;
		color: $text-muted;
		margin: 0;
	}
}

.login-form {
	::v-deep .el-input__inner {
		height: 44px;
		line-height: 44px;
		border-radius: 4px;
		border: 1px solid $border-color;
		transition: border-color 0.2s;
		
		&:focus {
			border-color: $primary-color;
		}
	}

	::v-deep .el-input__prefix {
		left: 10px;
		i {
			line-height: 44px;
			font-size: 16px;
		}
	}
}

.form-options {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 25px;
	margin-top: -10px;

	::v-deep .el-checkbox__label {
		color: $text-secondary;
	}
}

.link-btn {
	color: $primary-color;
	text-decoration: none;
	font-size: 14px;
	transition: color 0.2s;

	&:hover {
		color: $primary-hover;
	}
}

.submit-btn {
	width: 100%;
	height: 44px;
	font-size: 16px;
	border-radius: 4px;
	background-color: $primary-color;
	border-color: $primary-color;
	transition: all 0.2s;
	letter-spacing: 4px;

	&:hover, &:focus {
		background-color: $primary-hover;
		border-color: $primary-hover;
	}
}

/* 备案信息覆盖 */
::v-deep .icp {
	position: absolute;
	bottom: 20px;
	width: 100%;
	text-align: center;
	color: $text-muted !important;
	font-size: 12px;
	a {
		color: $text-muted !important;
		&:hover {
			color: $primary-color !important;
		}
	}
}

/* 响应式调整 */
@media (max-width: 950px) {
	.login-container {
		width: 90%;
		max-width: 420px;
		flex-direction: column;
		height: auto;
	}
	.brand-section {
		display: none; // 移动端隐藏左侧
	}
	.form-section {
		width: 100%;
		padding: 40px 30px;
	}
}
</style>
