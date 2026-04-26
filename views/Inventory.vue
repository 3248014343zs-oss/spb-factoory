<template>
  <div>
    <el-tabs v-model="activeTab">
      <!-- 单条操作 -->
      <el-tab-pane label="单条出入库" name="single">
        <el-card>
          <el-form :model="singleForm" label-width="80px" style="width: 400px">
            <el-form-item label="物料ID">
              <el-input-number v-model="singleForm.materialId" :min="1" style="width: 100%"></el-input-number>
            </el-form-item>
            <el-form-item label="操作类型">
              <el-radio-group v-model="singleForm.type">
                <el-radio label="IN">入库</el-radio>
                <el-radio label="OUT">出库</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="数量">
              <el-input-number v-model="singleForm.quantity" :min="1" style="width: 100%"></el-input-number>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="singleForm.remark" type="textarea" rows="2"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitSingle" :loading="singleLoading">提交</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 批量操作 -->
      <el-tab-pane label="批量出入库" name="batch">
        <el-card>
          <div style="margin-bottom: 15px">
            <el-button type="primary" size="small" @click="addBatchRow">添加记录</el-button>
            <el-button type="danger" size="small" @click="clearBatch">清空所有</el-button>
          </div>
          <el-table :data="batchRecords" border stripe>
            <el-table-column label="物料ID" width="150">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.materialId" :min="1" controls-position="right" style="width: 130px"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="操作类型" width="120">
              <template slot-scope="scope">
                <el-select v-model="scope.row.type">
                  <el-option label="入库" value="IN"></el-option>
                  <el-option label="出库" value="OUT"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" controls-position="right" style="width: 130px"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column label="备注">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" placeholder="选填"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="danger" @click="batchRecords.splice(scope.$index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 15px">
            <el-button type="primary" @click="submitBatch" :loading="batchLoading">批量提交</el-button>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 出入库记录查询 -->
    <el-card style="margin-top: 20px">
      <div slot="header">
        <span>出入库记录</span>
      </div>
      <el-form :inline="true" size="small">
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.materialId" placeholder="请输入" clearable></el-input>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryParams.type" placeholder="全部" clearable>
            <el-option label="入库" value="IN"></el-option>
            <el-option label="出库" value="OUT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRecords">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="records" border stripe v-loading="recordsLoading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="materialId" label="物料ID" width="100"></el-table-column>
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'IN' ? 'success' : 'danger'">{{ scope.row.type === 'IN' ? '入库' : '出库' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
        <el-table-column prop="operatorId" label="操作人ID" width="100"></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150"></el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180"></el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @current-change="handlePageChange"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script>
import { inventory } from '@/api'

export default {
  name: 'Inventory',
  data() {
    return {
      activeTab: 'single',
      singleForm: { materialId: null, type: 'IN', quantity: 1, remark: '' },
      singleLoading: false,
      batchRecords: [{ materialId: null, type: 'IN', quantity: 1, remark: '' }],
      batchLoading: false,
      records: [],
      recordsLoading: false,
      queryParams: { materialId: '', type: '' },
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  created() {
    this.searchRecords()
  },
  methods: {
    addBatchRow() {
      this.batchRecords.push({ materialId: null, type: 'IN', quantity: 1, remark: '' })
    },
    clearBatch() {
      this.batchRecords = [{ materialId: null, type: 'IN', quantity: 1, remark: '' }]
    },
    async submitSingle() {
      if (!this.singleForm.materialId) {
        this.$message.warning('请填写物料ID')
        return
      }
      this.singleLoading = true
      try {
        const res = await inventory.single(this.singleForm)
        if (res.code === 200) {
          this.$message.success('操作成功')
          this.singleForm = { materialId: null, type: 'IN', quantity: 1, remark: '' }
          this.searchRecords()
        } else {
          this.$message.error(res.msg)
        }
      } finally {
        this.singleLoading = false
      }
    },
    async submitBatch() {
      const valid = this.batchRecords.every(r => r.materialId && r.quantity > 0)
      if (!valid) {
        this.$message.warning('请完整填写所有记录')
        return
      }
      this.batchLoading = true
      try {
        const res = await inventory.batch({ records: this.batchRecords })
        if (res.code === 200) {
          this.$message.success(`成功处理 ${res.data} 条记录`)
          this.clearBatch()
          this.searchRecords()
        } else {
          this.$message.error(res.msg)
        }
      } finally {
        this.batchLoading = false
      }
    },
    async searchRecords() {
      this.recordsLoading = true
      try {
        const params = { page: this.pageNum, size: this.pageSize, ...this.queryParams }
        const res = await inventory.page(params)
        if (res.code === 200) {
          this.records = res.data.records
          this.total = res.data.total
        }
      } finally {
        this.recordsLoading = false
      }
    },
    resetQuery() {
      this.queryParams = { materialId: '', type: '' }
      this.pageNum = 1
      this.searchRecords()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.searchRecords()
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