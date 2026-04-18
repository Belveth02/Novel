<template>
  <div class="comment-list">
    <!-- 评论标题 -->
    <div class="comment-header">
      <h3>评论</h3>
      <div class="comment-count" v-if="commentCount !== null">
        共 {{ commentCount }} 条评论
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-loading="loading" class="comments-container">
      <div v-if="comments.length === 0" class="empty-comments">
        <el-empty description="暂无评论">
          <span>成为第一个评论的人</span>
        </el-empty>
      </div>
      <div v-else class="comments-items">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-content">
            <div class="comment-header">
              <span class="user-info">
                <el-icon><User /></el-icon>
                用户 {{ comment.userId }}
              </span>
              <span class="comment-time">
                <el-icon><Clock /></el-icon>
                {{ formatDate(comment.createTime) }}
              </span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button
                v-if="canDelete(comment.userId)"
                type="danger"
                size="small"
                :loading="deleteLoading[comment.id]"
                @click="handleDeleteComment(comment.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="comments.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { getComments, deleteComment, getCommentCount } from '@/api/comment'
import type { CommentVO, PageResult } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Clock } from '@element-plus/icons-vue'

interface Props {
  novelId: number
}

const props = defineProps<Props>()

// 评论数据
const comments = ref<CommentVO[]>([])
const commentCount = ref<number | null>(null)
const total = ref(0)
const loading = ref(false)

// 删除加载状态
const deleteLoading = ref<Record<number, boolean>>({})

// 当前用户ID（临时）
const currentUserId = ref<number | null>(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 20
})

// 初始化当前用户ID
const initCurrentUserId = () => {
  // 从 localStorage 获取临时用户ID
  const tempUserId = localStorage.getItem('tempUserId')
  if (tempUserId) {
    // 临时用户ID是字符串，但后端需要数字，这里我们只用于前端判断
    // 实际用户ID由后端通过 X-User-ID 请求头获取
    // 这里我们无法获取实际用户ID，所以只能通过其他方式判断
    // 暂时不实现用户判断，删除按钮对所有评论显示
    // 后端会验证用户权限
    currentUserId.value = null
  }
}

// 判断是否可以删除评论
const canDelete = (commentUserId: number): boolean => {
  // 由于当前是临时用户系统，无法准确判断
  // 这里显示删除按钮，由后端验证权限
  return true
}

// 加载评论数量
const loadCommentCount = async () => {
  try {
    commentCount.value = await getCommentCount(props.novelId)
  } catch (err) {
    console.error('加载评论数量失败:', err)
    commentCount.value = null
  }
}

// 加载评论列表
const loadComments = async () => {
  if (!props.novelId) return

  loading.value = true
  try {
    const result: PageResult<CommentVO> = await getComments({
      novelId: props.novelId,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    })
    comments.value = result.records
    total.value = result.total
  } catch (err) {
    console.error('加载评论列表失败:', err)
    comments.value = []
    total.value = 0
    ElMessage.error('加载评论列表失败')
  } finally {
    loading.value = false
  }
}

// 删除评论
const handleDeleteComment = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    deleteLoading.value[commentId] = true
    const success = await deleteComment(commentId)

    if (success) {
      ElMessage.success('评论删除成功')
      // 重新加载列表和数量
      loadComments()
      loadCommentCount()
    } else {
      ElMessage.error('评论删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除评论失败:', err)
      ElMessage.error('删除评论失败')
    }
  } finally {
    deleteLoading.value[commentId] = false
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  loadComments()
}

const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  loadComments()
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 监听小说ID变化
watch(() => props.novelId, (newNovelId) => {
  if (newNovelId) {
    queryParams.pageNum = 1
    loadComments()
    loadCommentCount()
  }
}, { immediate: true })

// 初始化加载
onMounted(() => {
  initCurrentUserId()
  if (props.novelId) {
    loadComments()
    loadCommentCount()
  }
})

// 暴露方法供父组件调用
defineExpose({
  refresh: () => {
    loadComments()
    loadCommentCount()
  }
})
</script>

<style scoped>
.comment-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.comment-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.comment-count {
  font-size: 14px;
  color: #666;
  background: #f5f7fa;
  padding: 5px 15px;
  border-radius: 20px;
}

.comments-container {
  min-height: 200px;
}

.empty-comments {
  padding: 40px 0;
  text-align: center;
}

.comments-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 20px;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: none;
  padding-bottom: 0;
  margin-bottom: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.user-info .el-icon {
  color: #409eff;
}

.comment-time {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  margin: 10px 0;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .comment-header .comment-count {
    align-self: flex-start;
  }

  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>