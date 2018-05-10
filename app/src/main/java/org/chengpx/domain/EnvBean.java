package org.chengpx.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "Env")
public class EnvBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "DateTime")
    private Date dateTime;
    @DatabaseField(columnName = "senseName")
    private String senseName;
    @DatabaseField(columnName = "senseDesc")
    private String senseDesc;
    @DatabaseField(persisted = false)
    private int[] range;
    @DatabaseField(columnName = "val")
    private Integer val;
    @DatabaseField(persisted = false)
    private String unit;

    public EnvBean(String senseName, String senseDesc, int[] range) {
        this.senseName = senseName;
        this.senseDesc = senseDesc;
        this.range = range;
    }

    public EnvBean() {
    }

    public EnvBean(String senseName, int[] range) {
        this.senseName = senseName;
        this.range = range;
    }

    public EnvBean(String senseName, String senseDesc, String unit) {
        this.senseName = senseName;
        this.senseDesc = senseDesc;
        this.unit = unit;
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
        return senseName;
    }

    public void setSenseName(String senseName) {
        this.senseName = senseName;
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
}
