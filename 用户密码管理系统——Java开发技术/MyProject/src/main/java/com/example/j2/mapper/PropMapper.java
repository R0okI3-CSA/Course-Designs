package com.example.j2.mapper;

import com.example.j2.entity.Prop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PropMapper {

    @Select("select *from prop where propname like concat('%',#{name},'%')")//模糊查询
    List<Prop> selectAll(String name);
}
