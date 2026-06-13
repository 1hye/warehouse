/**
 * Axios HTTP客户端封装模块
 * 封装全局配置和响应拦截器
 */
import axios from 'axios'

/**
 * 创建Axios实例
 * 配置基础URL、请求超时时间和默认请求头
 */
const instance = axios.create({
  baseURL: '/api',           // API基础路径，Vite代理到后端服务器
  timeout: 10000,            // 请求超时时间10秒
  headers: {
    'Content-Type': 'application/json'  // 默认Content-Type
  }
})

/**
 * 响应拦截器
 * 统一处理响应数据和错误
 */
instance.interceptors.response.use(
  // 成功响应 - 直接返回response.data
  response => {
    return response.data
  },
  // 错误响应 - 记录错误并抛出
  error => {
    console.error('API 请求失败:', error)
    throw error
  }
)

// 导出axios实例供其他模块使用
export default instance