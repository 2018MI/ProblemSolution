package org.chengpx.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "caroverspeedhistory")
public class CaroverspeedhistoryBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "CarId")
    private Integer CarId;
    @DatabaseField(columnName = "CarSpeed")
    private Integer CarSpeed;
    @DatabaseField(columnName = "overSpeedDateTime")
    private Date overSpeedDateTime;

    public CaroverspeedhistoryBean() {
    }

    public CaroverspeedhistoryBean(Integer carId) {
        CarId = carId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarId() {
        return CarId;
    }

    public void setCarId(Integer carId) {
        CarId = carId;
    }

    public Integer getCarSpeed() {
        return CarSpeed;
    }

    public void setCarSpeed(Integer carSpeed) {
        CarSpeed = carSpeed;
    }

    public Date getOverSpeedDateTime() {
        return overSpeedDateTime;
    }

    public void setOverSpeedDateTime(Date overSpeedDateTime) {
        this.overSpeedDateTime = overSpeedDateTime;
    }
}
