<template>
  <div>
    <div class="welcome-card">
      <div class="welcome-text">欢迎您，{{ username}}，您的信息已安全保护 </div>
    </div>
    <!--查询窗口-->
    <div class="card" style="margin-bottom: 10px;">
      <el-date-picker
        v-model="data.logindate"
        type="date"
        placeholder="选择日期"
        style="width: 200px; margin-right: 10px"
        value-format="YYYY-MM-DD"
      />
      <el-select v-model="data.loginstate" placeholder="登录状态" style="width: 200px; margin-right: 10px">
        <el-option label="允许" value="permit" />
        <el-option label="拒绝" value="refuse" />
        <el-option label="禁止" value="ban" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 10px">
      <!--数据显示区域-->
      <el-table stripe :data="data.tableData">
        <el-table-column label="记录编号" prop="recordid"></el-table-column>
        <el-table-column label="用户编号" prop="userid"></el-table-column>
        <el-table-column label="用户名" prop="username"></el-table-column>
        <el-table-column label="登录时间" prop="logindate"></el-table-column>
        <el-table-column label="登录状态" prop="loginstate">
          <template #default="scope">
            <el-tag :type="getLoginStateType(scope.row.loginstate)">
              {{ getLoginStateText(scope.row.loginstate) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!--翻页功能-->
    <div class="card">
      <el-pagination background layout="prev, pager, next"
                     v-model:page-size="data.pageSize"
                     v-model:current-page="data.pageNum"
                     :total="data.total"
                     @current-change="handleCurrentChange"/>
    </div>
  </div>
  <div class="warn-text">注意：本系统采用主动防御机制，请勿尝试非法攻击以免账户封禁！！！</div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessage} from "element-plus";

const username = localStorage.getItem('username')
//const password = localStorage.getItem('password')

const data = reactive({
  logindate: '',
  loginstate: '',
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5
})

// 获取登录状态对应的标签类型
const getLoginStateType = (state) => {
  switch (state) {
    case 'permit':
      return 'success'
    case 'refuse':
      return 'danger'
    case 'ban':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取登录状态对应的文本
const getLoginStateText = (state) => {
  switch (state) {
    case 'permit':
      return '允许'
    case 'refuse':
      return '拒绝'
    case 'ban':
      return '禁止'
    default:
      return '未知'
  }
}

// 加载数据
const load = () => {
  const userid = localStorage.getItem('userid')
  request.get('/record/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      logindate: data.logindate,
      loginstate: data.loginstate,
      userid: userid
    }
  }).then(res => {
    data.tableData = res.data?.list || []
    data.total = res.data?.total || 0
  })
}

// 重置查询条件
const reset = () => {
  data.logindate = ''
  data.loginstate = ''
  load()
}

// 翻页
const handleCurrentChange = () => {
  load()
}

// 初始加载
load()
</script>

<style scoped>
.welcome-card {
  line-height: 30px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.welcome-text {
  color: #000000;
  font-size: 18px;
  font-family: 'sans-serif';
}

.card {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-button--primary {
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
}

.el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.el-button--info {
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
}

.el-button--info:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.el-pagination {
  --el-pagination-button-color: #409EFF;
  --el-pagination-hover-color: #66b1ff;
  --el-pagination-button-bg-color: #ffffff;
  --el-pagination-button-disabled-color: #c0c4cc;
  --el-pagination-button-disabled-bg-color: #ffffff;
  --el-pagination-hover-bg-color: #ecf5ff;
}

.el-pagination .el-pager li.active {
  background-color: #409EFF;
  color: white;
}

.el-pagination .el-pager li:hover {
  color: #66b1ff;
}

.warn-text {
  color: white;
  font-size: 25px;
  font-weight: bold;
  font-family: 'sans-serif';
}
</style>