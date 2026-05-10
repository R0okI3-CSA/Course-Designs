import {createRouter, createWebHistory} from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/login',
      children: [
        { path: 'home', name: 'Home', component: () => import('@/views/manager/Home.vue')},
        { path: 'sense', name: 'Sense', component: () => import('@/views/manager/Sense.vue')},
        { path: 'prop', name: 'Prop', component: () => import('@/views/manager/Prop.vue')},
      ]
    },
    {
      path:'/login',
      name:'Login',
      component:() => import('@/views/Login.vue'),
    }
  ]
})

export default router
