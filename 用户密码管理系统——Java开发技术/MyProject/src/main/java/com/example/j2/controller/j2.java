package com.example.j2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//@Controller类中的方法可以直接通过返回String跳转到jsp、ftl、html等模版页面。在方法上加@ResponseBody注解，也可以返回实体对象。@RestController类中的所有方法只能返回String、Object、Json等实体对象，不能跳转到模版页面。
public class j2 {
    @GetMapping("/hello")
    public String hello(){
        return "hellosvghvhsr";
    }
}
