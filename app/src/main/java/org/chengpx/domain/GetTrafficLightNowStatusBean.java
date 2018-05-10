package org.chengpx.domain;

/**
 * create at 2018/5/9 21:57 by chengpx
 */
public class GetTrafficLightNowStatusBean {


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

    @Override
    public String toString() {
        return "GetTrafficLightNowStatusBean{" +
                "Status='" + Status + '\'' +
                ", Time=" + Time +
                '}';
    }
}
