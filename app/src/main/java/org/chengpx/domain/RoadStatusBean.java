package org.chengpx.domain;

/**
 * create at 2018/5/10 20:46 by chengpx
 */
public class RoadStatusBean {

    private Integer value;
    private Integer resId;
    private String desc;

    public RoadStatusBean(Integer value, Integer resId, String desc) {
        this.value = value;
        this.resId = resId;
        this.desc = desc;
    }

    public RoadStatusBean() {
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
