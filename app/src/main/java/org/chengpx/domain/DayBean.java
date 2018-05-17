package org.chengpx.domain;

/**
 * create at 2018/5/14 16:10 by chengpx
 */
public class DayBean {

    private WeatherBean weatherBean;
    private String desc;

    public DayBean(String desc) {
        this.desc = desc;
    }

    public DayBean() {
    }

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
