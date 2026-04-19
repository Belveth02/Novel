import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { Result } from '@/types'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加临时用户ID请求头
    const tempUserId = localStorage.getItem('tempUserId')
    if (tempUserId) {
      config.headers['X-User-ID'] = tempUserId
    }

    // 为管理员接口添加Authorization头
    if (config.url?.startsWith('/admin')) {
      const adminToken = localStorage.getItem('admin_token')
      if (adminToken) {
        config.headers.Authorization = `Bearer ${adminToken}`
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const { data } = response
    // 根据后端 Result 结构判断请求是否成功
    if (data.code === 200) {
      return data.data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    let message = '请求错误'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          // 如果是管理员接口，跳转到后台登录页
          if (error.config.url?.startsWith('/admin')) {
            localStorage.removeItem('admin_token')
            localStorage.removeItem('admin_user')
            // 使用setTimeout避免在当前请求上下文中直接跳转
            setTimeout(() => {
              window.location.href = '/admin/login'
            }, 0)
          }
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求错误 ${error.response.status}`
      }
    } else if (error.request) {
      message = '网络连接错误，请检查网络'
    } else {
      message = error.message
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 导出常用的请求方法
export const get = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return request.get(url, config)
}

export const post = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return request.post(url, data, config)
}

export const put = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return request.put(url, data, config)
}

export const del = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return request.delete(url, config)
}

export default request