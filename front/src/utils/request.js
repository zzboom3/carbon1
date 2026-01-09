import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端接口前缀
  timeout: 5000, // 请求超时时间
  withCredentials: true // 允许携带Cookie（跨域时需要）
})

// 请求拦截器：统一添加请求头（如token，登录注册暂时用不到）
request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理响应结果
request.interceptors.response.use(
  response => {
    const res = response.data
    // 非200状态码，统一抛出错误
    if (res.code !== 200) {
      // 提示错误信息（Element Plus的ElMessage）
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '服务器错误')
    return Promise.reject(error)
  }
)

export default request