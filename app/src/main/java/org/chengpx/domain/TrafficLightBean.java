package org.chengpx.domain;

public class TrafficLightBean {

    public TrafficLightBean(Integer trafficLightId) {
        TrafficLightId = trafficLightId;
    }

    public TrafficLightBean() {
    }

    /**
     * RedTime : 7
     * YellowTime : 3
     * GreenTime : 55
     */

    private int RedTime;
    private int YellowTime;
    private int GreenTime;
    /**
     * Status : Green
     * Time : 55
     */

    private String Status;
    private String Time;
    private Integer TrafficLightId;
    private String StatusDesc;
    private int StatusResId;
    private Integer RoadId;

    public int getRedTime() {
        return RedTime;
    }

    public void setRedTime(int RedTime) {
        this.RedTime = RedTime;
    }

    public int getYellowTime() {
        return YellowTime;
    }

    public void setYellowTime(int YellowTime) {
        this.YellowTime = YellowTime;
    }

    public int getGreenTime() {
        return GreenTime;
    }

    public void setGreenTime(int GreenTime) {
        this.GreenTime = GreenTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public Integer getTrafficLightId() {
        return TrafficLightId;
    }

    public void setTrafficLightId(Integer trafficLightId) {
        TrafficLightId = trafficLightId;
    }

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
    }

    public int getStatusResId() {
        return StatusResId;
    }

    public void setStatusResId(int statusResId) {
        StatusResId = statusResId;
    }

    public Integer getRoadId() {
        return RoadId;
    }

    public void setRoadId(Integer roadId) {
        RoadId = roadId;
    }
}
