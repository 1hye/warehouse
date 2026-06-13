/**
 * 应用入口文件
 * 创建Vue应用并注册全局插件
 */
import { createApp } from 'vue'
import 'bootstrap/dist/css/bootstrap.min.css'

// Bootstrap 4 JS 依赖 jQuery，需要挂载到 window
import $ from 'jquery'
window.jQuery = window.$ = $

import 'bootstrap'
import App from './App.vue'

// 创建Vue应用实例
const app = createApp(App)

// 将应用挂载到#app DOM元素上
app.mount('#app')