<template>
  <div class="app-container">
    <template v-if="!isLoggedIn">
      <Login @login-success="handleLoginSuccess" />
    </template>
    <template v-else>
      <nav class="navbar navbar-dark" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
        <span class="navbar-brand mb-0 h1">无人机信息管理系统</span>
        <div class="form-inline">
          <span class="text-white mr-3">{{ currentUser?.realName || currentUser?.username }}</span>
          <button class="btn btn-outline-light btn-sm" @click="handleLogout">退出登录</button>
        </div>
      </nav>
      <main class="container-fluid py-4">
        <DroneManagement />6
      </main>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Login from './components/Login.vue'
import DroneManagement from './components/DroneManagement.vue'
import { getToken, getUser, removeToken } from './api/user'

// 登录状态与当前用户信息
const isLoggedIn = ref(false)
const currentUser = ref(null)

// 应用初始化：检查本地 token 和用户信息，自动恢复登录状态
onMounted(() => {
  const token = getToken()
  const user = getUser()
  if (token && user) {
    isLoggedIn.value = true
    currentUser.value = user
  }
})

// 登录成功回调：更新登录状态和用户信息
const handleLoginSuccess = (user) => {
  isLoggedIn.value = true
  currentUser.value = user
}

// 退出登录：清除 token 并重置状态
const handleLogout = () => {
  if (!window.confirm('确定要退出登录吗？')) return
  removeToken()
  isLoggedIn.value = false
  currentUser.value = null
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

.app-container {
  min-height: 100vh;
}

.navbar-brand {
  font-size: 24px;
  font-weight: 600;
}
</style>