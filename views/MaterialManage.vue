<template>
  <div class="material-manage">
    <el-card>
      <div slot="header">
        <span>物料管理</span>
        <el-button type="primary" size="small" style="float: right" @click="showAddDialog">新增物料</el-button>
      </div>

      <el-table :data="materialList" border v-loading="loading">
        <el-table-column prop="code" label="物料编码" width="120"></el-table-column>
        <el-table-column prop="name" label="物料名称" width="150"></el-table-column>
        <el-table-column prop="spec" label="规格" width="120"></el-table-column>
        <el-table-column label="例图" width="100">
          <template slot-scope="scope">
            <el-image v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :preview-src-list="[scope.row.imageUrl]" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"></el-image>
            <span v-else style="color: #999;">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="minThreshold" label="最低阈值" width="80"></el-table-column>
        <el-table-column prop="maxThreshold" label="最高阈值" width="80"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="editMaterial(scope.row)">编辑</el-button>
            <el-button type="text" @click="deleteMaterial(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="materialForm" :rules="rules" ref="materialForm" label-width="100px">
        <el-form-item label="物料编码" prop="code">
          <el-input v-model="materialForm.code"></el-input>
        </el-form-item>
        <el-form-item label="物料名称" prop="name">
          <el-input v-model="materialForm.name"></el-input>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="materialForm.spec"></el-input>
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="materialForm.unit" placeholder="如：个、箱"></el-input>
        </el-form-item>
        <el-form-item label="最低阈值" prop="minThreshold">
          <el-input-number v-model="materialForm.minThreshold" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="最高阈值" prop="maxThreshold">
          <el-input-number v-model="materialForm.maxThreshold" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="物料例图">
          <el-upload
            class="material-uploader"
            action=""
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :http-request="uploadMaterialImage"
          >
            <img v-if="materialForm.imageUrl" :src="materialForm.imageUrl" class="material-image" />
            <i v-else class="el-icon-plus material-uploader-icon"></i>
          </el-upload>
          <div class="el-upload__tip">支持jpg/png，不超过3MB</div>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveMaterial">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MaterialManage',
  data() {
    return {
      materialList: [],
      loading: false,
      showDialog: false,
      isEdit: false,
      materialForm: {
        id: null, code: '', name: '', spec: '', unit: '个',
        minThreshold: 0, maxThreshold: 100, imageUrl: ''
      },
      rules: {
        code: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
        minThreshold: [{ required: true, message: '请输入最低阈值', trigger: 'blur' }],
        maxThreshold: [{ required: true, message: '请输入最高阈值', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() { return this.isEdit ? '编辑物料' : '新增物料' }
  },
  created() {
    this.fetchMaterialList()
  },
  methods: {
    async fetchMaterialList() {
      this.loading = true
      try {
        const res = await this.$http.get('/material/list')
        this.materialList = res.data
      } catch (e) {} finally { this.loading = false }
    },
    showAddDialog() {
      this.isEdit = false
      this.materialForm = { id: null, code: '', name: '', spec: '', unit: '个', minThreshold: 0, maxThreshold: 100, imageUrl: '' }
      this.showDialog = true
    },
    editMaterial(row) {
      this.isEdit = true
      this.materialForm = { ...row }
      this.showDialog = true
    },
    async deleteMaterial(id) {
      try {
        await this.$confirm('确定删除该物料吗？', '提示', { type: 'warning' })
        await this.$http.delete(`/material/${id}`)
        this.$message.success('删除成功')
        this.fetchMaterialList()
      } catch (e) {}
    },
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt3M = file.size / 1024 / 1024 < 3
      if (!isImage) this.$message.error('只能上传图片文件')
      if (!isLt3M) this.$message.error('图片大小不能超过3MB')
      return isImage && isLt3M
    },
    async uploadMaterialImage(options) {
      const formData = new FormData()
      formData.append('file', options.file)
      try {
        const res = await this.$http.post('/upload/material', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        this.materialForm.imageUrl = res.data
        this.$message.success('例图上传成功')
      } catch (error) {
        this.$message.error('例图上传失败')
      }
    },
    async saveMaterial() {
      try {
        await this.$refs.materialForm.validate()
      } catch { return }
      try {
        const data = { ...this.materialForm }
        if (this.isEdit) {
          await this.$http.put(`/material/${data.id}`, data)
        } else {
          await this.$http.post('/material', data)
        }
        this.$message.success(this.isEdit ? '编辑成功' : '添加成功')
        this.showDialog = false
        this.fetchMaterialList()
      } catch (error) {}
    }
  }
}
</script>

<style scoped>
.material-manage { padding: 20px; }
.material-uploader { display: inline-block; }
.material-image { width: 100px; height: 100px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd; }
.material-uploader-icon {
  font-size: 28px; color: #8c939d; width: 100px; height: 100px;
  line-height: 100px; text-align: center; border: 1px dashed #d9d9d9;
  border-radius: 4px; cursor: pointer;
}
.material-uploader-icon:hover { border-color: #409eff; }
</style>