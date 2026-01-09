<template>
  <div class="app-layout">
    <div class="layout-header">
      <div class="left">
        <span class="logo-text">碳中和资产管理平台</span>
      </div>
      
      <div class="right">
        <el-button 
          v-if="!isHome" 
          type="primary" 
          size="small" 
          @click="goHome"
        >
          <el-icon style="margin-right: 4px"><HomeFilled /></el-icon> 
          回到主页
        </el-button>
        </div>
    </div>

    <div class="layout-main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue' // 确保引入了图标

const router = useRouter()
const route = useRoute()

// 判断当前是否为主页 (根据你的实际路由调整路径)
const isHome = computed(() => {
  return route.path === '/home' || route.path === '/'
})

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.app-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  z-index: 100; /* 保证头部在最上层 */
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.layout-main {
  flex: 1; /* 占满剩余高度 */
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto; /* 内容过多时出现滚动条 */
}
</style>