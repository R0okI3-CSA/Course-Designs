package com.example.j2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.j2.mapper")//扫描mapper，关于数据库的全部放在mapper中
public class J2Application {

    public static void main(String[] args) {
        SpringApplication.run(J2Application.class, args);
    }

}
