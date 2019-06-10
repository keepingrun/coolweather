package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityname;
    @SerializedName("id")
    public String weatherId;
    //update和json数据里的name名字是一样，则不用@SerializedName注解
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
