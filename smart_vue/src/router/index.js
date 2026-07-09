import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store"

Vue.use(VueRouter)

// 基础路由（不需要登录的页面）
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
  // 如果直接访问 /，重定向到 /home
  {
    path: '/',
    redirect: '/home'
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 重置路由
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    routes
  })
}

// 拼装动态路由
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    const manageRoute = { 
      path: '/', 
      name: 'Manage', 
      component: () => import('../views/Manage.vue'), 
      redirect: "/home", 
      children: [
        { path: 'home', name: '首页', component: () => import('../views/Home.vue') },
        { path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
        { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')},
      ] 
    }
    const menus = JSON.parse(storeMenus)
    menus.forEach(item => {
      if (item.path) {
        let itemMenu = { 
          path: item.path.replace("/", ""), 
          name: item.name, 
          component: () => import('../views/' + item.pagePath + '.vue')
        }
        manageRoute.children.push(itemMenu)
      } else if(item.children && item.children.length) {
        item.children.forEach(child => {
          if (child.path) {
            let itemMenu = { 
              path: child.path.replace("/", ""), 
              name: child.name, 
              component: () => import('../views/' + child.pagePath + '.vue')
            }
            manageRoute.children.push(itemMenu)
          }
        })
      }
    })

    const currentRouteNames = router.getRoutes().map(v => v.name)
    if (!currentRouteNames.includes('Manage')) {
      router.addRoute(manageRoute)
    }
  }
}

// ========== 路由守卫 ==========
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem("user")
  const isLoginPage = to.path === '/login'
  const isRegisterPage = to.path === '/register'
  const is404 = to.path === '/404'

  // 如果未登录
  if (!user) {
    // 访问登录页或注册页 → 放行
    if (isLoginPage || isRegisterPage) {
      next()
    } else {
      // 其他页面 → 跳转到登录页
      next('/login')
    }
    return
  }

  // 已登录
  // 访问登录页或注册页 → 跳转到首页
  if (isLoginPage || isRegisterPage) {
    next('/home')
    return
  }

  // 访问404页面 → 检查是否有菜单权限
  if (is404) {
    const menus = localStorage.getItem("menus")
    if (!menus) {
      next('/login')
    } else {
      next()
    }
    return
  }

  // 检查是否有菜单权限
  const menus = localStorage.getItem("menus")
  if (!menus) {
    next('/login')
    return
  }

  // 检查路由是否存在
  if (!to.matched.length) {
    next('/404')
    return
  }

  // 正常访问
  next()
})

// 每次刷新页面都要重新设置路由
setRoutes()

export default router