<template>
  <div class="author-login-page">
    <div class="login-container">
      <div class="login-header">
        <h2>作者后台</h2>
        <p>作者登录</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>
          还没有作者账号？
          <router-link to="/register" class="register-link">立即注册</router-link>
        </p>
        <p>
          <router-link to="/login" class="back-link">返回用户登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { post } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const loginFormRef = ref<FormInstance>()

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

interface AuthorLoginResponse {
  token: string
  id: number
  username: string
  nickname: string
  role: string
  email: string
  phone: string
  avatar: string
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const result = await post<AuthorLoginResponse>('/author/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })

    // 保存token和用户信息（作者类型）
    localStorage.setItem('author_token', result.token)
    localStorage.setItem('author_info', JSON.stringify({
      id: result.id,
      userId: result.id,
      username: result.username,
      nickname: result.nickname,
      role: result.role,
      email: result.email,
      phone: result.phone,
      avatar: result.avatar
    }))

    ElMessage.success('登录成功')
    // 跳转到作者后台首页
    router.push('/author/dashboard')
  } catch (error: any) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.author-login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 20px;
}

.login-container {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 28px;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.login-footer p {
  margin: 8px 0;
}

.register-link,
.back-link {
  color: #409eff;
  text-decoration: none;
}

.register-link:hover,
.back-link:hover {
  text-decoration: underline;
}
</style>
