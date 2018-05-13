package org.chengpx.domain;

/**
 * create at 2018/5/9 21:01 by chengpx
 */
public class TrafficLightBean {

    /**
     * RedTime : 25
     * GreenTime : 55
     * YellowTime : 5
     */
    private String RedTime;
    private String GreenTime;
    private String YellowTime;
    private Integer RoadId;
    /**
     * Status : Red
     * Time : 8
     */
    private String Status;
    private int Time;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int Time) {
        this.Time = Time;
    }

    public String getRedTime() {
        return RedTime;
    }

    public void setRedTime(String RedTime) {
        this.RedTime = RedTime;
    }

    public String getGreenTime() {
        return GreenTime;
    }

    public void setGreenTime(String GreenTime) {
        this.GreenTime = GreenTime;
    }

    public String getYellowTime() {
        return YellowTime;
    }

    public void setYellowTime(String YellowTime) {
        this.YellowTime = YellowTime;
    }

    public Integer getRoadId() {
        return RoadId;
    }

    public void setRoadId(Integer roadId) {
        RoadId = roadId;
    }



}
