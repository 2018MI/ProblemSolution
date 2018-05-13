package org.chengpx.domain;

/**
 * create at 2018/5/10 21:51 by chengpx
 */
public class WeatherBean {

    private String name;
    private Integer resId;
    private Integer level;

    public WeatherBean(String name, Integer resId, Integer level) {
        this.name = name;
        this.resId = resId;
        this.level = level;
    }

    public WeatherBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
