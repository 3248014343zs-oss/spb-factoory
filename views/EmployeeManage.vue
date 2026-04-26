<template>
  <el-card>
    <div slot="header"><span>员工管理</span><span style="font-size:12px;color:#909399;margin-left:10px">（只能管理比您级别低的员工）</span></div>
    <el-table :data="employeeList" border stripe>
      <el-table-column prop="uid" label="UID" width="100"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="nickname" label="昵称"></el-table-column>
      <el-table-column prop="role" label="角色" width="140">
        <template slot-scope="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">{{ getRoleName(scope.row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="手机号"></el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="small" type="warning" @click="downgrade(scope.row)" :disabled="scope.row.role === 'OPERATOR' || scope.row.role === 'BOSS'">降级</el-button>
          <el-button size="small" type="danger" @click="deleteEmployee(scope.row)" :disabled="scope.row.username === localStorage.getItem('username')">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script>
import { employee } from '@/api'

export default {
  data() {
    return { employeeList: [] }
  },
  created() { this.fetchList() },
  methods: {
    getRoleName(role) {
      const map = { 'BOSS': '老板', 'ADMIN': '超级管理员', 'WAREHOUSE': '仓库管理员', 'OPERATOR': '普通操作员' }
      return map[role] || role
    },
    getRoleTagType(role) {
      const map = { 'BOSS': 'danger', 'ADMIN': 'danger', 'WAREHOUSE': 'warning', 'OPERATOR': 'success' }
      return map[role] || 'info'
    },
    async fetchList() {
      const res = await employee.list()
      if (res.code === 200) this.employeeList = res.data
    },
    async downgrade(row) {
      await this.$confirm(`确定将 ${row.username} 降级吗？`, '提示', { type: 'warning' })
      const res = await employee.downgrade(row.uid)
      if (res.code === 200) { this.$message.success('降级成功'); this.fetchList() }
    },
    async deleteEmployee(row) {
      await this.$confirm(`确定删除员工 ${row.username} 吗？`, '提示', { type: 'warning' })
      const res = await employee.delete(row.uid)
      if (res.code === 200) { this.$message.success('删除成功'); this.fetchList() }
    }
  }
}
</script>