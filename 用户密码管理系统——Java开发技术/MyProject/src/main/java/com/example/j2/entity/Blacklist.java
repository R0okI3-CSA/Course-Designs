package com.example.j2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("blacklist")
public class Blacklist {
    @TableId(type = IdType.AUTO)
    private Integer blacklistid;
    private Integer userid;

    public Integer getBlacklistid() {
        return blacklistid;
    }

    public void setBlacklistid(Integer blacklistid) {
        this.blacklistid = blacklistid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
} 