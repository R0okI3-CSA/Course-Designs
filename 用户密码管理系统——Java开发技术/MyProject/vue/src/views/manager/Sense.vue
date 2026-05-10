<template>
  <div>
    <!--查询窗口-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input  v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="请输入用户名称进行查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <!--新增按钮-->
    <div class="card" style="margin-bottom: 10px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </div>
      <!--数据显示区域-->
      <el-table stripe :data="data.tableData">
        <el-table-column label="编号" prop="sensorid"></el-table-column>
        <el-table-column label="用户名" prop="sensorname"></el-table-column>
        <el-table-column label="密码" prop="manufact"></el-table-column>
        <el-table-column label="安全级别" prop="model"></el-table-column>
        <el-table-column label="日期" prop="manudate"></el-table-column>
        <el-table-column label="备注" prop="span"></el-table-column>
        <el-table-column label="操作" align="center" width="160">
          <template v-slot="scope">
            <!--每一栏数据的操作按钮-->
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="del(scope.row.sensorid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!--翻页功能   这是 Element UI 的分页组件，用于在页面上显示分页导航。-->
    <div class="card">
      <el-pagination background layout="prev, pager, next"
                     v-model:page-size="data.pageSize"
                     v-model:current-page="data.pageNum"
                     :total="data.total"
                     @current-change="handleCurrentChange"/>
    </div>
    <!--增改信息窗口（默认隐藏）-->
    <el-dialog width="35%" v-model="data.formVisible" title="新增用户名密码">
        <el-form :model="data.form" label-width="100px" label-position="right">
           <el-form-item label="用户名" >
             <el-input v-model="data.form.sensorname" autocomplete="off"/>
           </el-form-item>
          <el-form-item label="密码" >
            <el-input v-model="data.form.manufact" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="安全级别" >
            <el-input v-model="data.form.model" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="日期" >
            <el-input v-model="data.form.manudate" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="备注" >
            <el-input v-model="data.form.span" autocomplete="off"/>
          </el-form-item>
        </el-form>
        <template #footer >
          <span class="dialog-footer">
            <el-button @click="data.formVisible=false">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
          </span>
        </template>
    </el-dialog>

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

//页面用到的数据暂存变量及其默认值    ----状态管理对象
const data = reactive({
  name:'',
  tableData: [],
  total:0,
  pageNum:1,//当前页码
  pageSize:5,//每页个数
  formVisible:false,//默认不展示
  form:{
    sensorid:'',
    sensorname:'',
    manufact:'',
    model:'',
    manudate:'',
    span:''
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

  request.get('/sensor/selectPage',{
    params:{//传给后台的数据
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name,
      username: username
    }
  }).then(res=>{
    console.log(res)
    data.tableData = res.data?.list || [] //加问号可以在data.list为null时不报错
    data.total = res.data?.total || 0
  })
}
//调用load方法获取后台数据
load()
//新增数据函数
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

//更新数据函数
const handleEdit = (row) => {
  console.log(row)
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}
//删除数据函数 Elentment Plus 的标签结合Axios发送http请求
const del = (sensorid) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/sensor/delete/'+sensorid).then(res=>{
      if(res.code==='200'){//删除成功
        ElMessage.success("删除成功！")
        data.formVisible=false //关闭窗口
        //重新获取数据
        load()}else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(res => {
    ElMessage({
      type:'info',
      message:'删除取消',
    })
  })
}

//保存数据到后台函数
const save=()=>{
  const userid = localStorage.getItem('userid')
  request.request({
    url:data.form.sensorid ? '/sensor/update':'/sensor/add',//id有没有?有就是编辑操作(使用put)，没有就是新增操作(使用post)
    method:data.form.sensorid ?'PUT':'POST',
    data:data.form,
    params: {
      userid: userid
    }
  }).then(res=>{//res为返回值
    if(res.code==='200'){//插入或者更新成功
      ElMessage.success("保存成功！")
      data.formVisible=false //关闭窗口
      //重新获取数据
      load()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

//翻页函数
const handleCurrentChange=(res)=>{//返回值res为当前页码（第几页），但其实没用到
load()//翻页时重新加载数据即可
}

//查询重置函数
const reset=()=>{
  data.name=''
  load()
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

.el-button--danger {
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
}

.el-button--danger:hover {
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