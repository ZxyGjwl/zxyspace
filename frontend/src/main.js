import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css' // Element Plus暗色主题
import './styles/element-dark.scss' // 自定义Element Plus暗色主题
import './styles/main.scss' // 自定义主题样式

const app = createApp(App)

// 初始化暗色模式
document.documentElement.classList.add('dark')

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  size: 'default'
})

app.mount('#app')