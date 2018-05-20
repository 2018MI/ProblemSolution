package org.chengpx.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "env")
public class EnvBean {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "DateTime")
    private Date dateTime;
    @DatabaseField(columnName = "SenseName")
    private String SenseName;
    @DatabaseField(columnName = "senseDesc")
    private String senseDesc;
    @DatabaseField(columnName = "val")
    private Integer val;
    @DatabaseField(persisted = false)
    private int[] range;
    @DatabaseField(persisted = false)
    private String unit;
    @DatabaseField(persisted = false)
    private String warnType;
    @DatabaseField(persisted = false)
    private Integer yuzhi;

    public EnvBean(String senseName, String senseDesc, int[] range) {
        this.SenseName = senseName;
        this.senseDesc = senseDesc;
        this.range = range;
    }

    public EnvBean(String senseName, String senseDesc, int[] range, String warnType) {
        SenseName = senseName;
        this.senseDesc = senseDesc;
        this.range = range;
        this.warnType = warnType;
    }

    public EnvBean() {
    }

    public EnvBean(Integer val, String warnType, Integer yuzhi) {
        this.val = val;
        this.warnType = warnType;
        this.yuzhi = yuzhi;
    }

    public EnvBean(String senseName, int[] range) {
        this.SenseName = senseName;
        this.range = range;
    }

    public EnvBean(String senseName, String senseDesc, String unit) {
        this.SenseName = senseName;
        this.senseDesc = senseDesc;
        this.unit = unit;
    }

    public EnvBean(String senseName, String senseDesc, int[] range, String warnType, Integer yuzhi) {
        SenseName = senseName;
        this.senseDesc = senseDesc;
        this.range = range;
        this.warnType = warnType;
        this.yuzhi = yuzhi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getSenseName() {
        return SenseName;
    }

    public void setSenseName(String senseName) {
        this.SenseName = senseName;
    }

    public String getSenseDesc() {
        return senseDesc;
    }

    public void setSenseDesc(String senseDesc) {
        this.senseDesc = senseDesc;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public Integer getYuzhi() {
        return yuzhi;
    }

    public void setYuzhi(Integer yuzhi) {
        this.yuzhi = yuzhi;
    }
}
