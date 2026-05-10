package com.example.j2.service;

import com.example.j2.entity.Sensor;
import com.example.j2.mapper.SensorMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SensorService {
    @Autowired
    SensorMapper sensorMapper;


    //pageNum是当前页码，pageSize是每页个数,PageInfo会返回total(数据总数)，list(数据列表)
    public PageInfo<Sensor> selectPage(int pageNum, int pageSize, String name, String username) {
        PageHelper.startPage(pageNum, pageSize);
        List<Sensor> sensorList = sensorMapper.selectByUsername(username, name);
        return PageInfo.of(sensorList);
    }
    //新增数据
    public void add(Sensor sensor, String userid) {
        sensorMapper.insert(sensor);
        sensorMapper.insertUserSensor(userid, sensor.getSensorid());
    }

    //根据sensorid编辑这条数据
    public void updateById(Sensor sensor) {
        sensorMapper.updateById(sensor);
    }

    //根据sensorid删除这条数据
    public void deleteById(int sensorid) {
        sensorMapper.deleteUserSensorById(sensorid);
        sensorMapper.deleteById(sensorid);
    }
}
