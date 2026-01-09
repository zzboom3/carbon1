import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { ElMessage } from 'element-plus'

const app = createApp(App)

app.use(router)
app.use(ElementPlus)
// 全局挂载ElMessage，方便在请求拦截器中使用
app.config.globalProperties.$ElMessage = ElMessage

app.mount('#app')