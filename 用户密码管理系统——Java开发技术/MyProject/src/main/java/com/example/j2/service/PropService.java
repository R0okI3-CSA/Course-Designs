package com.example.j2.service;

import com.example.j2.entity.Prop;
import com.example.j2.entity.Sensor;
import com.example.j2.mapper.PropMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropService {
    @Autowired
    PropMapper propMapper;


    //PageInfo会返回total(数据总数)，list(数据列表)
    //pageNum是当前页码，pageSize是每页个数
    public PageInfo<Prop> selectPage(int pageNum, int pageSize, String name) {
        PageHelper.startPage(pageNum,pageSize);//实现分页
        List<Prop> propList = propMapper.selectAll(name);//拿到取到的列表装到sensorList列表里
        return PageInfo.of(propList);//返回sensor列表
    }
}
