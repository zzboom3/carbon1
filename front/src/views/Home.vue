<template>
  <div class="home-container">
    
    <el-row :gutter="20" class="hero-section">
      <el-col :span="16">
        <div class="welcome-banner">
          <div class="banner-content">
            <h2>早安，{{ user.username || '碳管理专家' }} 👋</h2>
            <p>致力绿色发展，共创零碳未来。今天是 {{ currentDate }}</p>
          </div>
          <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="Eco" class="banner-img" />
          
          <div class="logout-btn-top">
            <el-button type="danger" plain size="small" round @click="handleLogout">
              <el-icon style="margin-right: 4px"><SwitchButton /></el-icon> 退出账号
            </el-button>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="asset-card">
          <div class="asset-title">我的碳资产总估值 (CNY)</div>
          <div class="asset-number">
            ¥ <span class="count-up">{{ totalAssetValue.toLocaleString() }}</span>
          </div>
          <div class="asset-tags">
            <el-tag type="success" effect="dark">实时数据</el-tag>
            <el-tag type="warning" effect="plain" style="margin-left: 10px">项目数: {{ projectCount }} 个</el-tag>
          </div>
          <el-button type="primary" plain class="asset-btn" @click="router.push('/assets')">
            管理资产 >
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stat-row">
      <el-col :span="8" v-for="(item, index) in statsList" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: item.color }">
            <component :is="item.icon" />
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-value">{{ item.value }} <span class="unit">tCO2e</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="main-content">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card mb-20">
          <template #header>
            <div class="card-header">
              <span>📊 碳交易行情趋势</span>
              <el-radio-group v-model="chartTime" size="small">
                <el-radio-button label="周" />
                <el-radio-button label="月" />
                <el-radio-button label="年" />
              </el-radio-group>
            </div>
          </template>
          <div id="trendChart" style="height: 300px;"></div>
        </el-card>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card">
              <template #header><span class="card-header-text">🏗️ 项目类型统计</span></template>
              <div id="projectChart" style="height: 250px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card">
              <template #header><span class="card-header-text">📰 碳行业资讯</span></template>
              <ul class="news-list">
                <li v-for="n in news" :key="n.id">
                  <span class="news-tag">政策</span>
                  <span class="news-title">{{ n.title }}</span>
                  <span class="news-date">{{ n.date }}</span>
                </li>
              </ul>
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="quick-nav-card">
          <template #header>
            <span class="card-header-text">🚀 常用功能</span>
          </template>
          <div class="nav-grid">
            <div class="nav-item" @click="router.push('/assets')">
              <div class="nav-icon" style="background: #e1f3d8; color: #67c23a"><Wallet /></div>
              <span>碳资产管理</span>
            </div>
            <div class="nav-item" @click="router.push('/ai')">
              <div class="nav-icon" style="background: #d9ecff; color: #409eff"><Cpu /></div>
              <span>AI 咨询助手</span>
            </div>
            <div class="nav-item" @click="router.push('/user-info')">
              <div class="nav-icon" style="background: #fde2e2; color: #f56c6c"><User /></div>
              <span>个人中心</span>
            </div>
            <div class="nav-item">
              <div class="nav-icon" style="background: #faecd8; color: #e6a23c"><DataLine /></div>
              <span>市场报表</span>
            </div>
            <div class="nav-item">
              <div class="nav-icon" style="background: #f4f4f5; color: #909399"><Setting /></div>
              <span>系统设置</span>
            </div>
            <div class="nav-item" @click="handleLogout">
              <div class="nav-icon" style="background: #fef0f0; color: #f56c6c"><SwitchButton /></div>
              <span>退出登录</span>
            </div>
          </div>
        </el-card>

        <div class="promo-box">
          <h3>CCER 重启在即</h3>
          <p>立即盘查您的碳资产，抢占市场先机。</p>
          <el-button type="success" size="small" round>了解详情</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
// === 1. 引入请求工具 ===
import request from '@/utils/request' 
import * as echarts from 'echarts'
import { Wallet, Cpu, User, DataLine, Setting, SwitchButton, Goods, Sell, Histogram } from '@element-plus/icons-vue' 
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const chartTime = ref('月')
const currentDate = dayjs().format('YYYY年MM月DD日')

// === 2. 定义响应式数据 ===
const user = ref({}) 
const totalAssetValue = ref(0) // 默认为0，等待接口获取
const projectCount = ref(0)    // 项目数量

// === 3. 核心修复：获取真实数据的方法 ===
const fetchDashboardData = async () => {
  // 从本地缓存拿用户信息
  const userStr = localStorage.getItem('user')
  if (!userStr) return
  
  const userObj = JSON.parse(userStr)
  user.value = userObj // 更新页面上的用户名

  try {
    // 发送请求，带上 uid，查询该用户的资产列表
    const res = await request.get('/asset/list', { 
      params: { uid: userObj.id } 
    })

    if (res.code === 200 || res.code === '200') {
      const list = res.data || []
      
      // 计算总金额 (累加每一项的 amount)
      const sum = list.reduce((prev, curr) => {
        return prev + Number(curr.amount)
      }, 0)
      
      totalAssetValue.value = sum
      projectCount.value = list.length
    }
  } catch (error) {
    console.error("获取首页数据失败", error)
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出当前账号吗？', '提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    ElMessage.success('已安全退出')
    router.push('/login')
  }).catch(() => {})
}

// 静态统计数据 (你可以根据需要也改成动态的)
const statsList = [
  { label: '本月碳减排供应量', value: '450.2', icon: 'Goods', color: 'rgba(64, 158, 255, 0.15)' },
  { label: '累计项目开发量', value: '1,200', icon: 'Histogram', color: 'rgba(103, 194, 58, 0.15)' },
  { label: '年度碳资产销售量', value: '890.5', icon: 'Sell', color: 'rgba(230, 162, 60, 0.15)' },
]

const news = [
  { id: 1, title: '全国碳市场碳排放配额累计成交破100亿', date: '08-12' },
  { id: 2, title: '欧盟碳边境调节机制(CBAM)正式生效', date: '08-10' },
  { id: 3, title: '生态环境部发布温室气体自愿减排新规', date: '08-08' },
  { id: 4, title: '绿色金融支持碳达峰碳中和的指导意见', date: '08-05' },
]

// 图表初始化
const initCharts = () => {
  const trendDom = document.getElementById('trendChart')
  if (trendDom) {
    if (echarts.getInstanceByDom(trendDom)) echarts.getInstanceByDom(trendDom).dispose()
    const myChart = echarts.init(trendDom)
    myChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: '10%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
      yAxis: { type: 'value' },
      series: [{
        name: '成交价',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
          ])
        },
        data: [58, 62, 59, 65, 68, 64, 70],
        itemStyle: { color: '#409EFF' }
      }]
    })
    window.addEventListener('resize', () => myChart.resize())
  }

  const projectDom = document.getElementById('projectChart')
  if (projectDom) {
    if (echarts.getInstanceByDom(projectDom)) echarts.getInstanceByDom(projectDom).dispose()
    const myChart = echarts.init(projectDom)
    myChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%', left: 'center' },
      series: [{
        name: '项目分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        data: [
          { value: 1048, name: '风电' },
          { value: 735, name: '光伏' },
          { value: 580, name: '林业碳汇' },
          { value: 484, name: '生物质能' }
        ]
      }]
    })
    window.addEventListener('resize', () => myChart.resize())
  }
}

onMounted(() => {
  // === 4. 页面加载时调用数据获取 ===
  fetchDashboardData()
  
  nextTick(() => {
    initCharts()
  })
})
</script>

<style scoped>
/* 样式保持原样，无需修改 */
.home-container {
  padding: 20px;
  background-color: #f5f7fa; 
  min-height: calc(100vh - 60px);
}

.hero-section {
  margin-bottom: 20px;
}

.welcome-banner {
  background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
  border-radius: 12px;
  padding: 30px;
  height: 200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.banner-content h2 {
  font-size: 24px;
  color: #006064;
  margin-bottom: 10px;
}
.banner-content p {
  color: #00838f;
  font-size: 16px;
}
.banner-img {
  height: 160px;
  opacity: 0.9;
  transform: rotate(-10deg) translateY(10px);
}
.logout-btn-top {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 10;
}

.asset-card {
  height: 200px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f9eb 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  border: 1px solid #e1f3d8;
}
.asset-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.asset-number {
  font-size: 36px;
  font-weight: bold;
  color: #67c23a; 
  margin-bottom: 15px;
}
.asset-btn {
  width: 100%;
  margin-top: 15px;
}

.stat-row {
  margin-bottom: 20px;
}
.stat-card {
  border-radius: 10px;
  border: none;
}
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  margin-right: 20px;
  color: #606266;
}
.stat-info .stat-label {
  font-size: 14px;
  color: #909399;
}
.stat-info .stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-top: 5px;
}
.unit {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header-text {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
.mb-20 {
  margin-bottom: 20px;
}

.news-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.news-list li {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
  cursor: pointer;
}
.news-list li:hover .news-title {
  color: #409eff;
}
.news-tag {
  background: #f0f9eb;
  color: #67c23a;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 10px;
}
.news-title {
  flex: 1;
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.news-date {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); 
  gap: 15px;
  padding: 10px 0;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s;
}
.nav-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}
.nav-icon {
  width: 45px;
  height: 45px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  margin-bottom: 8px;
}
.nav-item span {
  font-size: 12px;
  color: #606266;
}

.promo-box {
  margin-top: 20px;
  background: linear-gradient(135deg, #1f4037 0%, #99f2c8 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
  text-align: center;
}
.promo-box h3 {
  margin: 0 0 10px 0;
}
.promo-box p {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 15px;
}
</style>