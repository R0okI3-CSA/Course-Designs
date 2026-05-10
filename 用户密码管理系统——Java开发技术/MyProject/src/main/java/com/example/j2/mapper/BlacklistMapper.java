package com.example.j2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.j2.entity.Blacklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {
    @Select("select * from blacklist where userid = #{userid}")
    Blacklist selectByUserid(Integer userid);
} 