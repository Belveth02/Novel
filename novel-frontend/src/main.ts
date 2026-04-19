import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createHead } from '@vueuse/head'

import App from './App.vue'
import router from './router'
import store from './store'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(store)
app.use(router)
app.use(createHead())
app.use(ElementPlus, { locale: zhCn })

// 全局图片错误处理指令
app.directive('img-error', {
  mounted(el, binding) {
    const defaultSrc = binding.value || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZWVlZWVlIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0iIzk5OSI+Tm8gSW1hZ2U8L3RleHQ+PC9zdmc+'

    const handleError = () => {
      el.src = defaultSrc
      el.removeEventListener('error', handleError)
    }

    el.addEventListener('error', handleError)

    // 清理事件监听器
    el._vueImgErrorHandler = handleError
  },
  beforeUnmount(el) {
    if (el._vueImgErrorHandler) {
      el.removeEventListener('error', el._vueImgErrorHandler)
      delete el._vueImgErrorHandler
    }
  }
})

app.mount('#app')