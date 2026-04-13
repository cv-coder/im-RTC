const apiProxyTarget = process.env.VUE_APP_PROXY_TARGET || 'http://4.194.178.246'

module.exports = {
	devServer: {
		proxy: {
			'/api': {
				target: apiProxyTarget,
				changeOrigin: true,
				ws: false
			}
		}
	}

}
