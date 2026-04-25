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

  // 设置token
  function setToken(newToken: string, tokenType: 'user' | 'author' | 'admin' = 'user') {
    token.value = newToken
    const storageKey = `${tokenType}_token`
    localStorage.setItem(storageKey, newToken)
  }

  // 设置用户信息
  function setUserInfo(info: {
    id?: number
    userId?: number
    username: string
    nickname: string
    role?: string
    email?: string
    phone?: string
    avatar?: string
  }, tokenType: 'user' | 'author' | 'admin' = 'user') {
    userId.value = info.id || info.userId || null
    username.value = info.username
    nickname.value = info.nickname
    role.value = info.role || 'USER'
    email.value = info.email || ''
    phone.value = info.phone || ''
    avatar.value = info.avatar || ''

    // 保存到localStorage
    const userInfo: UserInfo = {
      userId: info.id || info.userId || 0,
      username: info.username,
      nickname: info.nickname,
      role: info.role || 'USER',
      email: info.email,
      phone: info.phone,
      avatar: info.avatar
    }
    localStorage.setItem(`${tokenType}_info`, JSON.stringify(userInfo))
  }

  // 清除用户信息
  function clearUserInfo() {
    userId.value = null
    username.value = ''
    nickname.value = ''
    role.value = ''
    email.value = ''
    phone.value = ''
    avatar.value = ''
    token.value = ''
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

  // 更新头像
  function updateAvatar(newAvatar: string) {
    avatar.value = newAvatar
    // 同时更新 localStorage 中的用户信息
    const storedUserInfo = localStorage.getItem('user_info')
    if (storedUserInfo) {
      try {
        const userInfo = JSON.parse(storedUserInfo)
        userInfo.avatar = newAvatar
        localStorage.setItem('user_info', JSON.stringify(userInfo))
      } catch (e) {
        console.error('更新本地头像失败:', e)
      }
    }
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
    setToken,
    setUserInfo,
    clearUserInfo,
    restoreFromStorage,
    logout,
    updateAvatar
  }
})

/**
 * 检查是否已登录（供router使用）
 */
export const isLoggedIn = (): boolean => {
  const token = localStorage.getItem('user_token')
  return !!token
}