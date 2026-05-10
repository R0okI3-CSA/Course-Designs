package com.example.j2.mapper;

import com.example.j2.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Mapper//声明这是一个mapper，启动类里面要扫描的就是这些有@Mapper注解的
public interface UserMapper {
//查询所有用户
    @Select("select *from user")//它会查询后自动将得到的数据封装成一个个User对象放进List中去
    public List<User> find();

    @Insert("insert into user(username,password,phone) values(#{username},#{password},#{phone})")
    public void insert(User user); //返回值是int类型代表插入了几条记录

    @Select("select *from user where username = #{username}")
    User selectByUsername(String username);//通过用户名查询用户

}
