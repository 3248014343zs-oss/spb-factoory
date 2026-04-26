<template>
  <div>
    <el-card>
      <div slot="header">
        <span>库存订单</span>
        <el-button type="primary" size="small" style="float: right" @click="openAddDialog">提交订单</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'IN' ? 'success' : 'danger'">{{ scope.row.type === 'IN' ? '入库' : '出库' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="materialId" label="物料ID" width="100"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantId" label="申请人" width="100"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150" align="center" v-if="role === 'WAREHOUSE' || role === 'ADMIN' || role === 'BOSS'">
          <template slot-scope="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="mini" type="success" @click="auditOrder(scope.row, 1)">通过</el-button>
              <el-button size="mini" type="danger" @click="auditOrder(scope.row, 2)">拒绝</el-button>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 提交订单弹窗 -->
    <el-dialog title="提交订单" :visible.sync="dialogVisible" width="450px">
      <el-form :model="form" :rules="rules" ref="orderForm" label-width="80px">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单号"></el-input>
        </el-form-item>
        <el-form-item label="订单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="IN">入库订单</el-radio>
            <el-radio label="OUT">出库订单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="物料ID" prop="materialId">
          <el-input-number v-model="form.materialId" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="2"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitLoading">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { order } from '@/api'

export default {
  name: 'Order',
  data() {
    return {
      list: [],
      loading: false,
      role: localStorage.getItem('role'),
      dialogVisible: false,
      submitLoading: false,
      form: { orderNo: '', type: 'IN', materialId: null, quantity: 1, remark: '' },
      rules: {
        orderNo: [{ required: true, message: '请输入订单号', trigger: 'blur' }],
        materialId: [{ required: true, message: '请填写物料ID', trigger: 'blur' }],
        quantity: [{ required: true, message: '请填写数量', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    getStatusText(status) {
      const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 0: 'info', 1: 'success', 2: 'danger' }
      return map[status] || 'info'
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await order.list()
        if (res.code === 200) {
          this.list = res.data
        }
      } finally {
        this.loading = false
      }
    },
    openAddDialog() {
      this.form = { orderNo: '', type: 'IN', materialId: null, quantity: 1, remark: '' }
      this.dialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.orderForm) this.$refs.orderForm.clearValidate()
      })
    },
    async submitOrder() {
      this.$refs.orderForm.validate(async (valid) => {
        if (!valid) return
        this.submitLoading = true
        try {
          const res = await order.submit(this.form)
          if (res.code === 200) {
            this.$message.success('订单提交成功')
            this.dialogVisible = false
            this.fetchList()
          } else {
            this.$message.error(res.msg)
          }
        } finally {
          this.submitLoading = false
        }
      })
    },
    async auditOrder(row, status) {
      const text = status === 1 ? '通过' : '拒绝'
      await this.$confirm(`确定${text}订单 ${row.orderNo} 吗？`, '提示', { type: 'warning' })
      const res = await order.audit(row.id, status, '')
      if (res.code === 200) {
        this.$message.success(`订单已${text}`)
        this.fetchList()
      } else {
        this.$message.error(res.msg)
      }
    }
  }
}
</script>