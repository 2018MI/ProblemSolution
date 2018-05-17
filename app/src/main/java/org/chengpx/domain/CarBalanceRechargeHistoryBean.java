package org.chengpx.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "CarBalanceRechargeHistory")
public class CarBalanceRechargeHistoryBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "CarId")
    private Integer CarId;
    @DatabaseField(persisted = false)
    private Integer Balance;
    @DatabaseField(columnName = "Money")
    private Integer Money;
    @DatabaseField(columnName = "rechargeDateTime")
    private Date rechargeDateTime;
    @DatabaseField(columnName = "uname")
    private String uname;
    @DatabaseField(persisted = false)
    private int resId;

    public CarBalanceRechargeHistoryBean(Integer carId) {
        CarId = carId;
    }

    public CarBalanceRechargeHistoryBean(Integer carId, String uname) {
        CarId = carId;
        this.uname = uname;
    }

    public CarBalanceRechargeHistoryBean(Integer carId, Integer money, Date rechargeDateTime, String uname) {
        CarId = carId;
        Money = money;
        this.rechargeDateTime = rechargeDateTime;
        this.uname = uname;
    }

    public CarBalanceRechargeHistoryBean() {
    }

    public CarBalanceRechargeHistoryBean(Integer carId, String uname, int resId) {
        CarId = carId;
        this.uname = uname;
        this.resId = resId;
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

    public Date getRechargeDateTime() {
        return rechargeDateTime;
    }

    public void setRechargeDateTime(Date rechargeDateTime) {
        this.rechargeDateTime = rechargeDateTime;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
