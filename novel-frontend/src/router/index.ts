import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '@/store/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomePage.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresGuest: true }
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
      component: () => import('@/views/ChapterRead.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/favorites',
      name: 'user-favorites',
      component: () => import('@/views/UserFavorites.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/profile',
      name: 'user-profile',
      component: () => import('@/views/UserProfile.vue'),
      meta: { requiresAuth: true }
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
      meta: { requiresAdmin: true },
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/AdminDashboard.vue')
        },
        {
          path: 'novels',
          name: 'admin-novels',
          component: () => import('@/views/admin/NovelManage.vue')
        },
        {
          path: 'chapters',
          name: 'admin-chapters',
          component: () => import('@/views/admin/ChapterManage.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/UserManage.vue')
        },
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('@/views/admin/CategoryManage.vue')
        }
      ]
    },
    // 作者后台路由
    {
      path: '/author/login',
      name: 'author-login',
      component: () => import('@/views/author/AuthorLogin.vue')
    },
    {
      path: '/author',
      name: 'author-layout',
      component: () => import('@/views/author/AuthorLayout.vue'),
      redirect: '/author/dashboard',
      meta: { requiresAuthor: true },
      children: [
        {
          path: 'dashboard',
          name: 'author-dashboard',
          component: () => import('@/views/author/AuthorDashboard.vue')
        },
        {
          path: 'novels',
          name: 'author-novels',
          component: () => import('@/views/author/AuthorNovelList.vue')
        },
        {
          path: 'novels/create',
          name: 'author-novel-create',
          component: () => import('@/views/author/AuthorNovelEdit.vue')
        },
        {
          path: 'novels/edit/:id',
          name: 'author-novel-edit',
          component: () => import('@/views/author/AuthorNovelEdit.vue')
        },
        {
          path: 'chapters',
          name: 'author-chapters',
          component: () => import('@/views/author/AuthorChapterList.vue')
        },
        {
          path: 'comments',
          name: 'author-comments',
          component: () => import('@/views/author/AuthorCommentList.vue')
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFound.vue')
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  NProgress.start()

  const loggedIn = isLoggedIn()
  const adminToken = localStorage.getItem('admin_token')
  const authorToken = localStorage.getItem('author_token')

  // 检查路由元信息
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  const requiresAuthor = to.matched.some(record => record.meta.requiresAuthor)

  // 检查是否访问管理员路由
  const isAdminRoute = to.path.startsWith('/admin') && to.path !== '/admin/login'

  // 检查是否访问作者路由
  const isAuthorRoute = to.path.startsWith('/author') && to.path !== '/author/login'

  // 管理员路由守卫
  if (isAdminRoute && !adminToken) {
    next('/admin/login')
    return
  }

  // 作者路由守卫
  if (isAuthorRoute && !authorToken) {
    next('/author/login')
    return
  }

  // 已登录管理员访问登录页，重定向到仪表盘
  if (to.path === '/admin/login' && adminToken) {
    next('/admin/dashboard')
    return
  }

  // 已登录作者访问登录页，重定向到仪表盘
  if (to.path === '/author/login' && authorToken) {
    next('/author/dashboard')
    return
  }

  // 需要管理员权限但不是管理员
  if (requiresAdmin && !adminToken) {
    next('/admin/login')
    return
  }

  // 需要作者权限但不是作者
  if (requiresAuthor && !authorToken) {
    next('/author/login')
    return
  }

  // 需要登录的页面
  if (requiresAuth && !loggedIn) {
    next('/login')
    return
  }

  // 已登录用户访问登录/注册页，重定向到首页
  if (requiresGuest && loggedIn) {
    next('/')
    return
  }

  // 正常放行
  next()
})

router.afterEach(() => {
  NProgress.done()
})

router.onError(() => {
  NProgress.done()
})

export default router
