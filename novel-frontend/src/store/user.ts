import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface UserInfo {
  userId: number
  username: string
  nickname: string
  role: string
  email?: string
  phone?: string
  avatar?: string
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const userId = ref<number | null>(null)
  const username = ref<string>('')
  const nickname = ref<string>('')
  const role = ref<string>('')
  const email = ref<string>('')
  const phone = ref<string>('')
  const avatar = ref<string>('')
  const token = ref<string>('')

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 设置用户信息
  function setUserInfo(info: {
    userId: number
    username: string
    nickname: string
    token: string
    role?: string
    email?: string
    phone?: string
    avatar?: string
  }) {
    userId.value = info.userId
    username.value = info.username
    nickname.value = info.nickname
    role.value = info.role || 'USER'
    email.value = info.email || ''
    phone.value = info.phone || ''
    avatar.value = info.avatar || ''
    token.value = info.token
  }

  // 从localStorage恢复用户信息
  function restoreFromStorage() {
    const storedToken = localStorage.getItem('user_token')
    const storedUserInfo = localStorage.getItem('user_info')

    if (storedToken && storedUserInfo) {
      try {
        const userInfo: UserInfo = JSON.parse(storedUserInfo)
        token.value = storedToken
        userId.value = userInfo.userId
        username.value = userInfo.username
        nickname.value = userInfo.nickname
        role.value = userInfo.role || 'USER'
        email.value = userInfo.email || ''
        phone.value = userInfo.phone || ''
        avatar.value = userInfo.avatar || ''
      } catch (e) {
        console.error('恢复用户信息失败:', e)
        logout()
      }
    }
  }

  // 退出登录
  function logout() {
    userId.value = null
    username.value = ''
    nickname.value = ''
    role.value = ''
    email.value = ''
    phone.value = ''
    avatar.value = ''
    token.value = ''

    localStorage.removeItem('user_token')
    localStorage.removeItem('user_info')
  }

  return {
    // 状态
    userId,
    username,
    nickname,
    role,
    email,
    phone,
    avatar,
    token,
    // 计算属性
    isLoggedIn,
    // 方法
    setUserInfo,
    restoreFromStorage,
    logout
  }
})

/**
 * 检查是否已登录（供router使用）
 */
export const isLoggedIn = (): boolean => {
  const token = localStorage.getItem('user_token')
  return !!token
}