import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import router from './router'
import * as echarts from 'echarts'

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.prototype.$echarts = echarts

const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 15000
})

http.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = 'Bearer ' + token
        }
        return config
    },
    error => Promise.reject(error)
)

http.interceptors.response.use(
    response => response.data,
    error => {
        if (error.response) {
            const { status, data } = error.response
            if (status === 401) {
                localStorage.clear()
                router.push('/login')
                Vue.prototype.$message.error('登录已过期，请重新登录')
            } else if (status === 403) {
                Vue.prototype.$message.error(data?.msg || '权限不足')
            } else {
                Vue.prototype.$message.error(data?.msg || `请求失败 (${status})`)
            }
        } else if (error.request) {
            Vue.prototype.$message.error('网络连接异常')
        } else {
            Vue.prototype.$message.error('请求配置错误')
        }
        return Promise.reject(error)
    }
)

Vue.prototype.$http = http

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')