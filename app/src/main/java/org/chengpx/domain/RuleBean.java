package org.chengpx.domain;

public class RuleBean {

    public static final Integer ASC = 0;
    public static final Integer DESC = 1;

    private String desc;
    private String columnField;
    private Integer priority;

    public RuleBean(String desc, String columnField, Integer priority) {
        this.desc = desc;
        this.columnField = columnField;
        this.priority = priority;
    }

    public RuleBean() {
    }

    public RuleBean(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getColumnField() {
        return columnField;
    }

    public void setColumnField(String columnField) {
        this.columnField = columnField;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}
