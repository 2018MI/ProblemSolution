package org.chengpx.domain;

import com.google.gson.annotations.SerializedName;

/**
 * create at 2018/5/11 20:34 by chengpx
 */
public class AllSenseBean {

    @SerializedName("pm2.5")
    private int _$Pm25171;
    private int co2;
    private int LightIntensity;
    private int humidity;
    private int temperature;

    public int get_$Pm25171() {
        return _$Pm25171;
    }

    public void set_$Pm25171(int _$Pm25171) {
        this._$Pm25171 = _$Pm25171;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getLightIntensity() {
        return LightIntensity;
    }

    public void setLightIntensity(int LightIntensity) {
        this.LightIntensity = LightIntensity;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
