import axios from 'axios'

const http = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080/api',
  timeout: 10000
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
}, error => Promise.reject(error))

http.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        localStorage.clear()
        window.location.href = '/login'
      }
      return Promise.reject(data)
    }
    return Promise.reject(error)
  }
)

export const user = {
  login: (data) => http.post('/user/login', data),
  register: (data) => http.post('/user/register', data),
  getProfile: () => http.get('/user/profile'),
  updateProfile: (data) => http.put('/user/profile', data),
  updatePassword: (data) => http.put('/user/password', data),
  getMyLogs: () => http.get('/logs/my')
}

export const invite = {
  generate: (targetRole) => http.post('/invite/generate', null, { params: { targetRole } }),
  use: (code) => http.post('/invite/use', null, { params: { code } }),
  getMyCodes: () => http.get('/invite/my-codes')
}

export const employee = {
  list: () => http.get('/employee/list'),
  downgrade: (uid) => http.put(`/employee/downgrade/${uid}`),
  delete: (uid) => http.delete(`/employee/delete/${uid}`)
}

export const material = {
  list: () => http.get('/material/list'),
  add: (data) => http.post('/material', data),
  update: (id, data) => http.put(`/material/${id}`, data),
  delete: (id) => http.delete(`/material/${id}`),
  updateStock: (id, type, quantity) => http.put(`/material/${id}/stock`, null, { params: { type, quantity } })
}

export const category = {
  list: () => http.get('/material-category/list'),
  add: (data) => http.post('/material-category', data),
  update: (id, data) => http.put(`/material-category/${id}`, data),
  delete: (id) => http.delete(`/material-category/${id}`)
}

export const inventory = {
  batch: (data) => http.post('/inventory/batch', data),
  single: (data) => http.post('/inventory/single', data),
  page: (params) => http.get('/inventory-record/page', { params })
}

export const order = {
  list: () => http.get('/inventory-order/list'),
  submit: (data) => http.post('/inventory-order', data),
  audit: (orderId, status, remark) => http.put(`/inventory-order/${orderId}/audit`, null, { params: { status, remark } })
}

export const alertReport = {
  list: () => http.get('/alert-report/list'),
  export: () => http.get('/alert-report/export', { responseType: 'blob' })
}

export const report = {
  inventoryTrend: (params) => http.get('/report/inventory-trend', { params }),
  stockStats: (params) => http.get('/report/stock-stats', { params }),
  exportStockStats: (params) => http.get('/report/export-stock-stats', { params, responseType: 'blob' })
}

export const log = {
  list: () => http.get('/logs/list'),
  listByUser: (userId) => http.get(`/logs/user/${userId}`)
}

export const aiChat = (data) => http.post('/ai/chat', data)

export default http