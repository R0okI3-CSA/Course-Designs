package com.example.j2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.j2.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    @Select("<script>" +
            "SELECT r.*, u.username FROM record r " +
            "LEFT JOIN user u ON r.userid = u.userid " +
            "WHERE 1=1 " +
            "<if test='logindate != null and logindate != \"\"'>" +
            "AND DATE_FORMAT(r.logindate, '%Y-%m-%d') = #{logindate} " +
            "</if>" +
            "<if test='loginstate != null and loginstate != \"\"'>" +
            "AND r.loginstate = #{loginstate} " +
            "</if>" +
            "<if test='userid != null and userid != \"\"'>" +
            "AND r.userid = #{userid} " +
            "</if>" +
            "ORDER BY r.logindate DESC" +
            "</script>")
    List<Record> selectAll(String logindate, String loginstate, String userid);
} 