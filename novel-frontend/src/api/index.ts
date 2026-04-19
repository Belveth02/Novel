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
      // 业务错误：code !== 200
      const errorMessage = data.message || '请求失败'
      ElMessage.error(errorMessage)
      return Promise.reject(new Error(errorMessage))
    }
  },
  (error) => {
    // 网络错误 vs HTTP 错误
    if (error.response) {
      // HTTP 错误（状态码非 2xx）
      const { status, data } = error.response
      switch (status) {
        case 400:
          ElMessage.error(data?.message || '请求参数错误')
          break
        case 401:
          // token 过期或未授权
          if (error.config.url?.startsWith('/admin')) {
            localStorage.removeItem('admin_token')
            localStorage.removeItem('admin_user')
            // 跳转到后台登录页
            setTimeout(() => {
              window.location.href = '/admin/login'
            }, 0)
          }
          ElMessage.error('登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('权限不足，拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误，请稍后再试')
          break
        default:
          ElMessage.error(`请求错误 ${status}`)
      }
    } else if (error.request) {
      // 网络错误（无响应）
      ElMessage.error('网络连接异常，请检查网络设置')
    } else {
      // 请求配置错误等其他错误
      ElMessage.error(error.message || '请求发送失败')
    }
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