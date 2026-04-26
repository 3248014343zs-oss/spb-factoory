<template>
  <div id="app">
    <el-container v-if="$route.path !== '/login' && $route.path !== '/register'">
      <el-header class="app-header">
        <div class="header-left">
          <span class="logo">🏭 工厂物料管理系统</span>
        </div>
        <div class="header-right">
          <notification-bell />
          <theme-switch />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <img v-if="avatar" :src="avatar" class="user-avatar" />
              <i v-else class="el-icon-user-solid user-avatar-default"></i>
              <span>{{ nickname }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="app-sidebar">
          <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
            <el-menu-item index="/dashboard">
              <i class="el-icon-s-home"></i>
              <span>首页看板</span>
            </el-menu-item>
            <el-menu-item index="/material">
              <i class="el-icon-box"></i>
              <span>物料管理</span>
            </el-menu-item>
            <el-menu-item index="/inventory">
              <i class="el-icon-s-order"></i>
              <span>出入库</span>
            </el-menu-item>
            <el-menu-item index="/inventory-record">
              <i class="el-icon-document"></i>
              <span>流水记录</span>
            </el-menu-item>
            <el-menu-item index="/profile">
              <i class="el-icon-user"></i>
              <span>个人中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
    <router-view v-else />
  </div>
</template>

<script>
import NotificationBell from '@/components/NotificationBell.vue'
import ThemeSwitch from '@/components/ThemeSwitch.vue'

export default {
  name: 'App',
  components: { NotificationBell, ThemeSwitch },
  data() {
    return {
      nickname: localStorage.getItem('nickname') || '用户',
      avatar: localStorage.getItem('avatar') || ''
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
        localStorage.clear()
        this.$router.push('/login')
        this.$message.success('已退出登录')
      } else if (cmd === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
#app { font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif; height: 100vh; width: 100%; }
.app-header { background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08); display: flex; align-items: center; justify-content: space-between; padding: 0 20px; }
.logo { font-size: 20px; font-weight: bold; color: #409EFF; }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; cursor: pointer; margin-left: 20px; }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; margin-right: 8px; object-fit: cover; }
.user-avatar-default { font-size: 28px; margin-right: 8px; color: #409EFF; }
.app-sidebar { background-color: #304156; }
.el-main { background-color: #f0f2f5; padding: 20px; }
</style>