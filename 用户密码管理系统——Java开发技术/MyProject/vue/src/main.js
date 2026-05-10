//程序入口
//模块化开发思想：import {模块中的某一个方法} from 模块
import { createApp } from 'vue'
//import id名为App的对象 from 模块
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import '@/assets/css/global.css'

axios.defaults.baseURL = "http://localhost:8080"
axios.prototype.$http = axios //这样当在其他地方使用axios时就不用导入了，直接用this.$http.get("/user").then((response)=>{this.tabledata = response.data})代替axios.get("/user  ")

const app = createApp(App)

app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
})
app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}