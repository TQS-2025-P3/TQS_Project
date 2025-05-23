import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import StationDetailView from '../views/StationDetailView.vue'
import BookingSuccessView from '../views/BookingSuccessView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/station/:id', component: StationDetailView },
  { path: '/booking-success', component: BookingSuccessView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
