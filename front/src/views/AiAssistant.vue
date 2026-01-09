<template>
  <div class="ai-page-container">
    <div class="sidebar">
      <div class="sidebar-header">
        <div class="logo-box">
          <el-icon><WindPower /></el-icon>
        </div>
        <div class="app-info">
          <h3>EcoBrain AI</h3>
          <span>碳中和智能大脑</span>
        </div>
      </div>

      <div class="status-card">
        <div class="card-row">
          <span class="label">当前用户</span>
          <span class="val">{{ user.username || 'Guest' }}</span>
        </div>
        <div class="card-row">
          <span class="label">资产连接</span>
          <span class="val highlight">已同步</span>
        </div>
        <div class="card-row">
          <span class="label">AI 引擎</span>
          <span class="val online">● Online</span>
        </div>
      </div>

      <div class="quick-actions">
        <div class="section-title">你可以试着问...</div>
        
        <div class="action-group">
          <div class="group-label"><el-icon><DataLine /></el-icon> 市场洞察</div>
          <div class="tag-list">
            <span class="cmd-tag" @click="handleQuickAsk('分析当前中国碳市场的价格趋势')">📉 碳价趋势</span>
            <span class="cmd-tag" @click="handleQuickAsk('什么是 CCER？它和配额有什么区别？')">📖 CCER 科普</span>
          </div>
        </div>

        <div class="action-group">
          <div class="group-label"><el-icon><Document /></el-icon> 业务辅助</div>
          <div class="tag-list">
            <span class="cmd-tag" @click="handleQuickAsk('帮我写一份企业碳减排的初步规划方案')">📝 生成减排方案</span>
            <span class="cmd-tag" @click="handleQuickAsk('分析我的资产持有情况')">💰 我的资产分析</span>
          </div>
        </div>
      </div>
      
      <div class="sidebar-footer">
        <p>Powered by Deep Carbon Model</p>
      </div>
    </div>

    <div class="chat-main">
      <div class="chat-header">
        <div class="header-left">
          <span class="title">智能对话</span>
          <el-tag size="small" type="success" effect="light" round>已联网</el-tag>
        </div>
        <el-button type="info" link size="small" @click="clearHistory">
          <el-icon><Delete /></el-icon> 清空记录
        </el-button>
      </div>

      <div class="chat-window" ref="chatRef">
        <div v-if="chatList.length === 0" class="empty-state">
          <div class="welcome-icon">
            <el-icon><Sunny /></el-icon>
          </div>
          <h2>你好，我是您的 ESG 碳汇顾问</h2>
          <p>我已经接入了公网大模型，您可以问我任何问题。</p>
          <p class="sub-tip">试试问我："如何申请碳资产账户？" 或 "帮我写一首关于环保的诗"</p>
        </div>

        <div 
          v-for="(msg, index) in chatList" 
          :key="index" 
          class="msg-row" 
          :class="msg.role === 'user' ? 'msg-right' : 'msg-left'"
        >
          <div class="avatar">
            <img v-if="msg.role === 'user'" :src="userAvatar" alt="Me">
            <div v-else class="ai-avatar">
              <el-icon><Cpu /></el-icon>
            </div>
          </div>

          <div class="msg-content">
            <div class="sender-name">{{ msg.role === 'user' ? '我' : 'EcoBrain' }}</div>
            <div class="bubble">
              <div v-if="msg.loading" class="typing">
                <span class="dot"></span><span class="dot"></span><span class="dot"></span>
              </div>
              <div v-else class="markdown-body" v-html="formatText(msg.content)"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <div class="input-box-wrapper">
          <el-input
            v-model="inputMsg"
            type="textarea"
            :rows="1"
            autosize
            placeholder="请输入您的问题... (Enter 发送)"
            @keydown.enter.prevent="handleEnter"
            class="custom-textarea"
          />
          <button class="send-btn" :disabled="isSending" @click="sendMessage">
            <el-icon v-if="!isSending"><Promotion /></el-icon>
            <el-icon v-else class="is-loading"><Loading /></el-icon>
          </button>
        </div>
        <div class="disclaimer">
          AI 内容由大模型生成，仅供参考，不构成投资建议。
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
// 确保这里引入的是 WindPower，防止报错
import { WindPower, DataLine, Document, Delete, Sunny, Cpu, Promotion, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// === 1. 核心配置 ===
const API_CONFIG = {
  url: 'https://api.openai-proxy.org/v1/chat/completions', 
  key: 'sk-DLXRrSaAZpEiqtkYr2Op6gs44R6CVu9j8fNXm0Zd4E0AZwCy',
  model: 'gpt-3.5-turbo'
}

// === 2. 状态管理 ===
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const userAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const assets = ref([])
const chatRef = ref(null)
const inputMsg = ref('')
const isSending = ref(false)
const chatList = ref([])

// === 3. 初始化：获取用户本地资产数据 ===
const fetchUserAssets = async () => {
  if (!user.value.id && !user.value.username) return
  const params = user.value.id ? { uid: user.value.id } : { username: user.value.username }
  try {
    const res = await request.get('/asset/list', { params })
    const list = res.data || res.rows || []
    assets.value = Array.isArray(list) ? list : []
  } catch (error) {
    console.error('资产同步失败', error)
  }
}

// === 4. 发送逻辑 ===
const handleEnter = (e) => {
  if (!e.shiftKey) sendMessage()
}

const handleQuickAsk = (text) => {
  inputMsg.value = text
  sendMessage()
}

const sendMessage = async () => {
  const text = inputMsg.value.trim()
  if (!text) return

  // 1. 用户消息上屏
  chatList.value.push({ role: 'user', content: text })
  inputMsg.value = ''
  scrollToBottom()

  // 2. 状态流转
  isSending.value = true
  chatList.value.push({ role: 'assistant', content: '', loading: true })
  scrollToBottom()

  try {
    // 3. 智能构建 Prompt
    let messages = []
    
    // --- 策略：判断是否需要注入“本地资产数据” ---
    const isAskingAssets = (text.includes('我的') || text.includes('持有') || text.includes('账户')) && 
                           (text.includes('资产') || text.includes('钱') || text.includes('多少'))

    if (isAskingAssets) {
      // ❗❗❗ 修改重点：这里将 amount 映射为“金额”，并加上“元”单位
      const assetData = assets.value.length > 0 
        ? JSON.stringify(assets.value.map(a => ({ 
            项目: a.name || a.projectName || '未命名资产', // 防止出现 undefined
            金额: a.amount + ' 元', // 明确告诉 AI 这是钱
            类型: a.type 
          })))
        : "用户当前账户余额为空。"
      
      messages = [
        { role: "system", content: `你是一个专业的碳金融助手。用户的数据库中资产记录如下（注意：单位均为人民币/元）：${assetData}。请根据这些数据回答用户的问题，直接说金额，不要说成“数量”或“吨”。` },
        { role: "user", content: text }
      ]
    } else {
      // --- 策略：普通问题 ---
      messages = [
        { role: "system", content: "你是一个专业的碳交易和ESG专家，名字叫 EcoBrain。你的回答应该专业、简洁，并且语气亲切。" },
        ...chatList.value.slice(-3, -1).map(m => ({ role: m.role === 'user' ? 'user' : 'assistant', content: m.content })),
        { role: "user", content: text }
      ]
    }

    // 4. 调用真实 API
    const aiResponse = await callOpenAI(messages)
    
    // 5. 更新 UI
    chatList.value.pop()
    chatList.value.push({ role: 'assistant', content: aiResponse })

  } catch (err) {
    chatList.value.pop()
    chatList.value.push({ role: 'assistant', content: `连接超时或 API 额度不足，请检查网络。\n错误信息：${err.message}` })
  } finally {
    isSending.value = false
    scrollToBottom()
  }
}

// === 5. 调用 OpenAI 接口 ===
const callOpenAI = async (messages) => {
  const response = await fetch(API_CONFIG.url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${API_CONFIG.key}`
    },
    body: JSON.stringify({
      model: API_CONFIG.model,
      messages: messages,
      temperature: 0.7
    })
  })

  if (!response.ok) {
    const errData = await response.json()
    throw new Error(errData.error?.message || 'API 请求失败')
  }

  const data = await response.json()
  return data.choices[0].message.content
}

// === 6. 工具函数 ===
const formatText = (text) => {
  if (!text) return ''
  return text.replace(/\n/g, '<br>').replace(/\*\*(.*?)\*\*/g, '<b>$1</b>')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatRef.value) chatRef.value.scrollTop = chatRef.value.scrollHeight + 100
  })
}

const clearHistory = () => {
  chatList.value = []
}

onMounted(() => {
  fetchUserAssets()
})
</script>

<style scoped>
/* 样式与之前完全保持一致 */
:root {
  --primary-green: #059669;    
  --light-green: #d1fae5;      
  --dark-green: #064e3b;       
  --bg-color: #f0fdf4;         
}

.ai-page-container {
  display: flex;
  height: calc(100vh - 80px);
  background-color: #f3f4f6;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin: 10px;
  font-family: 'PingFang SC', 'Helvetica Neue', sans-serif;
}

/* === 侧边栏设计 === */
.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #ffffff 0%, #f0fdf4 100%);
  border-right: 1px solid #e5e7eb;
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
}
.logo-box {
  width: 44px; height: 44px;
  background: #059669;
  border-radius: 12px;
  color: white;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 10px rgba(5, 150, 105, 0.3);
}
.app-info h3 { margin: 0; font-size: 18px; color: #111827; font-weight: 700; }
.app-info span { font-size: 12px; color: #059669; font-weight: 500; }

.status-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.02);
}
.card-row {
  display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 13px;
}
.card-row:last-child { margin-bottom: 0; }
.card-row .label { color: #6b7280; }
.card-row .val { font-weight: 600; color: #374151; }
.card-row .val.highlight { color: #059669; }
.card-row .val.online { color: #10b981; display: flex; align-items: center; gap: 4px; }

.section-title {
  font-size: 12px; font-weight: 700; color: #9ca3af; margin-bottom: 12px; letter-spacing: 0.5px;
}
.action-group { margin-bottom: 20px; }
.group-label {
  display: flex; align-items: center; gap: 6px;
  font-size: 13px; color: #374151; font-weight: 600; margin-bottom: 8px;
}
.tag-list { display: flex; flex-direction: column; gap: 8px; }
.cmd-tag {
  font-size: 13px; color: #4b5563; padding: 8px 12px;
  background: white; border: 1px solid #e5e7eb; border-radius: 8px;
  cursor: pointer; transition: all 0.2s;
}
.cmd-tag:hover {
  border-color: #059669; color: #059669; background: #ecfdf5; transform: translateX(4px);
}

.sidebar-footer {
  margin-top: auto; text-align: center; font-size: 11px; color: #d1d5db;
}

/* === 聊天主窗口 === */
.chat-main {
  flex: 1; display: flex; flex-direction: column; background: white;
}

.chat-header {
  height: 60px; border-bottom: 1px solid #f3f4f6;
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 24px;
}
.header-left { display: flex; align-items: center; gap: 10px; }
.header-left .title { font-size: 16px; font-weight: 700; color: #1f2937; }

.chat-window {
  flex: 1; background: #f9fafb; padding: 24px; overflow-y: auto;
}

/* 空状态 */
.empty-state {
  height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: #6b7280;
}
.welcome-icon {
  width: 80px; height: 80px; background: #ecfdf5; color: #059669;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 40px; margin-bottom: 20px;
}
.empty-state h2 { color: #111827; margin: 0 0 10px 0; }
.sub-tip { font-size: 13px; color: #9ca3af; margin-top: 5px; }

/* 消息样式 */
.msg-row { display: flex; margin-bottom: 24px; gap: 12px; }
.msg-right { flex-direction: row-reverse; }

.avatar img, .ai-avatar { width: 40px; height: 40px; border-radius: 10px; }
.ai-avatar { background: #059669; color: white; display: flex; align-items: center; justify-content: center; font-size: 20px; }

.msg-content { display: flex; flex-direction: column; max-width: 75%; }
.msg-right .msg-content { align-items: flex-end; }
.sender-name { font-size: 12px; color: #9ca3af; margin-bottom: 4px; }

.bubble {
  padding: 12px 16px; border-radius: 12px; font-size: 14px; line-height: 1.6;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05); position: relative;
}
.msg-left .bubble {
  background: white; border: 1px solid #e5e7eb; border-top-left-radius: 2px; color: #374151;
}
.msg-right .bubble {
  background: #059669; color: white; border-top-right-radius: 2px;
}

/* 输入框 */
.input-area {
  padding: 20px; background: white; border-top: 1px solid #f3f4f6;
}
.input-box-wrapper {
  display: flex; gap: 12px; align-items: flex-end;
  background: #f9fafb; padding: 10px; border-radius: 12px;
  border: 1px solid #e5e7eb; transition: all 0.2s;
}
.input-box-wrapper:focus-within { border-color: #059669; background: white; box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.1); }

.custom-textarea :deep(.el-textarea__inner) {
  background: transparent !important; box-shadow: none !important;
  padding: 0; min-height: 24px !important; color: #374151;
}
.send-btn {
  width: 36px; height: 36px; border-radius: 8px; border: none;
  background: #059669; color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: background 0.2s; flex-shrink: 0;
}
.send-btn:hover { background: #047857; }
.send-btn:disabled { background: #d1d5db; cursor: not-allowed; }
.is-loading { animation: rotate 1s linear infinite; }

.disclaimer {
  text-align: center; font-size: 11px; color: #d1d5db; margin-top: 10px;
}

/* 打字动画 */
.typing .dot {
  display: inline-block; width: 6px; height: 6px; background: #9ca3af;
  border-radius: 50%; margin: 0 2px; animation: bounce 1.4s infinite ease-in-out both;
}
.typing .dot:nth-child(1) { animation-delay: -0.32s; }
.typing .dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes bounce { 0%, 80%, 100% { transform: scale(0); } 40% { transform: scale(1); } }
@keyframes rotate { 100% { transform: rotate(360deg); } }
</style>