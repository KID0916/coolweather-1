package com.gdqyn.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdqyn.coolweather.R;
import com.gdqyn.coolweather.service.AutoUpdateService;
import com.gdqyn.coolweather.util.HttpCallBackListener;
import com.gdqyn.coolweather.util.HttpUtil;
import com.gdqyn.coolweather.util.Utility;

/**
 * Created by gdqyn on 15/2/28.
 */
public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        String weatherCode = getIntent().getStringExtra("weatherCode");
        if (!TextUtils.isEmpty(weatherCode)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(weatherCode);
        }
        else {
            temp2Text.setVisibility(View.INVISIBLE);
            showWeather();
        }

        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
            }
        });

        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
            }
        });
    }

    private void queryWeatherInfo(final String weatherCode) {
        final String address = "http://gdqyn.sinaapp.com/weather/" + weatherCode;
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
