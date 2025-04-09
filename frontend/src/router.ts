import { createWebHistory, createRouter } from 'vue-router'

import HomeView from './views/HomeView.vue'
import SigninView from './views/SigninView.vue'
import RegisterFormView from './views/RegisterFormView.vue'
import ResetPasswordView from './views/ResetPasswordView.vue'
import ImageView from './views/ImageView.vue'
import ProfileView from './views/ProfileView.vue'
import ProfilesView from './views/ProfilesView.vue'
import SearchPageView from './views/SearchPageView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: SigninView },
  { path: '/signup', component: RegisterFormView },
  { path: '/reset-password', component: ResetPasswordView },
  { path: '/profile', component: ProfileView },
  { path: '/search', component: SearchPageView },
  {
    path: '/image',
    children: [
      {
        path: ':id',
        component: ImageView,
      }
    ]
  },
  {
    path: '/users',
    children: [
      {
        path: ':id',
        component: ProfilesView,
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
