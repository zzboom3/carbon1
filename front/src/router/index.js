import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue' // 引入布局组件

import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import AiAssistant from '../views/AiAssistant.vue'
import UserInfo from '../views/UserInfo.vue'
import BossDashboard from '../views/BossDashboard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // === 1. 不需要顶部栏的页面 ===
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    // 根路径重定向到登录页
    {
      path: '/',
      redirect: '/login'
    },

    // === 2. 需要顶部栏(Layout)的页面 ===
    // 这里保留 Layout，是为了让你那个“回到主页”按钮能显示出来
    {
      path: '/',
      component: Layout, 
      children: [
        {
          path: 'home', // 访问 /home
          name: 'home',
          component: Home
        },
        {
          path: 'ai', // 访问 /ai
          name: 'AiAssistant',
          component: AiAssistant
        },
        {
          path: 'user-info', // 访问 /user-info
          name: 'UserInfo',
          component: UserInfo
        },
        { 
          path: 'assets', 
          name: 'Assets', 
          component: BossDashboard 
        }
      ]
    }
  ]
})

// === 注意：这里原来的 router.beforeEach 全部删掉了 ===
// 现在没有任何拦截，想去哪就去哪

export default router