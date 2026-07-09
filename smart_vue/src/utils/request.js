import axios from 'axios'
import router from "@/router";

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8082',
    timeout: 5000
})

// request 拦截器
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
    if (user) {
        config.headers['token'] = user.token;
    }
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (response.config.responseType === 'blob') {
            return res
        }
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === '401') {
            router.push("/login")
        }
        return res;
    },
    error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

// 🔥 创建 AI 专用请求（保持与 request 相同的拦截器逻辑）
const aiAxios = axios.create({
    baseURL: 'http://localhost:8087',
    timeout: 60000
})

// AI 请求拦截器
aiAxios.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
    if (user) {
        config.headers['token'] = user.token;
    }
    return config
}, error => {
    return Promise.reject(error)
});

// AI 响应拦截器
aiAxios.interceptors.response.use(
    response => {
        let res = response.data;
        if (response.config.responseType === 'blob') {
            return res
        }
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === '401') {
            router.push("/login")
        }
        return res;
    },
    error => {
        console.log('AI请求错误：', error)
        return Promise.reject(error)
    }
)

// 🔥 导出：默认导出业务请求，命名导出 AI 请求
export default request
export { aiAxios }