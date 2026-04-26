import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', component: Login, meta: { requiresAuth: false } },
  { path: '/register', component: () => import('@/views/Register.vue'), meta: { requiresAuth: false } },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
  { path: '/material', component: () => import('@/views/MaterialManage.vue'), meta: { requiresAuth: true } },
  { path: '/inventory', component: () => import('@/views/Inventory.vue'), meta: { requiresAuth: true } },
  { path: '/inventory-record', component: () => import('@/views/InventoryRecord.vue'), meta: { requiresAuth: true } },
  { path: '/order', component: () => import('@/views/Order.vue'), meta: { requiresAuth: true } },
  { path: '/alert', component: () => import('@/views/Alert.vue'), meta: { requiresAuth: true } },
  { path: '/report', component: () => import('@/views/Report.vue'), meta: { requiresAuth: true } },
  { path: '/log', component: () => import('@/views/Log.vue'), meta: { requiresAuth: true, roles: ['BOSS','ADMIN'] } },
  { path: '/employee', component: () => import('@/views/EmployeeManage.vue'), meta: { requiresAuth: true } },
  { path: '/invite-code', component: () => import('@/views/InviteCode.vue'), meta: { requiresAuth: true } },
  { path: '/category', component: () => import('@/views/Category.vue'), meta: { requiresAuth: true } },
  { path: '/ai-assistant', component: () => import('@/views/AiAssistant.vue'), meta: { requiresAuth: true } }
]

const router = new VueRouter({ mode: 'history', routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router