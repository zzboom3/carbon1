<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="left-content">
          <h2 class="platform-title">碳中和资产管理平台</h2>
          <p class="platform-desc">智慧低碳 · 绿色未来 · 高效管理</p>
          <img 
            src="https://img.freepik.com/free-vector/eco-conscious-concept-illustration_114360-4665.jpg?t=st=1710000000~exp=1710003600~hmac=xyz" 
            alt="Carbon Illustration" 
            class="illustration"
          />
        </div>
      </div>

      <div class="login-right">
        <div class="form-wrapper">
          <h3 class="welcome-title">欢迎登录</h3>
          <p class="welcome-subtitle">请输入您的账号信息以继续</p>

          <form @submit.prevent="handleLogin">
            <div class="input-group">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
              </span>
              <input 
                type="text" 
                v-model="loginForm.username" 
                placeholder="请输入用户名" 
                required
              />
            </div>

            <div class="input-group">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
              </span>
              <input 
                type="password" 
                v-model="loginForm.password" 
                placeholder="请输入密码" 
                required
              />
            </div>

            <div class="actions">
              <label class="checkbox-container">
                <input type="checkbox" v-model="loginForm.rememberMe">
                <span class="checkmark"></span>
                记住我
              </label>
              <a href="#" class="forgot-pwd">忘记密码?</a>
            </div>

            <button type="submit" class="login-btn" :disabled="isLoading">
              {{ isLoading ? '登录中...' : '立即登录' }}
            </button>

            <div class="register-link">
              还没有账号? <router-link to="/register">立即注册</router-link>
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
import { ElMessage } from 'element-plus' // 引入 Element Plus 的消息提示
import request from '@/utils/request'      // 引入真实的请求工具

const router = useRouter()
const isLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  // 1. 简单的表单校验 (因为没用 el-form，手动判断一下即可)
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  isLoading.value = true

  try {
    // 2. 发送真实请求 (对应你第一段代码的逻辑)
    const res = await request.post('/user/login', loginForm)
    
    // 注意：后端返回可能是数字 200 或字符串 '200'，这里做了兼容
    if (res.code === 200 || res.code === '200') {
      
      ElMessage.success('登录成功')
      
      // 3. 存储用户信息 (对应第一段代码)
      localStorage.setItem('user', JSON.stringify(res.data))
      
      // 4. 跳转首页
      router.push('/home') 
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录请求出错:', error)
    ElMessage.error('系统繁忙，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* =========================================
   样式部分保持原样，以保证页面好看
   ========================================= */

.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f7fa 0%, #e8f5e9 100%);
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-box {
  display: flex;
  width: 900px;
  height: 550px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #42b983 0%, #2c3e50 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  padding: 40px;
  position: relative;
}

.left-content { text-align: center; z-index: 2; }
.platform-title { font-size: 28px; font-weight: bold; margin-bottom: 10px; letter-spacing: 1px; }
.platform-desc { font-size: 14px; opacity: 0.9; margin-bottom: 30px; }
.illustration { width: 80%; max-width: 300px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); }

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-wrapper { width: 100%; max-width: 320px; }
.welcome-title { font-size: 26px; color: #333; margin-bottom: 5px; }
.welcome-subtitle { font-size: 14px; color: #999; margin-bottom: 30px; }

.input-group { position: relative; margin-bottom: 20px; }
.input-group .icon {
  position: absolute; left: 15px; top: 50%; transform: translateY(-50%);
  color: #aaa; display: flex;
}

/* 原生Input样式调整 */
.input-group input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #f9f9f9;
  font-size: 14px;
  transition: all 0.3s;
  box-sizing: border-box; 
}
.input-group input:focus {
  border-color: #42b983; background: #fff;
  box-shadow: 0 0 0 4px rgba(66, 185, 131, 0.1); outline: none;
}

.actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; font-size: 14px; color: #666; }
.checkbox-container { display: flex; align-items: center; cursor: pointer; }
.checkbox-container input { margin-right: 5px; accent-color: #42b983; }
.forgot-pwd { color: #42b983; text-decoration: none; }
.forgot-pwd:hover { text-decoration: underline; }

.login-btn {
  width: 100%; padding: 12px; border: none; border-radius: 8px;
  background: linear-gradient(90deg, #42b983, #3498db);
  color: white; font-size: 16px; font-weight: bold; cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.login-btn:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(66, 185, 131, 0.4); }
.login-btn:disabled { background: #ccc; cursor: not-allowed; transform: none; }

.register-link { margin-top: 20px; text-align: center; font-size: 14px; color: #666; }
.register-link a { color: #42b983; text-decoration: none; font-weight: bold; margin-left: 5px; }
</style>