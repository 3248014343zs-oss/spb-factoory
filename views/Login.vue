<template>
  <div class="login-container" :style="{ backgroundImage: `url(${bgImage})` }">
    <div class="login-box">
      <h2>工厂物料管理系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" @keyup.enter.native="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="text" @click="$router.push('/register')" style="float: right">注册账号</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false,
      bgImage: require('@/assets/login-bg.jpg')  // 您可替换此图片
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
      } catch {
        return
      }
      this.loading = true
      try {
        const res = await this.$http.post('/user/login', this.loginForm)
        const { token, role, nickname, avatar } = res.data
        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        localStorage.setItem('nickname', nickname || this.loginForm.username)
        if (avatar) localStorage.setItem('avatar', avatar)
        this.$message.success('登录成功')
        this.$router.push('/dashboard')
      } catch (error) {
        // 错误已在拦截器处理
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.login-box h2 { text-align: center; margin-bottom: 30px; color: #303133; }
</style>