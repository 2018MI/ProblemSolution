package org.chengpx.domain;

/**
 * create at 2018/5/14 16:10 by chengpx
 */
public class MyDayBean {

    //private String temperatureRange;
    private WeatherBean weatherBean;
    private String desc;

    public MyDayBean(String desc) {
        this.desc = desc;
    }

    public MyDayBean() {
    }

//    public String getTemperatureRange() {
//        return temperatureRange;
//    }
//
//    public void setTemperatureRange(String temperatureRange) {
//        this.temperatureRange = temperatureRange;
//    }

    public WeatherBean getWeatherBean() {
        return weatherBean;
    }

    public void setWeatherBean(WeatherBean weatherBean) {
        this.weatherBean = weatherBean;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



}
