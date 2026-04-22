<template>
  <div class="user-profile-page">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/" class="logo">小说阅读</router-link>
          <nav class="nav-menu">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/novels" class="nav-item">小说列表</router-link>
          </nav>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" :src="getFullAvatarUrl(userStore.avatar) || undefined">
                {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.nickname || userStore.username }}</span>
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="favorites">
                  <el-icon><star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <el-icon><user /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><switch-button /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="profile-main">
      <div class="profile-container">
        <!-- 左侧菜单 -->
        <aside class="profile-sidebar">
          <div class="user-brief">
            <el-avatar :size="80" :src="getFullAvatarUrl(userStore.avatar) || undefined">
              {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
            </el-avatar>
            <h3 class="user-name">{{ userStore.nickname || userStore.username }}</h3>
            <p class="user-role">{{ userStore.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
          </div>
          <el-menu
            :default-active="activeTab"
            class="profile-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="info">
              <el-icon><user /></el-icon>
              <span>基本信息</span>
            </el-menu-item>
            <el-menu-item index="password">
              <el-icon><lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
            <el-menu-item index="comments">
              <el-icon><chat-dot-round /></el-icon>
              <span>我的评论</span>
            </el-menu-item>
            <el-menu-item index="history">
              <el-icon><clock /></el-icon>
              <span>阅读历史</span>
            </el-menu-item>
          </el-menu>
        </aside>

        <!-- 右侧内容 -->
        <div class="profile-content">
          <!-- 基本信息 -->
          <div v-if="activeTab === 'info'" class="content-section">
            <h2 class="section-title">基本信息</h2>
            <el-form
              ref="infoFormRef"
              :model="userForm"
              :rules="userRules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="头像">
                <div class="avatar-upload">
                  <el-avatar :size="100" :src="getFullAvatarUrl(userForm.avatar) || undefined">
                    {{ userForm.nickname?.charAt(0) || userForm.username?.charAt(0) || 'U' }}
                  </el-avatar>
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :auto-upload="false"
                    :show-file-list="false"
                    :on-change="handleAvatarChange"
                    accept="image/*"
                  >
                    <el-button type="primary" size="small">更换头像</el-button>
                  </el-upload>
                </div>
              </el-form-item>

              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled />
              </el-form-item>

              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userForm.phone" placeholder="请输入手机号" maxlength="11" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="saveUserInfo" :loading="saving">保存修改</el-button>
                <el-button @click="resetUserInfo">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 修改密码 -->
          <div v-if="activeTab === 'password'" class="content-section">
            <h2 class="section-title">修改密码</h2>
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="password-form"
            >
              <el-form-item label="原密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入原密码"
                  show-password
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（6-20位）"
                  show-password
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="changeUserPassword" :loading="changingPassword">确认修改</el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 我的评论 -->
          <div v-if="activeTab === 'comments'" class="content-section">
            <h2 class="section-title">我的评论</h2>
            <div v-loading="loadingComments" class="comments-list">
              <el-empty v-if="comments.length === 0" description="暂无评论" />
              <div v-else>
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <div class="comment-header">
                    <span class="novel-name">《{{ comment.novelTitle || '未知小说' }}》</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-content">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <el-button type="danger" link size="small" @click="deleteCommentHandler(comment.id)">
                      <el-icon><delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
                <div class="pagination">
                  <el-pagination
                    v-model:current-page="commentPage"
                    v-model:page-size="commentSize"
                    :total="commentTotal"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next"
                    @size-change="loadComments"
                    @current-change="loadComments"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 阅读历史 -->
          <div v-if="activeTab === 'history'" class="content-section">
            <h2 class="section-title">阅读历史</h2>
            <div v-loading="loadingHistory" class="history-list">
              <el-empty v-if="readingHistory.length === 0" description="暂无阅读记录" />
              <div v-else>
                <div v-for="item in readingHistory" :key="item.novelId" class="history-item" @click="goToNovel(item.novelId)">
                  <el-image
                    :src="getFullCoverUrl(item.novelCoverImage) || 'https://via.placeholder.com/120x160?text=封面'"
                    fit="cover"
                    class="history-cover"
                  />
                  <div class="history-info">
                    <h4 class="history-title">{{ item.novelTitle }}</h4>
                    <p class="history-chapter">读至：{{ item.chapterTitle }}</p>
                    <p class="history-time">上次阅读：{{ formatTime(item.lastReadTime) }}</p>
                    <el-button type="primary" size="small" @click.stop="continueReading(item)">继续阅读</el-button>
                  </div>
                </div>
                <div class="pagination">
                  <el-pagination
                    v-model:current-page="historyPage"
                    v-model:page-size="historySize"
                    :total="historyTotal"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next"
                    @size-change="loadReadingHistory"
                    @current-change="loadReadingHistory"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部 -->
    <footer class="app-footer">
      <p>小说阅读网站 &copy; 2024</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { ArrowDown, Star, User, SwitchButton, Lock, ChatDotRound, Delete, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getCurrentUser, updateUserInfo, changePassword, uploadAvatar, getMyComments } from '@/api/user'
import { deleteComment } from '@/api/comment'
import { getReadingHistory } from '@/api/readingHistory'
import type { CommentVO, ReadingHistoryVO } from '@/types'

// 获取完整头像 URL
const getFullAvatarUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整 URL: http://localhost:8080/api + /uploads/avatars/xxx.jpg
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
}

// 获取完整封面 URL
const getFullCoverUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整 URL: http://localhost:8080/api + /uploads/covers/xxx.jpg
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
}

const router = useRouter()
const userStore = useUserStore()

// 当前激活的标签
const activeTab = ref('info')

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  activeTab.value = index
  if (index === 'comments') {
    loadComments()
  } else if (index === 'history') {
    loadReadingHistory()
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

// ============ 基本信息 ============
const infoFormRef = ref<FormInstance>()
const saving = ref(false)
const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const userRules: FormRules = {
  nickname: [
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userInfo = await getCurrentUser()
    Object.assign(userForm, userInfo)
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 使用store中的信息作为后备
    userForm.username = userStore.username
    userForm.nickname = userStore.nickname
    userForm.email = userStore.email
    userForm.phone = userStore.phone
    userForm.avatar = userStore.avatar
  }
}

// 处理头像变更
const handleAvatarChange = async (file: UploadFile) => {
  if (!file.raw) return

  // 验证文件类型
  const isImage = file.raw.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return
  }

  // 验证文件大小（限制为2MB）
  const isLt2M = file.raw.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  try {
    const avatarUrl = await uploadAvatar(file.raw)
    userForm.avatar = avatarUrl
    // 立即更新 store 中的头像，使页面其他地方的 avatar 也更新
    userStore.updateAvatar(avatarUrl)
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

// 保存用户信息
const saveUserInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      const updatedUser = await updateUserInfo({
        nickname: userForm.nickname,
        email: userForm.email,
        phone: userForm.phone,
        avatar: userForm.avatar
      })

      // 更新store中的信息
      userStore.setUserInfo({
        userId: userStore.userId!,
        username: updatedUser.username,
        nickname: updatedUser.nickname,
        token: userStore.token,
        role: updatedUser.role,
        email: updatedUser.email,
        phone: updatedUser.phone,
        avatar: updatedUser.avatar
      })

      ElMessage.success('保存成功')
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      saving.value = false
    }
  })
}

// 重置用户信息
const resetUserInfo = () => {
  loadUserInfo()
}

// ============ 修改密码 ============
const passwordFormRef = ref<FormInstance>()
const changingPassword = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: Function) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 修改密码
const changeUserPassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true
    try {
      await changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      // 退出登录
      userStore.logout()
      router.push('/login')
    } catch (error) {
      console.error('修改密码失败:', error)
    } finally {
      changingPassword.value = false
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// ============ 我的评论 ============
const loadingComments = ref(false)
const comments = ref<CommentVO[]>([])
const commentPage = ref(1)
const commentSize = ref(10)
const commentTotal = ref(0)

const loadComments = async () => {
  if (activeTab.value !== 'comments') return
  loadingComments.value = true
  try {
    const result = await getMyComments({
      page: commentPage.value,
      size: commentSize.value
    })
    comments.value = result.records
    commentTotal.value = result.total
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loadingComments.value = false
  }
}

// ============ 阅读历史 ============
const loadingHistory = ref(false)
const readingHistory = ref<ReadingHistoryVO[]>([])
const historyPage = ref(1)
const historySize = ref(10)
const historyTotal = ref(0)

const loadReadingHistory = async () => {
  if (activeTab.value !== 'history') return
  loadingHistory.value = true
  try {
    const result = await getReadingHistory({
      pageNum: historyPage.value,
      pageSize: historySize.value
    })
    // 按小说去重，只保留每本小说的最新记录
    const novelMap = new Map<number, ReadingHistoryVO>()
    result.records.forEach((item: ReadingHistoryVO) => {
      const existing = novelMap.get(item.novelId)
      if (!existing || new Date(item.lastReadTime) > new Date(existing.lastReadTime)) {
        novelMap.set(item.novelId, item)
      }
    })
    readingHistory.value = Array.from(novelMap.values())
    historyTotal.value = result.total
  } catch (error) {
    console.error('加载阅读历史失败:', error)
  } finally {
    loadingHistory.value = false
  }
}

// 跳转到小说详情
const goToNovel = (novelId: number) => {
  router.push(`/novels/${novelId}`)
}

// 继续阅读
const continueReading = (item: ReadingHistoryVO) => {
  router.push(`/chapters/${item.chapterId}`)
}

// 删除评论
const deleteCommentHandler = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteComment(commentId)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 初始化
onMounted(() => {
  userStore.restoreFromStorage()
  loadUserInfo()
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.app-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #333;
  text-decoration: none;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.username {
  color: #333;
  font-size: 14px;
}

/* 主要内容 */
.profile-main {
  flex: 1;
  background: #f5f7fa;
  padding: 20px 0;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}

/* 左侧侧边栏 */
.profile-sidebar {
  width: 260px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.user-brief {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.user-name {
  margin: 10px 0 5px;
  font-size: 18px;
  color: #333;
}

.user-role {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.profile-menu {
  border: none;
}

.profile-menu :deep(.el-menu-item) {
  border-radius: 4px;
  margin-bottom: 5px;
}

.profile-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 右侧内容 */
.profile-content {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 500px;
}

.content-section {
  max-width: 600px;
}

.section-title {
  margin: 0 0 30px 0;
  font-size: 20px;
  color: #333;
  padding-bottom: 15px;
  border-bottom: 2px solid #409eff;
}

/* 头像上传 */
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 表单样式 */
.info-form,
.password-form {
  margin-top: 20px;
}

/* 评论列表 */
.comments-list {
  margin-top: 20px;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.novel-name {
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
}

.novel-name:hover {
  text-decoration: underline;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin: 0 0 10px 0;
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

/* 阅读历史 */
.history-list {
  margin-top: 20px;
}

.history-item {
  display: flex;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  background: #ecf5ff;
  transform: translateX(5px);
}

.history-cover {
  width: 120px;
  height: 160px;
  border-radius: 4px;
  margin-right: 20px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.history-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.history-chapter {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.history-time {
  margin: 0 0 15px 0;
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 底部 */
.app-footer {
  background: #333;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.app-footer p {
  margin: 0;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
  }

  .user-brief {
    display: flex;
    align-items: center;
    gap: 15px;
    text-align: left;
    padding-bottom: 15px;
  }

  .user-brief .el-avatar {
    flex-shrink: 0;
  }

  .profile-content {
    padding: 20px;
  }

  .content-section {
    max-width: 100%;
  }
}
</style>
