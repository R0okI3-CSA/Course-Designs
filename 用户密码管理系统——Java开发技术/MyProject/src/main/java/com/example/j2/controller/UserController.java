package com.example.j2.controller;

import com.example.j2.entity.User;
import com.example.j2.mapper.UserMapper;
import com.example.j2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.j2.entity.Result;
import javax.annotation.Resource;
import com.example.j2.entity.Record;
import com.example.j2.service.RecordService;
import com.example.j2.entity.Blacklist;
import com.example.j2.mapper.BlacklistMapper;
import java.util.List;
import java.util.Date;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin//允许以下所有方法跨域（跨端口）以确保前后端联系
@RequestMapping("/User")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private RecordService recordService;

    @Resource
    private BlacklistMapper blacklistMapper;

    @Autowired//自动创建一个UserMapper实例并注入到userMapper中
    private UserMapper userMapper;
    @GetMapping("/user")//请求时要用到的数据都在地址栏里了，不用再额外送数据了
    public  List query(){//返回值变成List时，它返回的list会自动转成Json格式以便前端获取数据并进行渲染（该项目前后端分离）
        List<User> list = userMapper.find();
        System.out.println(list);
        return list;
    }

//    @PostMapping("/user")//请求时专门有一个请求体，存放要用到的数据
//    public String save(User user){
//        int i = userMapper.insert(user);//前端传过来的数据对象我们直接给它传到数据库里面去
//        if (i>0){
//            return"插入成功";
//        }else{
//            return"插入失败";
//        }
//    }
    private boolean containsSqlInjection(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("select") || 
               lowerInput.contains("union") || 
               lowerInput.contains("<?php");
    }

    private boolean containsSpecialChars(String input) {
        if (input == null) return false;
        return !Pattern.matches("^[a-zA-Z0-9_]+$", input);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        try {
            logger.info("Attempting login for user: {}", user.getUsername());
            
            // 先查询用户是否存在
            User dbuser = userMapper.selectByUsername(user.getUsername());
            if (dbuser != null) {
                // 检查用户是否在黑名单中
                Blacklist blacklist = blacklistMapper.selectByUserid(dbuser.getUserid());
                if (blacklist != null) {
                    logger.warn("Blocked user attempted to login: {}", user.getUsername());
                    return Result.error("该账号已被封禁");
                }
            }
            
            // 检查SQL注入
            if (containsSqlInjection(user.getUsername())) {
                // 先查询用户是否存在
                if (dbuser != null) {
                    // 将当前登录用户加入黑名单
                    Blacklist blacklist = new Blacklist();
                    blacklist.setUserid(dbuser.getUserid());
                    try {
                        blacklistMapper.insert(blacklist);
                        logger.warn("SQL injection attempt detected for user: {}", user.getUsername());
                    } catch (Exception e) {
                        logger.error("Error adding user to blacklist: {}", e.getMessage());
                    }
                }
                return Result.error("非法攻击");
            }

            // 检查特殊字符
            if (containsSpecialChars(user.getUsername())) {
                logger.warn("Special characters detected in username: {}", user.getUsername());
                return Result.error("用户名不能包含特殊字符");
            }
            
            if (dbuser != null) {
                // 创建登录记录
                Record record = new Record();
                record.setUserid(dbuser.getUserid());
                record.setLogindate(new Date());
                
                if (user.getPassword().equals(dbuser.getPassword())) {
                    // 密码匹配，登录成功
                    record.setLoginstate("permit");
                    try {
                        recordService.save(record);
                        logger.info("Login successful for user: {}", user.getUsername());
                        return Result.success(dbuser);
                    } catch (Exception e) {
                        logger.error("Error saving login record: {}", e.getMessage());
                        return Result.error("登录记录保存失败");
                    }
                } else {
                    // 密码不匹配，登录失败
                    record.setLoginstate("refuse");
                    try {
                        recordService.save(record);
                        logger.info("Login failed for user: {} - incorrect password", user.getUsername());
                        return Result.error("用户名或密码错误");
                    } catch (Exception e) {
                        logger.error("Error saving login record: {}", e.getMessage());
                        return Result.error("登录记录保存失败");
                    }
                }
            }
            logger.info("Login failed for user: {} - user not found", user.getUsername());
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage(), e);
            return Result.error("登录过程中发生错误");
        }
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        // 检查SQL注入
        if (containsSqlInjection(user.getUsername())) {
            // 先查询用户是否存在
            User dbuser = userMapper.selectByUsername(user.getUsername());
            if (dbuser != null) {
                // 将当前登录用户加入黑名单
                Blacklist blacklist = new Blacklist();
                blacklist.setUserid(dbuser.getUserid());
                try {
                    blacklistMapper.insert(blacklist);
                    logger.warn("SQL injection attempt detected during registration for user: {}", user.getUsername());
                } catch (Exception e) {
                    logger.error("Error adding user to blacklist: {}", e.getMessage());
                }
            }
            return Result.error("非法攻击");
        }

        // 检查特殊字符
        if (containsSpecialChars(user.getUsername())) {
            logger.warn("Special characters detected in username during registration: {}", user.getUsername());
            return Result.error("用户名不能包含特殊字符");
        }

        userService.add(user);
        return Result.success();
    }

    @PostMapping("/addToBlacklist")
    public Result addToBlacklist(@RequestBody Blacklist blacklist) {
        try {
            blacklistMapper.insert(blacklist);
            return Result.success();
        } catch (Exception e) {
            logger.error("Error adding user to blacklist: {}", e.getMessage());
            return Result.error("加入黑名单失败");
        }
    }

}
