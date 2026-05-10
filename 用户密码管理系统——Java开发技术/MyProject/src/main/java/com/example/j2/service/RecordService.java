package com.example.j2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.j2.entity.Record;
import com.github.pagehelper.PageInfo;

public interface RecordService extends IService<Record> {
    PageInfo<Record> selectPage(int pageNum, int pageSize, String logindate, String loginstate, String userid);
} 