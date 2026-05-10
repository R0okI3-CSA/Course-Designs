package com.example.j2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.j2.entity.Record;
import com.example.j2.mapper.RecordMapper;
import com.example.j2.service.RecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Override
    public PageInfo<Record> selectPage(int pageNum, int pageSize, String logindate, String loginstate, String userid) {
        PageHelper.startPage(pageNum, pageSize);
        List<Record> recordList = baseMapper.selectAll(logindate, loginstate, userid);
        return PageInfo.of(recordList);
    }
} 