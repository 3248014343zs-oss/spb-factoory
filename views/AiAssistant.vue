<template>
  <div class="ai-assistant">
    <!-- API Key 配置对话框 -->
    <el-dialog title="AI 助手配置" :visible.sync="showConfigDialog" width="500px" :close-on-click-modal="false" :show-close="false">
      <el-alert title="首次使用需配置 API Key" type="info" :closable="false" style="margin-bottom: 15px"></el-alert>
      <el-form label-width="100px">
        <el-form-item label="API Key">
          <el-input v-model="apiKey" type="password" placeholder="请输入您的 OpenAI API Key" show-password></el-input>
        </el-form-item>
        <el-form-item label="API 地址">
          <el-input v-model="apiUrl" placeholder="默认：https://api.openai.com/v1/chat/completions"></el-input>
        </el-form-item>
        <el-form-item label="模型">
          <el-select v-model="model" placeholder="选择模型" style="width: 100%">
            <el-option label="GPT-3.5-Turbo" value="gpt-3.5-turbo"></el-option>
            <el-option label="GPT-4" value="gpt-4"></el-option>
            <el-option label="GPT-4-Turbo" value="gpt-4-turbo"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="系统提示词">
          <el-input
            v-model="systemPrompt"
            type="textarea"
            rows="3"
            placeholder="可选，设置AI的角色和行为"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="saveConfig">保存并启用</el-button>
      </div>
    </el-dialog>

    <!-- 聊天界面 -->
    <el-card class="chat-card" v-if="!showConfigDialog">
      <div slot="header">
        <span><i class="el-icon-magic-stick"></i> AI 助手</span>
        <el-button style="float: right" type="text" @click="openConfig">重新配置</el-button>
        <el-button style="float: right; margin-right: 10px" type="text" @click="clearHistory">清空对话</el-button>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
          <div class="avatar">
            <i :class="msg.role === 'user' ? 'el-icon-user' : 'el-icon-service'"></i>
          </div>
          <div class="content">
            <div class="message-text">{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="avatar"><i class="el-icon-service"></i></div>
          <div class="content typing">
            <span>AI 正在思考</span>
            <span class="dot">.</span><span class="dot">.</span><span class="dot">.</span>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="输入您的问题，例如：当前库存预警的物料有哪些？库存低于50的物料有哪些？"
          v-model="inputMessage"
          @keyup.ctrl.enter="sendMessage"
        ></el-input>
        <el-button type="primary" @click="sendMessage" :loading="loading" class="send-btn">发送</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'AiAssistant',
  data() {
    return {
      showConfigDialog: true,
      apiKey: '',
      apiUrl: 'https://api.openai.com/v1/chat/completions',
      model: 'gpt-3.5-turbo',
      systemPrompt: '你是一个专业的工厂物料管理助手。你可以帮助用户查询物料信息、解释库存预警原因、给出出入库建议、分析报表等。回答要简洁、专业、实用。',
      messages: [
        { role: 'assistant', content: '您好！我是您的物料管理 AI 助手。配置好 API Key 后，您就可以向我提问了。\n\n我可以帮助您：\n• 查询物料库存信息\n• 分析库存预警原因\n• 给出出入库建议\n• 解答物料管理问题' }
      ],
      inputMessage: '',
      loading: false
    }
  },
  created() {
    this.loadConfig()
  },
  methods: {
    loadConfig() {
      const savedKey = localStorage.getItem('ai_api_key')
      const savedUrl = localStorage.getItem('ai_api_url')
      const savedModel = localStorage.getItem('ai_model')
      const savedPrompt = localStorage.getItem('ai_system_prompt')
      if (savedKey) {
        this.apiKey = savedKey
        this.apiUrl = savedUrl || this.apiUrl
        this.model = savedModel || this.model
        this.systemPrompt = savedPrompt || this.systemPrompt
        this.showConfigDialog = false
      }
    },
    saveConfig() {
      if (!this.apiKey) {
        this.$message.error('请输入 API Key')
        return
      }
      localStorage.setItem('ai_api_key', this.apiKey)
      localStorage.setItem('ai_api_url', this.apiUrl)
      localStorage.setItem('ai_model', this.model)
      localStorage.setItem('ai_system_prompt', this.systemPrompt)
      this.$message.success('配置已保存')
      this.showConfigDialog = false
    },
    openConfig() {
      this.showConfigDialog = true
    },
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      const userMsg = this.inputMessage.trim()
      this.messages.push({ role: 'user', content: userMsg })
      this.inputMessage = ''
      this.loading = true
      this.scrollToBottom()

      try {
        // 构建历史消息（保留最近10条作为上下文）
        const historyMessages = this.messages.slice(-10).map(m => ({
          role: m.role,
          content: m.content
        }))

        const response = await fetch('/api/ai/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
          },
          body: JSON.stringify({
            message: userMsg,
            apiKey: this.apiKey,
            apiUrl: this.apiUrl,
            model: this.model,
            systemPrompt: this.systemPrompt,
            history: historyMessages
          })
        })

        const data = await response.json()
        if (data.code === 200) {
          this.messages.push({ role: 'assistant', content: data.data.reply })
        } else {
          this.messages.push({ role: 'assistant', content: 'AI 响应失败：' + (data.msg || '未知错误，请检查 API Key 是否正确') })
        }
      } catch (err) {
        console.error('AI 请求失败:', err)
        this.messages.push({ role: 'assistant', content: '网络错误，请检查 API Key 是否正确，或稍后重试。' })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    clearHistory() {
      this.messages = [
        { role: 'assistant', content: '对话已清空。您好！我是您的物料管理 AI 助手。\n\n我可以帮助您：\n• 查询物料库存信息\n• 分析库存预警原因\n• 给出出入库建议\n• 解答物料管理问题' }
      ]
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) container.scrollTop = container.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
.ai-assistant {
  padding: 20px;
  height: calc(100vh - 100px);
}
.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chat-card >>> .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}
.message {
  display: flex;
  margin-bottom: 15px;
  animation: fadeIn 0.3s ease;
}
.message.user {
  justify-content: flex-end;
}
.message.user .content {
  background: #409eff;
  color: white;
  border-radius: 18px 18px 4px 18px;
}
.message.assistant .content {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 18px 18px 18px 4px;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  flex-shrink: 0;
}
.message.user .avatar {
  order: 2;
  margin-right: 0;
  margin-left: 10px;
  background: #409eff;
  color: white;
}
.content {
  max-width: 70%;
  padding: 10px 15px;
  word-break: break-word;
  line-height: 1.5;
}
.message-text {
  white-space: pre-wrap;
}
.typing {
  color: #909399;
}
.typing .dot {
  animation: blink 1.4s infinite;
  animation-fill-mode: both;
}
.typing .dot:nth-child(2) { animation-delay: 0.2s; }
.typing .dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes blink {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.chat-input {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
.chat-input .el-textarea {
  flex: 1;
}
.send-btn {
  align-self: flex-end;
  height: 40px;
}
</style>