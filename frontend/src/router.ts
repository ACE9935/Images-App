import { createWebHistory, createRouter } from 'vue-router'

import HomeView from './views/HomeView.vue'
import SigninView from './views/SigninView.vue'
import RegisterFormView from './views/RegisterFormView.vue'
import ResetPasswordView from './views/ResetPasswordView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: SigninView },
  { path: '/signup', component: RegisterFormView },
  { path: '/reset-password', component: ResetPasswordView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
