<template>
  <div class="placeholder-page">
    <el-result
      icon="info"
      title="分类管理"
      sub-title="功能开发中，敬请期待"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/admin/dashboard')">
          返回仪表盘
        </el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.placeholder-page {
  padding: 100px 20px;
  text-align: center;
}
</style>