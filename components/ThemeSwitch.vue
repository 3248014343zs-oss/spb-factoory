<template>
  <div class="theme-switch">
    <el-tooltip content="主题切换" placement="bottom">
      <el-dropdown @command="handleCommand">
        <span class="theme-icon"><i class="el-icon-brush"></i></span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="light">🌞 亮色</el-dropdown-item>
          <el-dropdown-item command="dark">🌙 暗色</el-dropdown-item>
          <el-dropdown-item divided command="custom">🎨 自定义</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-tooltip>

    <el-dialog title="自定义主题" :visible.sync="customDialog" width="400px">
      <el-form label-width="100px">
        <el-form-item label="主色调">
          <el-color-picker v-model="primaryColor"></el-color-picker>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="customDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCustomTheme">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ThemeSwitch',
  data() {
    return {
      customDialog: false,
      primaryColor: '#409EFF',
      currentMode: 'light'
    }
  },
  mounted() {
    this.loadTheme()
  },
  methods: {
    async loadTheme() {
      try {
        const res = await this.$http.get('/config/theme')
        const theme = JSON.parse(res.data || '{}')
        this.currentMode = theme.mode || 'light'
        this.primaryColor = theme.primaryColor || '#409EFF'
        this.applyTheme(this.currentMode, this.primaryColor)
      } catch (e) {}
    },
    handleCommand(cmd) {
      if (cmd === 'light') {
        this.currentMode = 'light'
        this.applyTheme('light', this.primaryColor)
        this.saveTheme('light', this.primaryColor)
      } else if (cmd === 'dark') {
        this.currentMode = 'dark'
        this.applyTheme('dark', this.primaryColor)
        this.saveTheme('dark', this.primaryColor)
      } else {
        this.customDialog = true
      }
    },
    applyTheme(mode, color) {
      const root = document.documentElement
      if (mode === 'dark') {
        root.setAttribute('data-theme', 'dark')
      } else {
        root.removeAttribute('data-theme')
      }
      root.style.setProperty('--el-color-primary', color)
    },
    async saveCustomTheme() {
      this.applyTheme(this.currentMode, this.primaryColor)
      await this.saveTheme(this.currentMode, this.primaryColor)
      this.customDialog = false
      this.$message.success('主题已更新')
    },
    async saveTheme(mode, primaryColor) {
      try {
        await this.$http.put('/config/theme', JSON.stringify({ mode, primaryColor }))
      } catch (e) {}
    }
  }
}
</script>

<style>
[data-theme='dark'] {
  --el-bg-color: #1f1f1f;
  --el-text-color-primary: #e5e5e5;
  --el-border-color: #434343;
}
[data-theme='dark'] .el-card,
[data-theme='dark'] .el-table,
[data-theme='dark'] .el-dialog,
[data-theme='dark'] .el-menu {
  background-color: #1f1f1f !important;
  color: #e5e5e5 !important;
}
[data-theme='dark'] .app-header {
  background-color: #1f1f1f !important;
  border-bottom: 1px solid #434343;
}
[data-theme='dark'] .el-main {
  background-color: #141414 !important;
}
.theme-icon { font-size: 20px; cursor: pointer; margin-right: 20px; }
</style>