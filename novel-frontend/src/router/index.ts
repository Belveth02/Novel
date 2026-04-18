import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomePage.vue')
    },
    {
      path: '/novels',
      name: 'novel-list',
      component: () => import('@/views/NovelList.vue')
    },
    {
      path: '/novels/:id',
      name: 'novel-detail',
      component: () => import('@/views/NovelDetail.vue')
    },
    {
      path: '/chapters/:id',
      name: 'chapter-read',
      component: () => import('@/views/ChapterRead.vue')
    },
    {
      path: '/user/favorites',
      name: 'user-favorites',
      component: () => import('@/views/UserFavorites.vue')
    }
  ]
})

export default router