package com.example.j2.entity;

public class Sensor {
    private int sensorid;//传感器id
    private String sensorname;//名称（温度湿度...）
    private String manufact;//生产厂家
    private String model;//型号（大中小）
    private String manudate;//出厂日期
    private String span;//使用年限

    public int getSensorid() {
        return sensorid;
    }

    public void setSensorid(int sensorid) {
        this.sensorid = sensorid;
    }

    public String getSensorname() {
        return sensorname;
    }

    public void setSensorname(String sensorname) {
        this.sensorname = sensorname;
    }

    public String getManufact() {
        return manufact;
    }

    public void setManufact(String manufact) {
        this.manufact = manufact;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManudate() {
        return manudate;
    }

    public void setManudate(String manudate) {
        this.manudate = manudate;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }
}
