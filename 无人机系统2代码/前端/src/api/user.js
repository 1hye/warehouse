/**
 * 用户认证API模块
 * 封装用户登录及相关操作的HTTP请求
 */
import axios from '../utils/axios'

/**
 * 用户登录
 * @param {Object} data - 登录信息，包含username和password
 * @returns {Promise} 返回登录成功用户信息
 */
export const login = (data) => {
  return axios.post('/auth/login', data)
}

/**
 * 获取用户信息
 * @param {number} id - 用户ID
 * @returns {Promise} 返回用户详细信息
 */
export const getUserInfo = (id) => {
  return axios.get(`/auth/user/${id}`)
}

/**
 * 保存Token到本地存储
 * @param {string} token - 认证Token
 */
export const setToken = (token) => {
  localStorage.setItem('token', token)
}

/**
 * 从本地存储获取Token
 * @returns {string|null} 返回Token或null
 */
export const getToken = () => {
  return localStorage.getItem('token')
}

/**
 * 清除本地存储的Token和用户信息
 */
export const removeToken = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

/**
 * 保存用户信息到本地存储
 * @param {Object} user - 用户信息对象
 */
export const setUser = (user) => {
  localStorage.setItem('user', JSON.stringify(user))
}

/**
 * 从本地存储获取用户信息
 * @returns {Object|null} 返回用户信息对象或null
 */
export const getUser = () => {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : null
}