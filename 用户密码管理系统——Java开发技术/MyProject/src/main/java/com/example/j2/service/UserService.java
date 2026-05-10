package com.example.j2.service;

import com.example.j2.entity.User;
import com.example.j2.exception.CustomException;
import com.example.j2.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;


    //登录
    public User login(User user) {
        User dbuser = userMapper.selectByUsername(user.getUsername());
        if (dbuser == null) {
            //查询不到该用户
            throw new CustomException("账号或密码错误");
        }
        //查询到则比较密码
        if(!user.getPassword().equals(dbuser.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        //登录成功
        return dbuser;
    }
    public void add(User user) {
         userMapper.insert(user);
    }
}
