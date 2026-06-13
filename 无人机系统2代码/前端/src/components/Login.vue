<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="text-center mb-4">无人机管理平台</h2>

      <div v-if="message.text" :class="'alert alert-' + message.type + ' alert-dismissible fade show'" role="alert">
        {{ message.text }}
        <button type="button" class="close" @click="message.text = ''">&times;</button>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="loginForm.username"
            type="text"
            class="form-control form-control-lg"
            :class="{ 'is-invalid': errors.username }"
            placeholder="请输入用户名"
            @input="errors.username = ''"
          />
          <div class="invalid-feedback" v-if="errors.username">{{ errors.username }}</div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            class="form-control form-control-lg"
            :class="{ 'is-invalid': errors.password }"
            placeholder="请输入密码"
            @input="errors.password = ''"
            @keyup.enter="handleLogin"
          />
          <div class="invalid-feedback" v-if="errors.password">{{ errors.password }}</div>
        </div>

        <button type="submit" class="btn btn-primary btn-lg btn-block mt-4" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm mr-2" role="status"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="text-center mt-3">
        <small class="text-muted">默认账号: admin / admin123</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { login, setToken, setUser } from '../api/user'

// 触发登录成功事件，通知父组件切换页面
const emit = defineEmits(['login-success'])

// 登录按钮加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单字段校验错误信息
const errors = reactive({
  username: '',
  password: ''
})

// 提示消息（用于显示登录失败的反馈信息）
const message = reactive({
  text: '',
  type: 'danger'
})

// 表单校验：验证用户名和密码是否有效
const validate = () => {
  let valid = true
  if (!loginForm.username) {
    errors.username = '请输入用户名'
    valid = false
  }
  if (!loginForm.password) {
    errors.password = '请输入密码'
    valid = false
  } else if (loginForm.password.length < 3) {
    errors.password = '密码长度至少3个字符'
    valid = false
  }
  return valid
}

// 提交登录：调用后端登录接口，成功后保存 token 并通知父组件
const handleLogin = async () => {
  message.text = ''
  if (!validate()) return

  loading.value = true
  try {
    const response = await login(loginForm)
    if (response.code === 200) {
      setToken('token-' + response.data.id)
      setUser(response.data)
      emit('login-success', response.data)
    } else {
      message.text = response.message || '登录失败'
      message.type = 'danger'
    }
  } catch (error) {
    message.text = error.response?.data?.message || '登录失败，请检查用户名和密码'
    message.type = 'danger'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}
</style>