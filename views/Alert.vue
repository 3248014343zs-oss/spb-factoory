<template>
  <div>
    <el-card>
      <div slot="header">
        <span>库存预警报表</span>
        <el-button type="primary" size="small" style="float: right" @click="exportExcel" :loading="exportLoading">导出Excel</el-button>
      </div>

      <el-alert
        title="预警说明"
        type="info"
        description="当物料库存低于下限或高于上限时，系统会自动预警。请及时处理预警物料。"
        show-icon
        :closable="false"
        style="margin-bottom: 15px"
      ></el-alert>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="code" label="编码" width="120"></el-table-column>
        <el-table-column prop="name" label="物料名称" width="150"></el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="spec" label="规格" width="150"></el-table-column>
        <el-table-column prop="stockNum" label="当前库存" width="100" align="center">
          <template slot-scope="scope">
            <span :style="{ color: getWarningColor(scope.row) }">{{ scope.row.stockNum }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minThreshold" label="下限" width="80" align="center"></el-table-column>
        <el-table-column prop="maxThreshold" label="上限" width="80" align="center"></el-table-column>
        <el-table-column label="预警状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getWarningType(scope.row)">
              {{ getWarningText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="建议操作" width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.stockNum < scope.row.minThreshold" style="color: #f56c6c">建议紧急采购</span>
            <span v-else-if="scope.row.stockNum > scope.row.maxThreshold" style="color: #e6a23c">建议安排出库</span>
            <span v-else style="color: #67c23a">库存正常</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { alertReport } from '@/api'

export default {
  name: 'Alert',
  data() {
    return {
      list: [],
      loading: false,
      exportLoading: false
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await alertReport.list()
        if (res.code === 200) {
          this.list = res.data
        }
      } finally {
        this.loading = false
      }
    },
    getWarningColor(row) {
      if (row.stockNum < row.minThreshold) return '#f56c6c'
      if (row.stockNum > row.maxThreshold) return '#e6a23c'
      return '#67c23a'
    },
    getWarningType(row) {
      if (row.stockNum < row.minThreshold) return 'danger'
      if (row.stockNum > row.maxThreshold) return 'warning'
      return 'success'
    },
    getWarningText(row) {
      if (row.stockNum < row.minThreshold) return '低于下限'
      if (row.stockNum > row.maxThreshold) return '高于上限'
      return '正常'
    },
    async exportExcel() {
      this.exportLoading = true
      try {
        const res = await alertReport.export()
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = `库存预警报表_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.xlsx`
        link.click()
        URL.revokeObjectURL(link.href)
        this.$message.success('导出成功')
      } catch (err) {
        this.$message.error('导出失败')
      } finally {
        this.exportLoading = false
      }
    }
  }
}
</script>