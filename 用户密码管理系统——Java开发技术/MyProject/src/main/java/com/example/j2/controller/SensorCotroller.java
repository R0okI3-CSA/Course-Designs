package com.example.j2.controller;

import com.example.j2.entity.Sensor;
import com.example.j2.service.SensorService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.j2.entity.Result;

import javax.annotation.Resource;
@CrossOrigin//允许以下所有方法跨域（跨端口）以确保前后端联系
@RestController
@RequestMapping("/sensor")
public class SensorCotroller {
    @Autowired
    SensorService sensorService;
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "5") int pageSize,
                           @RequestParam String name,
                           @RequestParam String username) {
        PageInfo<Sensor> sensorPageInfo = sensorService.selectPage(pageNum, pageSize, name, username);
        return Result.success(sensorPageInfo);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Sensor sensor, @RequestParam String userid) {
        sensorService.add(sensor, userid);
        return Result.success();
    }
    @PutMapping("/update")//根据sensorid编辑这条数据
    public Result update(@RequestBody Sensor sensor){//通过该注解接收从前端传来的Json数据（一定要是JSON的）
        sensorService.updateById(sensor);
        return Result.success();
    }
    @DeleteMapping ("/delete/{sensorid}")//根据sensorid删除这条数据
    public Result delete(@PathVariable int sensorid){
        sensorService.deleteById(sensorid);
        return Result.success();//返回成功信息
    }
}
