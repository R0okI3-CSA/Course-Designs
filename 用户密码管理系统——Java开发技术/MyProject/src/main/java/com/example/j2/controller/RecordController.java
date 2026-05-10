package com.example.j2.controller;

import com.example.j2.entity.Record;
import com.example.j2.service.RecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.j2.entity.Result;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/record")
public class RecordController {
    @Autowired
    RecordService recordService;

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") int pageNum,
                           @RequestParam(defaultValue = "5") int pageSize,
                           @RequestParam(required = false) String logindate,
                           @RequestParam(required = false) String loginstate,
                           @RequestParam(required = false) String userid) {
        PageInfo<Record> recordPageInfo = recordService.selectPage(pageNum, pageSize, logindate, loginstate, userid);
        return Result.success(recordPageInfo);
    }

    @PostMapping("/add")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public Result add(@RequestBody Record record) {
        recordService.save(record);
        return Result.success();
    }
} 