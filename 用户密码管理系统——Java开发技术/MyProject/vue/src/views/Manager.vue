<template>
  <div class="manager-container">
    <div class="header">
      <div class="header-left">
        <div class="logo-container">
          <div class="system-title">用户密码管理系统</div>
        </div>
      </div>
      <div class="header-right">
        <img src="@/assets/imgs/security.png" alt="" class="user-avatar">
        <span class="user-text">用户</span>
      </div>
    </div>

    <div class="main-content">
      <div class="sidebar">
        <el-menu
            router
            class="menu"
            :default-active="$route.path"
            :default-openeds="['/home', '2']"
            background-color="#1a1a1a"
            text-color="#ffffff"
            active-text-color="#409EFF"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><FolderOpened /></el-icon>
              <span>信息管理</span>
            </template>
            <el-menu-item index="/sense">
              <el-icon><Lock /></el-icon>
              <span>密码信息</span>
            </el-menu-item>
            <el-menu-item index="/prop">
              <el-icon><Link /></el-icon>
              <span>加密工具</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="login" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出系统</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const $route = useRoute()
console.log($route.path)

const logout = () => {
  localStorage.removeItem('username')
  localStorage.removeItem('password')
}
</script>

<style scoped>
.manager-container {
  min-height: 100vh;
  background-color: #000000;
  color: #ffffff;
}

.header {
  height: 60px;
  background-color: #1a1a1a;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #333;
}

.header-left {
  flex: 1;
}

.logo-container {
  padding-left: 20px;
  display: flex;
  align-items: center;
}

.logo-icon {
  font-size: 24px;
  color: #ffffff;
}

.system-title {
  font-weight: bold;
  font-family: YouYuan;
  font-size: 24px;
  margin-left: 5px;
  color: #ffffff;
}

.header-right {
  width: fit-content;
  padding-right: 20px;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
}

.user-text {
  margin-left: 5px;
  font-family: YouYuan;
  color: #ffffff;
}

.main-content {
  display: flex;
  font-family: YouYuan;
}

.sidebar {
  width: 200px;
  border-right: 1px solid #333;
  min-height: calc(100vh - 60px);
  background-color: #1a1a1a;
}

.menu {
  border: none;
}

.content {
  flex: 1;
  width: 0;
  background-color: #121212;
  padding: 20px;
}

/* 菜单样式 */
.el-menu {
  border-right: none;
}

.el-menu-item {
  color: #ffffff;
}

.el-menu-item.is-active {
  background-color: #2c2c2c !important;
}

.el-menu-item:hover {
  background-color: #2c2c2c !important;
  color: #409EFF !important;
}

.el-sub-menu__title {
  color: #ffffff;
}

.el-sub-menu__title:hover {
  background-color: #2c2c2c !important;
  color: #409EFF !important;
}

.el-menu--collapse {
  background-color: #1a1a1a;
}

.el-menu--popup {
  background-color: #1a1a1a;
}

.el-menu--popup .el-menu-item {
  color: #ffffff;
}

.el-menu--popup .el-menu-item:hover {
  background-color: #2c2c2c !important;
  color: #409EFF !important;
}

.el-menu--popup .el-menu-item.is-active {
  background-color: #2c2c2c !important;
  color: #409EFF !important;
}
</style>