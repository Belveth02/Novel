<template>
  <div class="comment-form">
    <h3>发表评论</h3>
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评论内容（最多500字）"
            maxlength="500"
            show-word-limit
            resize="none"
          />
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            发布评论
          </el-button>
          <el-button @click="handleReset">
            清空
          </el-button>
          <span class="char-count">
            {{ form.content.length }}/500
          </span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { addComment } from '@/api/comment'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

interface Props {
  novelId: number
}

interface Emits {
  (e: 'comment-added'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  content: ''
})

// 提交状态
const submitting = ref(false)

// 表单验证规则
const rules = reactive<FormRules>({
  content: [
    { required: true, message: '评论内容不能为空', trigger: 'blur' },
    { min: 1, max: 500, message: '评论内容长度在1到500个字符之间', trigger: 'blur' }
  ]
})

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return

  // 验证表单
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
  } catch (err) {
    console.error('表单验证失败:', err)
    return
  }

  submitting.value = true
  try {
    await addComment(props.novelId, form.content)
    ElMessage.success('评论发布成功')

    // 清空表单
    handleReset()

    // 触发事件通知父组件刷新评论列表
    emit('comment-added')
  } catch (err: any) {
    console.error('评论发布失败:', err)
    ElMessage.error(err.message || '评论发布失败')
  } finally {
    submitting.value = false
  }
}

// 处理重置
const handleReset = () => {
  form.content = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}
</script>

<style scoped>
.comment-form {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-form h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.form-container {
  margin-top: 10px;
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 0;
}

.char-count {
  margin-left: auto;
  font-size: 14px;
  color: #666;
  background: #f5f7fa;
  padding: 5px 10px;
  border-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .char-count {
    margin-left: 0;
    text-align: center;
    margin-top: 10px;
  }
}
</style>