<template>
  <div>
    <!--查询窗口-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input  v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="请输入工具名称进行查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 10px">
      <!--数据显示区域-->
      <el-table stripe :data="data.tableData">
        <el-table-column label="工具编号" prop="propid"></el-table-column>
        <el-table-column label="加密方式" prop="propname"></el-table-column>
        <el-table-column label="链接" prop="addr"></el-table-column>
        <el-table-column label="加密强度" prop="kind"></el-table-column>
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
</template>

<script setup>
//依赖引入
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {Search,Calendar} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";

const router = useRouter();

//页面用到的数据暂存变量及其默认值
const data = reactive({
  name:'',
  tableData: [],
  total:0,
  pageNum:1,//当前页码
  pageSize:5,//每页个数
  formVisible:false,//默认不展示
  form:{
    propid:'',
    propname:'',
    addr:'',
    propany:'',
    kind:'',
  }
})


//初始加载数据和关键字查询函数
const load=()=>{
  const username = localStorage.getItem('username')
  const userid = localStorage.getItem('userid')
  // 检查SQL注入
  if (data.name && (data.name.toLowerCase().includes('select') || 
                    data.name.toLowerCase().includes('union') || 
                    data.name.toLowerCase().includes('<?php'))) {
    // 将当前用户加入黑名单
    const blacklistData = {
      userid: userid
    };
    request.post('/User/addToBlacklist', blacklistData).then(res => {
      if(res.code === '200') {
        ElMessage.error('检测到非法攻击行为，您已被加入黑名单');
        router.push('/login');
      } else {
        ElMessage.error('系统错误');
      }
    }).catch(error => {
      console.error('加入黑名单失败:', error);
      ElMessage.error('系统错误');
    });
    return;
  }
  
  // 检查特殊字符
  if (data.name && !/^[a-zA-Z0-9_]+$/.test(data.name)) {
    ElMessage.error('用户名不能包含特殊字符');
    return;
  }

  request.get('/prop/selectPage',{
    params:{//传给后台的数据
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name
    }
  }).then(res=>{
    console.log(res)
    data.tableData = res.data?.list || [] //加问号可以在data.list为null时不报错
    data.total = res.data?.total || 0
  })
}
//调用load方法获取后台数据
load()

//查询重置函数
const reset=()=>{
  data.name=''
  load()
}



//翻页函数
const handleCurrentChange=(res)=>{//返回值res为当前页码（第几页），但其实没用到
  load()//翻页时重新加载数据即可
}


</script>

<style scoped>
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
</style>