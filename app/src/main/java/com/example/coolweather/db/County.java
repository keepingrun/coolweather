package com.example.coolweather.db;

import org.litepal.crud.DataSupport;
/**
 * 数据库表
 * 需要在assets目录的litepal.xml文件里配置
 */
public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;//cityCode,找到当前县或区所属于的城市

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
