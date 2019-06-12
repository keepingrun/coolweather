package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 天气界面Activity
 */
public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawer_layout;
    private Button nav_button;

    //下拉刷新
    public SwipeRefreshLayout swipe_refresh;
    private ImageView bing_pic_img;
    private final String getImg_url="http://guolin.tech/api/bing_pic";

    private ScrollView weatherLayout;
    //所选县
    private TextView titleCity;
    //天气更新时间
    private TextView titleUpdateTime;
    //温度
    private TextView degreeText;
    //天气晴
    private TextView weatherInfoText;
    //未来天气
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    //建议
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        //获取天气数据
        getWeatherData();
    }

    private void getWeatherData() {
        //下拉刷新
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        //设置天气界面的背景图片
        String imgurl=prefs.getString("bing_pic",null);
        if(imgurl!=null){
            Glide.with(this).load(imgurl).into(bing_pic_img);
        }else{
            //从网上获取url
            loadBingPic();
        }
        final String weatherId;
        //从本地获取到天气 缓存数据，否则到服务器获取
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            weatherId=getIntent().getStringExtra("weather_id");
            //请求数据时layout不可见
            weatherLayout.setVisibility(View.INVISIBLE);
            //根据weather_id请求数据
            requestWeather(weatherId);
        }
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    private void loadBingPic() {
        HttpUtil.sendOkHttpRequest(getImg_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgurl=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",imgurl);
                editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(imgurl).into(bing_pic_img);
                        }
                    });

            }
        });
    }

    //根据weather_id请求数据
    public void requestWeather(final String weatherId) {
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=babd1a56e23944fdbd0ef928bf372c90";
       HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               //匿名内部类
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                       swipe_refresh.setRefreshing(false);
                   }
               });
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //保存到缓存
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        //在runOnUiThread中隐藏进度条
                        swipe_refresh.setRefreshing(false);
                    }
                });
           }
       });
    loadBingPic();
    }

    /**
     *
     *  处理显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        //所选县
        titleCity.setText(weather.basic.cityname);
        //split返回的是数组
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature+"'C");
        weatherInfoText.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        comfortText.setText("舒适度:"+weather.suggestion.comfort.info);
        carWashText.setText("洗车指数:"+weather.suggestion.carWash.info);
        sportText.setText("运动建议:"+weather.suggestion.sport.info);
        //数据查询到，layout可见
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //初始化控件
    private void initView() {
        weatherLayout=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.car_wash_text);
        sportText=findViewById(R.id.sport_text);
        bing_pic_img=findViewById(R.id.bing_pic_img);
        swipe_refresh=findViewById(R.id.swipe_refresh);

        drawer_layout=findViewById(R.id.drawer_layout);
        nav_button=findViewById(R.id.nav_button);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开滑动菜单
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
    }
}
