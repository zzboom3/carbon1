<template>
  <div class="dashboard-container">
    
    <el-row :gutter="20" class="top-statistics">
      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-title">我的碳资产总值</div>
          <div class="kpi-value">¥ {{ totalAmount.toLocaleString() }}</div>
          <div class="kpi-desc">持有项目数: {{ tableData.length }}</div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">资产类型分布</div>
          <div id="pieChart" style="height: 220px;"></div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card shadow="hover">
          <div slot="header" class="chart-header">Top 5 高价值项目</div>
          <div id="barChart" style="height: 220px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <div class="toolbar">
      <el-button type="primary" size="large" @click="handleCreate">
        + 新建碳资产
      </el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="projectName" label="项目名称" />
        <el-table-column prop="assetType" label="碳资产类型" align="center">
          <template #default="scope">
            <el-tag>{{ scope.row.assetType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="资产金额" sortable align="right">
          <template #default="scope">
            ¥ {{ Number(scope.row.amount).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">修改</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEditMode ? '修改资产' : '新建资产'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="form.projectName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="资产类型">
          <el-select v-model="form.assetType" placeholder="请选择类型" style="width: 100%">
            <el-option label="CCER" value="CCER" />
            <el-option label="CEA" value="CEA" />
            <el-option label="I-REC" value="I-REC" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产金额">
          <el-input-number v-model="form.amount" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

const tableData = ref([])
const dialogVisible = ref(false)
const isEditMode = ref(false)
const form = ref({ id: null, uid: null, projectName: '', assetType: '', amount: 0 })

// 获取当前用户ID (假设登录时已存入 user)
const userStr = localStorage.getItem('user')
const currentUser = userStr ? JSON.parse(userStr) : {}
const currentUid = currentUser.id

// 计算总金额
const totalAmount = computed(() => {
  return tableData.value.reduce((sum, item) => sum + Number(item.amount), 0)
})

// 加载数据
const loadData = async () => {
  if (!currentUid) return ElMessage.warning('请先登录')
  
  const res = await request.get('/asset/list', { params: { uid: currentUid } })
  if (res.code === '200' || res.code === 200) {
    tableData.value = res.data
    nextTick(() => renderCharts()) // 刷新图表
  }
}

// 渲染图表
let pieChart, barChart
const renderCharts = () => {
  // 1. 饼图数据
  const typeMap = {}
  tableData.value.forEach(item => {
    typeMap[item.assetType] = (typeMap[item.assetType] || 0) + Number(item.amount)
  })
  const pieData = Object.keys(typeMap).map(k => ({ name: k, value: typeMap[k] }))

  // 2. 柱状图数据 (前5)
  const top5 = [...tableData.value].sort((a,b) => b.amount - a.amount).slice(0, 5)
  
  // 绘制饼图
  const pieDom = document.getElementById('pieChart')
  if (pieDom) {
    if (echarts.getInstanceByDom(pieDom)) echarts.getInstanceByDom(pieDom).dispose()
    pieChart = echarts.init(pieDom)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: ['40%', '70%'], data: pieData }]
    })
  }

  // 绘制柱状图
  const barDom = document.getElementById('barChart')
  if (barDom) {
    if (echarts.getInstanceByDom(barDom)) echarts.getInstanceByDom(barDom).dispose()
    barChart = echarts.init(barDom)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { bottom: '20%' },
      xAxis: { type: 'category', data: top5.map(i => i.projectName), axisLabel: { rotate: 30 } },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: top5.map(i => i.amount), itemStyle: { color: '#409EFF' } }]
    })
  }
}

// 操作逻辑
const handleCreate = () => {
  isEditMode.value = false
  form.value = { id: null, uid: currentUid, projectName: '', assetType: '', amount: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEditMode.value = true
  form.value = JSON.parse(JSON.stringify(row))
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.value.projectName) return ElMessage.warning('请输入名称')
  
  // 确保带上 uid
  form.value.uid = currentUid 
  
  const url = isEditMode.value ? '/asset/update' : '/asset/add'
  const method = isEditMode.value ? request.put : request.post
  
  const res = await method(url, form.value)
  if (res.code === '200' || res.code === 200) {
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除?', '提示', { type: 'warning' }).then(async () => {
    const res = await request.delete(`/asset/delete/${id}`)
    if (res.code === '200' || res.code === 200) {
      ElMessage.success('已删除')
      loadData()
    }
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => {
    pieChart?.resize()
    barChart?.resize()
  })
})
</script>

<style scoped>
.dashboard-container { padding: 20px; background: #f0f2f5; min-height: 100vh; }
.top-statistics { margin-bottom: 20px; }
.kpi-card { height: 280px; display: flex; flex-direction: column; justify-content: center; align-items: center; background: linear-gradient(135deg, #1f4037, #99f2c8); color: white; }
.kpi-value { font-size: 40px; font-weight: bold; margin: 15px 0; }
.toolbar { margin-bottom: 15px; }
.chart-header { font-weight: bold; }
</style>