<template>
  <div>
    <el-card>
      <div slot="header">
        <span>操作日志</span>
        <span style="font-size: 12px; color: #909399; margin-left: 10px">（记录所有用户的操作行为）</span>
      </div>

      <!-- 筛选条件 -->
      <el-form :inline="true" size="small">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="成功" :value="1"></el-option>
            <el-option label="失败" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchLogs">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 日志表格 -->
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="operation" label="操作描述" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="method" label="请求方法" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ip" label="IP地址" width="140"></el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.duration > 1000 ? 'danger' : scope.row.duration > 500 ? 'warning' : 'success'" size="small">
              {{ scope.row.duration }}ms
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <span style="color: #f56c6c">{{ scope.row.errorMsg || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180"></el-table-column>
        <el-table-column label="详情" width="80" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next, jumper"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @current-change="handlePageChange"
      ></el-pagination>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog title="操作日志详情" :visible.sync="detailVisible" width="60%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre style="white-space: pre-wrap; word-break: break-all; max-height: 300px; overflow: auto;">{{ formatParams(currentLog.params) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.duration }} ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" v-if="currentLog.errorMsg">
          <span style="color: #f56c6c">{{ currentLog.errorMsg }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { log } from '@/api'

export default {
  name: 'Log',
  data() {
    return {
      list: [],
      loading: false,
      queryParams: { username: '', status: '' },
      dateRange: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      detailVisible: false,
      currentLog: {}
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    formatParams(params) {
      if (!params) return '-'
      try {
        const obj = JSON.parse(params)
        return JSON.stringify(obj, null, 2)
      } catch {
        return params
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await log.list()
        if (res.code === 200) {
          let data = res.data
          if (this.queryParams.username) {
            data = data.filter(item => item.username === this.queryParams.username)
          }
          if (this.queryParams.status !== '') {
            data = data.filter(item => item.status === this.queryParams.status)
          }
          if (this.dateRange && this.dateRange.length === 2) {
            const [start, end] = this.dateRange
            data = data.filter(item => item.createTime >= start && item.createTime <= end + ' 23:59:59')
          }
          this.total = data.length
          const start = (this.pageNum - 1) * this.pageSize
          this.list = data.slice(start, start + this.pageSize)
        }
      } finally {
        this.loading = false
      }
    },
    searchLogs() {
      this.pageNum = 1
      this.fetchList()
    },
    resetSearch() {
      this.queryParams = { username: '', status: '' }
      this.dateRange = []
      this.pageNum = 1
      this.fetchList()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.fetchList()
    },
    viewDetail(row) {
      this.currentLog = row
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.pagination {
  margin-top: 15px;
  text-align: right;
}
</style>