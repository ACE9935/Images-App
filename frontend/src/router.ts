import { createWebHistory, createRouter } from 'vue-router'

import HomeView from './views/HomeView.vue'
import SigninView from './views/SigninView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/signin', component: SigninView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
