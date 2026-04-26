<template>
  <div>
    <el-card>
      <div slot="header">
        <span>出入库统计报表</span>
      </div>

      <!-- 查询条件 -->
      <el-form :inline="true" size="small">
        <el-form-item label="开始时间">
          <el-date-picker v-model="queryParams.startTime" type="date" value-format="yyyy-MM-dd" placeholder="选择开始日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="queryParams.endTime" type="date" value-format="yyyy-MM-dd" placeholder="选择结束日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.materialId" placeholder="选填" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadStats" :loading="statsLoading">查询统计</el-button>
          <el-button @click="exportStats" :loading="exportLoading">导出Excel</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="8">
          <el-card shadow="hover">
            <div style="text-align: center">
              <div style="font-size: 14px; color: #909399">入库总量</div>
              <div style="font-size: 28px; color: #67c23a">{{ stats.totalIn || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div style="text-align: center">
              <div style="font-size: 14px; color: #909399">出库总量</div>
              <div style="font-size: 28px; color: #f56c6c">{{ stats.totalOut || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <div style="text-align: center">
              <div style="font-size: 14px; color: #909399">净变化</div>
              <div :style="{ fontSize: '28px', color: stats.netChange >= 0 ? '#67c23a' : '#f56c6c' }">
                {{ stats.netChange || 0 }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 库存趋势图 -->
      <div style="margin-top: 20px">
        <div style="font-size: 16px; font-weight: 500; margin-bottom: 10px">库存趋势图</div>
        <div ref="chart" style="height: 400px"></div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { report } from '@/api'
import * as echarts from 'echarts'

export default {
  name: 'Report',
  data() {
    return {
      queryParams: { startTime: '', endTime: '', materialId: '' },
      stats: { totalIn: 0, totalOut: 0, netChange: 0 },
      trendData: [],
      statsLoading: false,
      exportLoading: false,
      chart: null
    }
  },
  methods: {
    async loadStats() {
      if (!this.queryParams.startTime || !this.queryParams.endTime) {
        this.$message.warning('请选择时间范围')
        return
      }
      this.statsLoading = true
      try {
        const res = await report.stockStats(this.queryParams)
        if (res.code === 200) {
          this.stats = res.data
        }
        await this.loadTrend()
      } finally {
        this.statsLoading = false
      }
    },
    async loadTrend() {
      const params = { materialId: this.queryParams.materialId, days: 30 }
      const res = await report.inventoryTrend(params)
      if (res.code === 200 && res.data) {
        this.trendData = res.data
        this.renderChart()
      }
    },
    renderChart() {
      if (this.chart) {
        this.chart.dispose()
      }
      this.chart = echarts.init(this.$refs.chart)
      this.chart.setOption({
        title: { text: '物料库存趋势（近30天）', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.trendData.map(item => item.date), name: '日期' },
        yAxis: { type: 'value', name: '库存数量' },
        series: [{
          data: this.trendData.map(item => item.stockNum),
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.3 },
          lineStyle: { color: '#409eff', width: 2 },
          itemStyle: { color: '#409eff' }
        }]
      })
    },
    async exportStats() {
      if (!this.queryParams.startTime || !this.queryParams.endTime) {
        this.$message.warning('请选择时间范围')
        return
      }
      this.exportLoading = true
      try {
        const res = await report.exportStockStats(this.queryParams)
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = `出入库统计_${this.queryParams.startTime}_${this.queryParams.endTime}.xlsx`
        link.click()
        URL.revokeObjectURL(link.href)
        this.$message.success('导出成功')
      } catch (err) {
        this.$message.error('导出失败')
      } finally {
        this.exportLoading = false
      }
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  }
}
</script>