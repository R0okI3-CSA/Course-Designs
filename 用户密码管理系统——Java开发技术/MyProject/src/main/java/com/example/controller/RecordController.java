package com.example.controller;

import com.example.common.Result;
import com.example.entity.Record;
import com.example.service.RecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/Record")
public class RecordController {
    @Resource
    private RecordService recordService;

    @PostMapping("/add")
    public Result add(@RequestBody Record record) {
        // 设置登录时间
        record.setLogindate(new Date());
        // 保存记录
        recordService.save(record);
        return Result.success();
    }
} 