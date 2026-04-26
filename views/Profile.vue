<template>
  <div class="profile-container">
    <el-card>
      <div slot="header"><span>个人中心</span></div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              action=""
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="uploadAvatar"
            >
              <img v-if="user.avatar" :src="user.avatar" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <p class="avatar-tip">点击更换头像</p>
          </div>
        </el-col>
        <el-col :span="18">
          <el-form :model="user" :rules="rules" ref="profileForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="user.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="user.nickname"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="user.email"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="user.phone"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile">保存修改</el-button>
              <el-button @click="showPasswordDialog = true">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog title="修改密码" :visible.sync="showPasswordDialog" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="updatePassword">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      user: { username: '', nickname: '', email: '', phone: '', avatar: '' },
      rules: {
        email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
      },
      showPasswordDialog: false,
      passwordForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value !== this.passwordForm.newPassword) callback(new Error('两次密码不一致'))
            else callback()
          }, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchProfile()
  },
  methods: {
    async fetchProfile() {
      const res = await this.$http.get('/user/profile')
      this.user = res.data
      if (this.user.avatar) localStorage.setItem('avatar', this.user.avatar)
    },
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) this.$message.error('只能上传图片文件')
      if (!isLt2M) this.$message.error('图片大小不能超过2MB')
      return isImage && isLt2M
    },
    async uploadAvatar(options) {
      const formData = new FormData()
      formData.append('file', options.file)
      try {
        const res = await this.$http.post('/upload/avatar', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        const avatarUrl = res.data
        await this.$http.put('/user/avatar', null, { params: { avatarUrl } })
        this.user.avatar = avatarUrl
        localStorage.setItem('avatar', avatarUrl)
        this.$message.success('头像更新成功')
      } catch (error) {
        this.$message.error('头像上传失败')
      }
    },
    async updateProfile() {
      try {
        await this.$http.put('/user/profile', {
          nickname: this.user.nickname,
          email: this.user.email,
          phone: this.user.phone
        })
        localStorage.setItem('nickname', this.user.nickname)
        this.$message.success('资料更新成功')
        this.fetchProfile()
      } catch (error) {}
    },
    async updatePassword() {
      try {
        await this.$refs.passwordForm.validate()
      } catch { return }
      try {
        await this.$http.put('/user/password', {
          oldPassword: this.passwordForm.oldPassword,
          newPassword: this.passwordForm.newPassword
        })
        this.$message.success('密码修改成功，请重新登录')
        this.showPasswordDialog = false
        localStorage.clear()
        this.$router.push('/login')
      } catch (error) {}
    }
  }
}
</script>

<style scoped>
.profile-container { padding: 20px; }
.avatar-section { text-align: center; }
.avatar-uploader { display: inline-block; }
.avatar { width: 150px; height: 150px; border-radius: 50%; object-fit: cover; border: 2px solid #ddd; }
.avatar-uploader-icon {
  font-size: 28px; color: #8c939d; width: 150px; height: 150px;
  line-height: 150px; text-align: center; border: 2px dashed #d9d9d9;
  border-radius: 50%; cursor: pointer;
}
.avatar-uploader-icon:hover { border-color: #409eff; }
.avatar-tip { margin-top: 10px; color: #909399; font-size: 12px; }
</style>