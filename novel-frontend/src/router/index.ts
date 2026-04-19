import { createRouter, createWebHistory } from 'vue-router'
import { isAdminLoggedIn } from '@/api/admin'

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
    },
    // 后台路由
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/views/admin/AdminLogin.vue')
    },
    {
      path: '/admin',
      name: 'admin-layout',
      component: () => import('@/views/admin/AdminLayout.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/AdminDashboard.vue')
        },
        {
          path: 'novels',
          name: 'admin-novels',
          component: () => import('@/views/admin/placeholder/AdminNovels.vue')
        },
        {
          path: 'chapters',
          name: 'admin-chapters',
          component: () => import('@/views/admin/placeholder/AdminChapters.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/placeholder/AdminUsers.vue')
        },
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('@/views/admin/placeholder/AdminCategories.vue')
        }
      ]
    }
  ]
})

// 路由守卫：检查管理员登录状态
router.beforeEach((to, from, next) => {
  // 检查是否访问后台路由
  const isAdminRoute = to.path.startsWith('/admin') && to.path !== '/admin/login'

  if (isAdminRoute && !isAdminLoggedIn()) {
    // 未登录，重定向到登录页
    next('/admin/login')
  } else if (to.path === '/admin/login' && isAdminLoggedIn()) {
    // 已登录但访问登录页，重定向到仪表盘
    next('/admin/dashboard')
  } else {
    // 正常放行
    next()
  }
})

export default router