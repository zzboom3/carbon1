<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-left">
        <div class="left-content">
          <h2 class="platform-title">加入绿色行动</h2>
          <p class="platform-desc">注册成为碳中和资产管理平台的一员<br>共同守护地球家园</p>
          <img 
            src="https://img.freepik.com/free-vector/save-earth-concept-illustration_114360-1234.jpg?t=st=1710000000" 
            alt="Register Illustration" 
            class="illustration"
          />
        </div>
      </div>

      <div class="register-right">
        <div class="form-wrapper">
          <h3 class="welcome-title">创建新账号</h3>
          <p class="welcome-subtitle">请填写以下信息完成注册</p>

          <form @submit.prevent="handleRegister">
            
            <div class="input-group">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
              </span>
              <input 
                type="text" 
                v-model="registerForm.username" 
                placeholder="请输入用户名 (3-20位)" 
                required 
              />
            </div>

            <div class="input-group">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
              </span>
              <input 
                type="password" 
                v-model="registerForm.password" 
                placeholder="设置密码 (6-20位)" 
                required 
              />
            </div>

            <div class="input-group">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"></path></svg>
              </span>
              <input 
                type="password" 
                v-model="registerForm.confirmPassword" 
                placeholder="请再次确认密码" 
                required 
              />
            </div>

            <button type="submit" class="register-btn" :disabled="isLoading">
              {{ isLoading ? '注册中...' : '立即注册' }}
            </button>

            <div class="login-link">
              已有账号? <router-link to="/login">去登录</router-link>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request' // 确保路径正确

const router = useRouter()
const isLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  // === 1. 手动校验逻辑 (替代 Element Plus 的 rules) ===
  const { username, password, confirmPassword } = registerForm

  if (!username || username.length < 3 || username.length > 20) {
    ElMessage.warning('用户名长度需在 3-20 位之间')
    return
  }
  if (!password || password.length < 6 || password.length > 20) {
    ElMessage.warning('密码长度需在 6-20 位之间')
    return
  }
  if (password !== confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  isLoading.value = true

  try {
    // === 2. 发送请求 ===
    // 注意：这里只传后端需要的字段，不传 confirmPassword
    const res = await request.post('/user/register', {
      username: username,
      password: password,
      // 如果你的后端 RegisterDTO 还需要 nickname，可以在这里加上
      // nickname: '新用户' 
    })

    if (res.code === 200 || res.code === '200') {
      ElMessage.success('注册成功，请登录')
      // 延迟一点点跳转，让用户看清提示
      setTimeout(() => {
        router.push('/login')
      }, 1000)
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error('系统繁忙，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* =========================================
   样式复用 Login.vue 的风格，配色微调
   ========================================= */

.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); /* 稍微比登录页更绿一点 */
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.register-box {
  display: flex;
  width: 900px;
  height: 550px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 左侧区域：使用稍不同的渐变色 */
.register-left {
  flex: 1;
  background: linear-gradient(135deg, #27ae60 0%, #16a085 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  padding: 40px;
}

.left-content { text-align: center; }
.platform-title { font-size: 28px; font-weight: bold; margin-bottom: 10px; letter-spacing: 1px; }
.platform-desc { font-size: 14px; opacity: 0.9; margin-bottom: 30px; line-height: 1.6; }
.illustration { width: 80%; max-width: 300px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }

/* 右侧区域 */
.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-wrapper { width: 100%; max-width: 320px; }
.welcome-title { font-size: 26px; color: #333; margin-bottom: 5px; }
.welcome-subtitle { font-size: 14px; color: #999; margin-bottom: 25px; }

/* 输入框组 */
.input-group { position: relative; margin-bottom: 20px; }
.input-group .icon {
  position: absolute; left: 15px; top: 50%; transform: translateY(-50%);
  color: #aaa; display: flex;
}

.input-group input {
  width: 100%; padding: 12px 15px 12px 45px;
  border: 1px solid #eee; border-radius: 8px; background: #f9f9f9;
  font-size: 14px; transition: 0.3s; box-sizing: border-box;
}

/* 聚焦效果：改为绿色系 */
.input-group input:focus {
  border-color: #27ae60; background: #fff;
  box-shadow: 0 0 0 4px rgba(39, 174, 96, 0.1); outline: none;
}

/* 注册按钮：绿色渐变 */
.register-btn {
  width: 100%; padding: 12px; border: none; border-radius: 8px;
  background: linear-gradient(90deg, #27ae60, #2ecc71);
  color: white; font-size: 16px; font-weight: bold; cursor: pointer;
  margin-top: 10px; transition: 0.2s;
}
.register-btn:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(39, 174, 96, 0.4); }
.register-btn:disabled { background: #ccc; cursor: not-allowed; }

/* 底部链接 */
.login-link { margin-top: 20px; text-align: center; font-size: 14px; color: #666; }
.login-link a { color: #27ae60; text-decoration: none; font-weight: bold; margin-left: 5px; }
.login-link a:hover { text-decoration: underline; }
</style>