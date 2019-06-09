package com.example.coolweather.db;

import org.litepal.crud.DataSupport;
/**
 * 数据库表
 * 需要在assets目录的litepal.xml文件里配置
 */
public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;//provinceCode,找到当前城市所属于的省份

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
