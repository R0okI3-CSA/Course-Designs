<template>
    <div>
        <div class="login-container">
            <div class="login-box">
                <div class="main-title">用户密码管理系统</div>
                <div class="login-title">登录</div>
                <el-form :model="data.form" ref="formRef" :rules="rules">
                    <el-form-item prop="username" >
                    <el-input  prefix-icon="User" v-model="data.form.username" placeholder="请输入用户名"/>
                    </el-form-item>
                    <el-form-item prop="password" >
                        <el-input  show-password prefix-icon="Lock" v-model="data.form.password" placeholder="请输入密码"/>
                    </el-form-item>

                    <el-form-item class="button-group">
                        <el-button class="login-btn" type="primary" @click="login">登录</el-button>
                        <el-button class="register-btn" type="primary" @click="userAdd">注册</el-button>
                    </el-form-item>
                </el-form> 
            </div>
        </div>
      <!--增改信息窗口（默认隐藏）-->
      <el-dialog width="35%" v-model="data.formVisible" title="新用户注册">
        <el-form :model="data.form" label-width="100px" label-position="right">
          <el-form-item label="用户名" >
            <el-input v-model="data.form.username" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="密码" >
            <el-input v-model="data.form.password" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="电话" >
            <el-input v-model="data.form.phone" autocomplete="off"/>
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
import { reactive,ref } from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import * as Json from "postcss";
import router from "@/router";

const data = reactive({
  form:{
    username:'',
    password:'',
    phone:''
  },
  i:0,
  formVisible:false,//默认不展示
})

// 添加验证函数
const validateInput = (input) => {
  // 使用正则表达式检查是否只包含字母、数字和下划线
  const regex = /^[a-zA-Z0-9_]+$/;
  if (!regex.test(input)) {
    ElMessage.error('输入不能包含特殊字符，只能使用字母、数字和下划线');
    return false;
  }
  return true;
}

const userAdd = () => {
  data.form = {}
  data.formVisible = true
}

const save=()=>{
  // 验证用户名、密码和电话号码
  if (!validateInput(data.form.username) || !validateInput(data.form.password) || !validateInput(data.form.phone)) {
    return;
  }
  
  request.request({
    url:'/User/add',
    method:'POST',
    data:data.form,
  }).then(res=>{
    if(res.code==='200'){
      ElMessage.success("注册成功！")
      data.formVisible=false
      load()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const rules=reactive({
    username:[{required:true,message:'请输入用户名',trigger:'blur'}],
    password:[{required:true,message:'请输入密码',trigger:'blur'}],
})

const formRef = ref()

const login = ()=>{
    formRef.value.validate((valid)=>{
       if(valid){
        // 验证用户名和密码
        if (!validateInput(data.form.username) || !validateInput(data.form.password)) {
          return;
        }
        
        request.post('/User/login',data.form).then(res=>{
            if(res.code === '200'){
             localStorage.setItem('username',res.data.username);
             localStorage.setItem('userid',res.data.userid);
             // 添加登录记录
             const recordData = {
               userid: res.data.userid,
               logindate: new Date().toISOString(),
               loginstate: 'permit'
             };
             request.post('/record/add', recordData).then(recordRes => {
               if(recordRes.code !== '200') {
                 console.error('登录记录保存失败');
               }
             }).catch(error => {
               console.error('登录记录保存失败:', error);
             });
             ElMessage.success('登录成功')
             router.push('/home')
            }else {
              // 处理特殊响应
              if(res.msg === '非法攻击') {
                ElMessage.error('检测到非法攻击行为，您已被加入黑名单');
                router.push('/login');
                return;
              }
              if(res.msg === '用户名不能包含特殊字符') {
                ElMessage.error('用户名不能包含特殊字符');
                return;
              }
              // 登录失败时也记录
              if(res.data && res.data.userid) {
                const recordData = {
                  userid: res.data.userid,
                  logindate: new Date().toISOString(),
                  loginstate: 'refuse'
                };
                request.post('/record/add', recordData).then(recordRes => {
                  if(recordRes.code !== '200') {
                    console.error('登录记录保存失败');
                  }
                }).catch(error => {
                  console.error('登录记录保存失败:', error);
                });
              }
              ElMessage.error(res.msg || '用户名或密码错误')
            }
        })
       }
    })
}

</script>

<style scoped>
.login-container{
    min-height: 100vh;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    background-image: url("@/assets/imgs/LoginBG.png");
    background-size: cover;
}
.login-box{
    width: 400px;
    height: 600px;
    background-color:rgba(0, 0, 0, 0.4);
    box-shadow: 0 0 10px rgba(0, 0, 0,0.1);
    padding: 40px;
    border-radius: 5px;
    margin-right: 350px;
}
.main-title {
    font-family: 'sans-serif';
    font-size: 30px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 60px;
    color: deepskyblue;
}
.login-title {
  font-family: 'sans-serif';
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: white;
}
.button-group {
    display: flex;
    justify-content: space-between;
    margin-top: 30px;
}
.login-btn, .register-btn {
    width: 45%;
    background-color: #409EFF;
    color: white;
    font-family: 'sans-serif';
    font-size: 16px;
}
.login-btn:hover, .register-btn:hover {
    background-color: #409EFF;
    border-color: #409EFF;
}

/* 输入框样式 */
.el-input__wrapper {
    background-color: rgba(255, 255, 255, 0.9);
}
.el-input__wrapper:hover {
    box-shadow: 0 0 0 1px #409EFF inset;
}
.el-input__inner::placeholder {
    color: #909399;
}

/* 按钮选中状态 */
.el-button--primary:focus {
    border-color: #409EFF;
    background-color: #409EFF;
}

/* 成功提示样式 */
.el-message--success {
    background-color: #409EFF;
    border-color: #409EFF;
}
.el-message--success .el-message__content {
    color: white;
}

/* 注册弹窗样式 */
.el-dialog {
    width: 50% !important;
    height: 500px;
    margin: 15vh auto;
    border: 2px solid #409EFF;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
}
.el-dialog__header {
    text-align: center;
    padding: 20px;
}
.el-dialog__title {
    font-size: 24px;
    color: #409EFF;
}
.el-dialog__headerbtn {
    display: none;
}
.el-dialog__body {
    padding: 30px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.el-form-item__label {
    font-size: 16px;
}
.el-input {
    width: 80%;
    margin: 0 auto;
    display: block;
}
.el-dialog__footer {
    text-align: center;
    padding: 20px;
}
.dialog-footer .el-button--primary {
    background-color: #409EFF;
    color: white;
    border-color: #409EFF;
}
.dialog-footer .el-button--primary:hover {
    background-color: #66b1ff;
    border-color: #66b1ff;
}
</style>