<template>
  <div>
    <el-card>
      <div slot="header">
        <span>物料分类管理</span>
        <el-button type="primary" size="small" style="float: right" @click="openAddDialog">新增分类</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="分类名称"></el-table-column>
        <el-table-column prop="parentId" label="父分类ID" width="120">
          <template slot-scope="scope">
            {{ scope.row.parentId === 0 ? '顶级分类' : scope.row.parentId }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" :rules="rules" ref="categoryForm" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="父分类ID">
          <el-input-number v-model="form.parentId" :min="0" placeholder="0表示顶级分类" style="width: 100%"></el-input-number>
          <span style="font-size:12px; color:#909399">0表示顶级分类</span>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="submitLoading">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { category } from '@/api'

export default {
  name: 'Category',
  data() {
    return {
      list: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增分类',
      form: { name: '', parentId: 0, sort: 0 },
      editId: null,
      submitLoading: false,
      rules: {
        name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await category.list()
        if (res.code === 200) {
          this.list = Array.isArray(res.data) ? res.data : []
        } else {
          this.$message.error(res.msg || '获取分类列表失败')
          this.list = []
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        this.$message.error('网络错误，请检查后端服务')
        this.list = []
      } finally {
        this.loading = false
      }
    },
    openAddDialog() {
      this.dialogTitle = '新增分类'
      this.form = { name: '', parentId: 0, sort: 0 }
      this.editId = null
      this.dialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.categoryForm) this.$refs.categoryForm.clearValidate()
      })
    },
    openEditDialog(row) {
      this.dialogTitle = '编辑分类'
      this.form = { name: row.name, parentId: row.parentId, sort: row.sort }
      this.editId = row.id
      this.dialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.categoryForm) this.$refs.categoryForm.clearValidate()
      })
    },
    async saveCategory() {
      this.$refs.categoryForm.validate(async (valid) => {
        if (!valid) return
        this.submitLoading = true
        try {
          let res
          if (this.editId) {
            res = await category.update(this.editId, this.form)
          } else {
            res = await category.add(this.form)
          }
          if (res.code === 200) {
            this.$message.success(this.editId ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.fetchList()
          } else {
            this.$message.error(res.msg || '操作失败')
          }
        } catch (error) {
          console.error('保存分类失败:', error)
          this.$message.error('保存失败，请稍后重试')
        } finally {
          this.submitLoading = false
        }
      })
    },
    async deleteCategory(row) {
      try {
        await this.$confirm(`确定删除分类 "${row.name}" 吗？`, '提示', { type: 'warning' })
        const res = await category.delete(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.fetchList()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除分类失败:', error)
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>