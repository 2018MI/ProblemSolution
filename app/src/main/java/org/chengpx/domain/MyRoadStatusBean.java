package org.chengpx.domain;

public class MyRoadStatusBean {

    private Integer RoadId;
    private Integer Status;
    private TrafficLightBean trafficLightBean;

    public MyRoadStatusBean(Integer roadId, TrafficLightBean trafficLightBean) {
        RoadId = roadId;
        this.trafficLightBean = trafficLightBean;
    }

    public MyRoadStatusBean() {
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
}
