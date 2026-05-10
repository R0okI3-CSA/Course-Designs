package com.example.j2.mapper;

import com.example.j2.entity.Sensor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SensorMapper {
    @Select("select *from sensor where sensorname like concat('%',#{name},'%')")//模糊查询
    List<Sensor> selectAll(String name);//返回一个列表

    @Insert("insert into sensor (sensorname,manufact,model,manudate,span) values(#{sensorname},#{manufact},#{model},#{manudate},#{span})")
    @Options(useGeneratedKeys = true, keyProperty = "sensorid")  // 获取自动生成的sensorid
    void insert(Sensor sensor);

    @Insert("insert into user_sensor (userid, sensorid) values(#{userid}, #{sensorid})")
    void insertUserSensor(@Param("userid") String userid, @Param("sensorid") int sensorid);

    @Update("update sensor set sensorname=#{sensorname},manufact=#{manufact},model=#{model},manudate=#{manudate},span=#{span} where sensorid=#{sensorid}")
    void updateById(Sensor sensor);

    @Delete("delete from sensor where sensorid=#{sensorid}")
    void deleteById(int sensorid);

    @Delete("delete from user_sensor where sensorid=#{sensorid}")
    void deleteUserSensorById(int sensorid);

    @Select("select s.* from sensor s " +
            "inner join user_sensor us on s.sensorid = us.sensorid " +
            "inner join user u on us.userid = u.userid " +
            "where u.username = #{username} and s.sensorname like concat('%',#{name},'%')")
    List<Sensor> selectByUsername(String username, String name);
}
