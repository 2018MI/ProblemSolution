package org.chengpx.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "car")
public class CarBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "CarId")
    private Integer CarId;
    @DatabaseField(columnName = "Balance")
    private Integer Balance;
    @DatabaseField(columnName = "Money")
    private Integer Money;
    @DatabaseField(columnName = "rechargeTime")
    private Date rechargeTime;
    @DatabaseField(columnName = "rechargeUName")
    private String rechargeUName;
    @DatabaseField(persisted = false)
    private Boolean enable;
    @DatabaseField(persisted = false)
    private String CarAction;

    public CarBean(Integer carId) {
        CarId = carId;
    }

    public CarBean() {
    }

    public Integer getCarId() {
        return CarId;
    }

    public void setCarId(Integer carId) {
        CarId = carId;
    }

    public Integer getBalance() {
        return Balance;
    }

    public void setBalance(Integer balance) {
        Balance = balance;
    }

    public Integer getMoney() {
        return Money;
    }

    public void setMoney(Integer money) {
        Money = money;
    }

    @Override
    public String toString() {
        return "CarBean{" +
                "CarId=" + CarId +
                ", Balance=" + Balance +
                ", Money=" + Money +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getRechargeUName() {
        return rechargeUName;
    }

    public void setRechargeUName(String rechargeUName) {
        this.rechargeUName = rechargeUName;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    public String getCarAction() {
        return CarAction;
    }

    public void setCarAction(String carAction) {
        CarAction = carAction;
    }
}
