package org.chengpx.domain;

/**
 * create at 2018/5/10 20:46 by chengpx
 */
public class RoadBean {

    private Integer RoadId;
    private Integer Status;
    private Integer resId;
    private String desc;
    private TrafficLightBean trafficLightBean;

    public RoadBean(Integer roadId, TrafficLightBean trafficLightBean) {
        RoadId = roadId;
        this.trafficLightBean = trafficLightBean;
    }

    public RoadBean() {
    }

    public RoadBean(Integer roadId) {
        RoadId = roadId;
    }

    public RoadBean(Integer status, Integer resId, String desc) {
        Status = status;
        this.resId = resId;
        this.desc = desc;
    }

    public Integer getRoadId() {
        return RoadId;
    }

    public void setRoadId(Integer roadId) {
        RoadId = roadId;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public TrafficLightBean getTrafficLightBean() {
        return trafficLightBean;
    }

    public void setTrafficLightBean(TrafficLightBean trafficLightBean) {
        this.trafficLightBean = trafficLightBean;
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
