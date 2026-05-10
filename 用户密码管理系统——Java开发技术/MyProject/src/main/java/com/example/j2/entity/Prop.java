package com.example.j2.entity;

public class Prop {
    private int propid;//自增主键
    private String propname;//房屋名称
    private String addr;//所在地
    private String kind;//房屋类型

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getPropid() {
        return propid;
    }

    public void setPropid(int propid) {
        this.propid = propid;
    }

    public String getPropname() {
        return propname;
    }

    public void setPropname(String propname) {
        this.propname = propname;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPropany() {
        return propany;
    }

    public void setPropany(String propany) {
        this.propany = propany;
    }

    private String propany;//建筑单位


}
