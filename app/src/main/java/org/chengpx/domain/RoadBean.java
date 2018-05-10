package org.chengpx.domain;

public class RoadBean {

    /**
     * RoadId : 1
     * Status : 1
     */
    private int RoadId;
    private int Status;

    public RoadBean(int roadId) {
        RoadId = roadId;
    }

    public RoadBean() {
    }

    public int getRoadId() {
        return RoadId;
    }

    public void setRoadId(int RoadId) {
        this.RoadId = RoadId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

}
