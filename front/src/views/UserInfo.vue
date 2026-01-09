<template>
  <div class="user-info-container">
    <el-card shadow="hover" class="info-card">
      <template #header>
        <div class="card-header">
          <span class="title">基本信息</span>
          <el-button type="success" plain size="small" @click="handleEdit">修改信息</el-button>
        </div>
      </template>

      <div class="profile-box">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" class="avatar-img" />
          <div class="avatar-tip">点击修改头像</div>
        </div>

        <div class="info-section">
          <el-row :gutter="40">
            <el-col :span="12">
              <div class="info-item">
                <span class="label">用户名</span>
                <div class="value-box">{{ userInfo.username }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">姓名</span>
                <div class="value-box">
                   {{ userInfo.nickname || '未填写' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">系统角色</span>
                <div class="value-box tag-style">{{ userInfo.role || '普通用户' }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">注册时间</span>
                <div class="value-box">{{ formatTime(userInfo.createTime) }}</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <el-divider border-style="dashed" />

      <div class="account-bind-section">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="登录相关账号" name="login">
            <div class="bind-list">
              <div class="bind-item">
                <div class="icon-wrapper user-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="bind-content">
                  <div class="bind-title">账号</div>
                  <div class="bind-desc">系统自动生成，可用于登录</div>
                </div>
                <div class="bind-status">{{ userInfo.username }}</div>
              </div>

              <div class="bind-item">
                <div class="icon-wrapper phone-icon">
                  <el-icon><Iphone /></el-icon>
                </div>
                <div class="bind-content">
                  <div class="bind-title">手机号</div>
                  <div class="bind-desc">绑定手机号，可用于登录、重置密码或其他安全验证</div>
                </div>
                <div class="bind-action">
                  <span v-if="userInfo.phone" class="binded-text">{{ hidePhone(userInfo.phone) }}</span>
                  <el-button v-else type="primary" link @click="handleEdit">去绑定</el-button>
                  <el-button v-if="userInfo.phone" type="success" plain size="small">修改</el-button>
                </div>
              </div>

              <div class="bind-item">
                <div class="icon-wrapper mail-icon">
                  <el-icon><Message /></el-icon>
                </div>
                <div class="bind-content">
                  <div class="bind-title">邮箱</div>
                  <div class="bind-desc">绑定邮箱，可用于登录、重置密码或其他安全验证</div>
                </div>
                <div class="bind-action">
                   <span v-if="userInfo.email" class="binded-text">{{ userInfo.email }}</span>
                   <el-button v-else type="primary" link @click="handleEdit">去绑定</el-button>
                   <el-button v-if="userInfo.email" type="success" plain size="small">绑定</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="交易相关账号" name="trade" disabled></el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑基本信息" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.nickname" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="success" @click="saveUserInfo">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Iphone, Message } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const activeTab = ref('login')
const dialogVisible = ref(false)

// 用户信息数据
const userInfo = ref({
  id: null,
  username: '',
  nickname: '',
  role: '',
  phone: '',
  email: '',
  createTime: '',
  avatar: ''
})

// 编辑表单数据
const editForm = reactive({
  id: null,
  nickname: '',
  phone: '',
  email: ''
})

// 1. 初始化加载数据
const fetchUserInfo = async () => {
  // 假设登录时把 username 存到了 localStorage，或者你从 store 里取
  // 实际开发中，通常直接调接口，后端从 token 解析我是谁
  const storedUser = JSON.parse(localStorage.getItem('user') || '{}')
  const username = storedUser.username || 'admin' // 临时兜底

  try {
    const res = await request.get('/user/info', {
      params: { username: username }
    })
    if (res.code === '200' || res.code === 200) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// 2. 打开编辑弹窗
const handleEdit = () => {
  // 把当前信息复制给表单
  editForm.id = userInfo.value.id
  editForm.nickname = userInfo.value.nickname
  editForm.phone = userInfo.value.phone
  editForm.email = userInfo.value.email
  dialogVisible.value = true
}

// 3. 保存修改
const saveUserInfo = async () => {
  try {
    const res = await request.post('/user/update', editForm)
    if (res.code === '200' || res.code === 200) {
      ElMessage.success('信息更新成功')
      dialogVisible.value = false
      fetchUserInfo() // 刷新页面数据
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error('系统繁忙')
  }
}

// 工具：手机号脱敏 (138****1234)
const hidePhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/^(\d{3})\d{4}(\d{4})$/, "$1****$2")
}

// 工具：格式化时间 (简单版，如果后端返回标准格式可省略)
const formatTime = (timeStr) => {
  if (!timeStr) return '暂无数据'
  return timeStr.replace('T', ' ')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
/* 容器与卡片 */
.user-info-container {
  padding: 20px;
  background-color: #f5f7fa; /* 浅灰底色 */
  min-height: 85vh;
}
.info-card {
  max-width: 1000px;
  margin: 0 auto;
  border-radius: 8px;
}

/* 标题区 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  border-left: 4px solid #27ae60; /* 绿色左边条 */
  padding-left: 10px;
}

/* 上部：头像与资料 */
.profile-box {
  display: flex;
  padding: 20px 0;
  align-items: flex-start;
}
.avatar-section {
  width: 150px;
  text-align: center;
  margin-right: 40px;
}
.avatar-img {
  border: 4px solid #e8f5e9; /* 浅绿边框 */
  cursor: pointer;
  transition: transform 0.3s;
}
.avatar-img:hover {
  transform: scale(1.05);
}
.avatar-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

.info-section {
  flex: 1;
}
.info-item {
  margin-bottom: 25px;
}
.label {
  display: block;
  font-size: 14px;
  color: #888;
  margin-bottom: 8px;
}
.value-box {
  font-size: 16px;
  color: #333;
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 4px;
  min-height: 20px;
  border: 1px solid #eee;
}
.tag-style {
  color: #27ae60;
  background: #e8f5e9;
  border-color: #c8e6c9;
  display: inline-block;
  font-weight: bold;
  font-size: 14px;
}

/* 底部：绑定列表 */
.account-bind-section {
  margin-top: 10px;
}
.bind-list {
  padding: 0 20px;
}
.bind-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}
.bind-item:last-child {
  border-bottom: none;
}
.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
  font-size: 20px;
}
.user-icon { background: #e3f2fd; color: #2196f3; }
.phone-icon { background: #e8f5e9; color: #4caf50; }
.mail-icon { background: #fff3e0; color: #ff9800; }

.bind-content {
  flex: 1;
}
.bind-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
}
.bind-desc {
  font-size: 13px;
  color: #999;
}
.bind-status, .binded-text {
  color: #666;
  font-size: 14px;
  margin-right: 15px;
}
.bind-action .el-button {
  font-size: 14px;
}

/* 修改 Element Tabs 样式以匹配 */
:deep(.el-tabs__item.is-active) {
  color: #27ae60;
}
:deep(.el-tabs__active-bar) {
  background-color: #27ae60;
}
</style>