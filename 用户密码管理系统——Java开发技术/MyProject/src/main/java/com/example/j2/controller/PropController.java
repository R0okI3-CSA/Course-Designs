package com.example.j2.controller;


import com.example.j2.entity.Prop;
import com.example.j2.entity.Sensor;
import com.example.j2.service.PropService;
import com.example.j2.service.SensorService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.j2.entity.Result;

import javax.annotation.Resource;
@CrossOrigin//允许以下所有方法跨域（跨端口）以确保前后端联系
@RestController
@RequestMapping("/prop")
public class PropController {
    @Autowired
    PropService propService;

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "5") int pageSize, //接收前端传的值。默认值（1，5）
                             @RequestParam String name)

    {
        PageInfo<Prop> propPageInfo = propService.selectPage(pageNum, pageSize,name);//取出return过来的sensor列表装进sensorPageInfo
        return Result.success(propPageInfo);//返回一个pageinfo对象并setdata且有“200，请求成功”字段
    }
}
