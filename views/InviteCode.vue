<template>
  <div>
    <el-card>
      <div slot="header"><span>邀请码管理</span></div>
      <div v-if="role === 'BOSS'">
        <el-button type="danger" @click="generateCode('ADMIN')">生成超级管理员邀请码</el-button>
        <el-button type="warning" @click="generateCode('WAREHOUSE')">生成仓库管理员邀请码</el-button>
        <el-button type="success" @click="generateCode('OPERATOR')">生成操作员邀请码</el-button>
      </div>
      <div v-if="role === 'ADMIN'">
        <el-button type="warning" @click="generateCode('WAREHOUSE')">生成仓库管理员邀请码</el-button>
        <el-button type="success" @click="generateCode('OPERATOR')">生成操作员邀请码</el-button>
      </div>
      <div v-if="role === 'WAREHOUSE'">
        <el-button type="success" @click="generateCode('OPERATOR')">生成操作员邀请码</el-button>
      </div>
      <div v-if="role === 'OPERATOR'"><el-alert title="您当前是操作员角色，没有生成邀请码的权限" type="info" :closable="false"></el-alert></div>
      
      <el-divider></el-divider>
      <el-table :data="myCodes" border stripe>
        <el-table-column prop="code" label="邀请码"></el-table-column>
        <el-table-column prop="targetRole" label="目标角色">
          <template slot-scope="scope"><el-tag>{{ getRoleName(scope.row.targetRole) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="used" label="状态">
          <template slot-scope="scope"><el-tag :type="scope.row.used ? 'danger' : 'success'">{{ scope.row.used ? '已使用' : '未使用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间"></el-table-column>
        <el-table-column prop="createTime" label="生成时间"></el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top:20px" v-if="role !== 'BOSS'">
      <div slot="header"><span>角色升级</span><span style="font-size:12px;color:#909399;margin-left:10px">（输入更高级别的邀请码可升级角色）</span></div>
      <el-input v-model="upgradeCode" placeholder="请输入升级邀请码" style="width:300px"></el-input>
      <el-button type="primary" @click="upgradeRole">升级角色</el-button>
    </el-card>
  </div>
</template>

<script>
import { invite } from '@/api'

export default {
  data() {
    return {
      role: localStorage.getItem('role'),
      myCodes: [],
      upgradeCode: ''
    }
  },
  created() { if (this.role !== 'OPERATOR') this.fetchMyCodes() },
  methods: {
    getRoleName(role) {
      const map = { 'BOSS': '老板', 'ADMIN': '超级管理员', 'WAREHOUSE': '仓库管理员', 'OPERATOR': '普通操作员' }
      return map[role] || role
    },
    async generateCode(targetRole) {
      const res = await invite.generate(targetRole)
      if (res.code === 200) { this.$message.success(`邀请码：${res.data.code}`); this.fetchMyCodes() }
    },
    async fetchMyCodes() {
      const res = await invite.getMyCodes()
      if (res.code === 200) this.myCodes = res.data
    },
    async upgradeRole() {
      if (!this.upgradeCode) { this.$message.warning('请输入邀请码'); return }
      const res = await invite.use(this.upgradeCode)
      if (res.code === 200) {
        this.$message.success('升级成功，请重新登录')
        setTimeout(() => { localStorage.clear(); this.$router.push('/login') }, 1500)
      }
    }
  }
}
</script>