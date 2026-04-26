<template>
  <el-popover placement="bottom" width="360" trigger="click" @show="fetchNotifications">
    <div slot="reference" class="notification-bell">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0">
        <i class="el-icon-bell"></i>
      </el-badge>
    </div>
    <div class="notification-list">
      <div class="notification-header">
        <span>通知</span>
        <el-button type="text" size="mini" @click="markAllRead" v-if="unreadCount > 0">全部已读</el-button>
      </div>
      <div v-if="notifications.length === 0" class="empty">暂无通知</div>
      <div v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: item.isRead === 0 }" @click="readNotification(item)">
        <div class="title">{{ item.title }}</div>
        <div class="content">{{ item.content }}</div>
        <div class="time">{{ formatTime(item.createTime) }}</div>
      </div>
    </div>
  </el-popover>
</template>

<script>
export default {
  name: 'NotificationBell',
  data() {
    return { notifications: [], unreadCount: 0 }
  },
  mounted() {
    this.fetchUnreadCount()
    setInterval(() => this.fetchUnreadCount(), 30000)
  },
  methods: {
    async fetchNotifications() {
      try {
        const res = await this.$http.get('/notification/unread')
        this.notifications = res.data
        this.unreadCount = this.notifications.filter(n => n.isRead === 0).length
      } catch (e) {}
    },
    async fetchUnreadCount() {
      try {
        const res = await this.$http.get('/notification/unread')
        this.unreadCount = res.data.filter(n => n.isRead === 0).length
      } catch (e) {}
    },
    async readNotification(item) {
      if (item.isRead === 0) {
        try {
          await this.$http.put(`/notification/${item.id}/read`)
          item.isRead = 1
          this.unreadCount--
        } catch (e) {}
      }
    },
    async markAllRead() {
      try {
        await this.$http.put('/notification/read-all')
        this.notifications.forEach(n => n.isRead = 1)
        this.unreadCount = 0
      } catch (e) {}
    },
    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>

<style scoped>
.notification-bell { font-size: 20px; cursor: pointer; margin-right: 20px; }
.notification-header { display: flex; justify-content: space-between; align-items: center; padding: 10px 15px; border-bottom: 1px solid #eee; }
.notification-item { padding: 12px 15px; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.notification-item.unread { background-color: #f0f9ff; }
.notification-item .title { font-weight: bold; margin-bottom: 4px; }
.notification-item .content { font-size: 13px; color: #666; }
.notification-item .time { font-size: 12px; color: #999; margin-top: 5px; }
.empty { padding: 30px; text-align: center; color: #999; }
</style>