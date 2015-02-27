package com.gdqyn.coolweather.util;

/**
 * Created by gdqyn on 15/2/26.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
