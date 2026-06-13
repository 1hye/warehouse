/**
 * 无人机管理API模块
 * 封装无人机相关的HTTP请求
 */
import axios from '../utils/axios'

/**
 * 获取无人机列表（分页+条件查询）
 * @param {Object} params - 查询参数，包含pageNum、pageSize、keyword等
 * @returns {Promise} 返回分页后的无人机列表
 */
export const getDrones = (params) => {
  return axios.get('/drone', { params })
}

/**
 * 根据ID获取无人机详情
 * @param {number} id - 无人机ID
 * @returns {Promise} 返回无人机详情
 */
export const getDroneById = (id) => {
  return axios.get(`/drone/${id}`)
}

/**
 * 新增无人机
 * @param {Object} data - 无人机信息对象
 * @returns {Promise} 返回创建成功的无人机信息
 */
export const addDrone = (data) => {
  return axios.post('/drone', data)
}

/**
 * 更新无人机信息
 * @param {number} id - 无人机ID
 * @param {Object} data - 新的无人机信息
 * @returns {Promise} 返回更新后的无人机信息
 */
export const updateDrone = (id, data) => {
  return axios.put(`/drone/${id}`, data)
}

/**
 * 删除无人机
 * @param {number} id - 无人机ID
 * @returns {Promise} 返回删除操作结果
 */
export const deleteDrone = (id) => {
  return axios.delete(`/drone/${id}`)
}