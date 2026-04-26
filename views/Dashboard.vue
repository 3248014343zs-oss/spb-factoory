<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-icon" :style="{ color: card.color }"><i :class="card.icon"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <div slot="header"><span>近7天出入库趋势</span></div>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header"><span>物料分类占比</span></div>
          <div ref="pieChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      cards: [
        { title: '物料总数', value: 0, icon: 'el-icon-box', color: '#409EFF' },
        { title: '低库存预警', value: 0, icon: 'el-icon-warning', color: '#E6A23C' },
        { title: '今日入库', value: 0, icon: 'el-icon-download', color: '#67C23A' },
        { title: '今日出库', value: 0, icon: 'el-icon-upload2', color: '#F56C6C' }
      ],
      trendChart: null,
      pieChart: null
    }
  },
  mounted() {
    this.fetchStats()
    this.fetchTrend()
    this.fetchPie()
  },
  beforeDestroy() {
    if (this.trendChart) this.trendChart.dispose()
    if (this.pieChart) this.pieChart.dispose()
  },
  methods: {
    async fetchStats() {
      try {
        const res = await this.$http.get('/dashboard/stats')
        const data = res.data
        this.cards[0].value = data.totalMaterials || 0
        this.cards[1].value = data.lowStockCount || 0
        this.cards[2].value = data.todayInQuantity || 0
        this.cards[3].value = data.todayOutQuantity || 0
      } catch (e) {}
    },
    async fetchTrend() {
      try {
        const res = await this.$http.get('/dashboard/trend?days=7')
        const data = res.data
        const dates = data.map(d => d.date)
        const inData = data.map(d => d.inQuantity)
        const outData = data.map(d => d.outQuantity)

        this.trendChart = echarts.init(this.$refs.trendChart)
        this.trendChart.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['入库', '出库'] },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value' },
          series: [
            { name: '入库', type: 'line', smooth: true, data: inData, color: '#67C23A' },
            { name: '出库', type: 'line', smooth: true, data: outData, color: '#F56C6C' }
          ]
        })
      } catch (e) {}
    },
    async fetchPie() {
      try {
        const res = await this.$http.get('/dashboard/pie')
        const data = res.data

        this.pieChart = echarts.init(this.$refs.pieChart)
        this.pieChart.setOption({
          tooltip: { trigger: 'item' },
          legend: { orient: 'vertical', left: 'left' },
          series: [{
            type: 'pie',
            radius: ['40%', '65%'],
            avoidLabelOverlap: true,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            emphasis: { label: { show: true } },
            data: data
          }]
        })
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.stat-card { display: flex; align-items: center; }
.stat-icon { font-size: 48px; margin-right: 20px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-title { color: #909399; margin-top: 5px; }
</style><template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-icon" :style="{ color: card.color }"><i :class="card.icon"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <div slot="header"><span>近7天出入库趋势</span></div>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header"><span>物料分类占比</span></div>
          <div ref="pieChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      cards: [
        { title: '物料总数', value: 0, icon: 'el-icon-box', color: '#409EFF' },
        { title: '低库存预警', value: 0, icon: 'el-icon-warning', color: '#E6A23C' },
        { title: '今日入库', value: 0, icon: 'el-icon-download', color: '#67C23A' },
        { title: '今日出库', value: 0, icon: 'el-icon-upload2', color: '#F56C6C' }
      ],
      trendChart: null,
      pieChart: null
    }
  },
  mounted() {
    this.fetchStats()
    this.fetchTrend()
    this.fetchPie()
  },
  beforeDestroy() {
    if (this.trendChart) this.trendChart.dispose()
    if (this.pieChart) this.pieChart.dispose()
  },
  methods: {
    async fetchStats() {
      try {
        const res = await this.$http.get('/dashboard/stats')
        const data = res.data
        this.cards[0].value = data.totalMaterials || 0
        this.cards[1].value = data.lowStockCount || 0
        this.cards[2].value = data.todayInQuantity || 0
        this.cards[3].value = data.todayOutQuantity || 0
      } catch (e) {}
    },
    async fetchTrend() {
      try {
        const res = await this.$http.get('/dashboard/trend?days=7')
        const data = res.data
        const dates = data.map(d => d.date)
        const inData = data.map(d => d.inQuantity)
        const outData = data.map(d => d.outQuantity)

        this.trendChart = echarts.init(this.$refs.trendChart)
        this.trendChart.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['入库', '出库'] },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value' },
          series: [
            { name: '入库', type: 'line', smooth: true, data: inData, color: '#67C23A' },
            { name: '出库', type: 'line', smooth: true, data: outData, color: '#F56C6C' }
          ]
        })
      } catch (e) {}
    },
    async fetchPie() {
      try {
        const res = await this.$http.get('/dashboard/pie')
        const data = res.data

        this.pieChart = echarts.init(this.$refs.pieChart)
        this.pieChart.setOption({
          tooltip: { trigger: 'item' },
          legend: { orient: 'vertical', left: 'left' },
          series: [{
            type: 'pie',
            radius: ['40%', '65%'],
            avoidLabelOverlap: true,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            emphasis: { label: { show: true } },
            data: data
          }]
        })
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.stat-card { display: flex; align-items: center; }
.stat-icon { font-size: 48px; margin-right: 20px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-title { color: #909399; margin-top: 5px; }
</style>