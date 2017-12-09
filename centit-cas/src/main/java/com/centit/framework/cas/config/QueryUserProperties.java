package com.centit.framework.cas.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import com.centit.support.database.utils.DataSourceDescription;
import java.io.Serializable;

public class QueryUserProperties implements Serializable {

    private static final long serialVersionUID = 1L;


    private String sql;
    private String pinField;
    private Integer paramRepeatTimes;
    private String principalField;
    private String disabledField;
    private String expiredField;

    @NestedConfigurationProperty
    private DataSourceDescription datasource = new DataSourceDescription();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getPinField() {
        return pinField;
    }

    public void setPinField(String pinField) {
        this.pinField = pinField;
    }

    public Integer getParamRepeatTimes() {
        return paramRepeatTimes;
    }

    public void setParamRepeatTimes(Integer paramRepeatTimes) {
        this.paramRepeatTimes = paramRepeatTimes;
    }

    public String getPrincipalField() {
        return principalField;
    }

    public void setPrincipalField(String principalField) {
        this.principalField = principalField;
    }

    public DataSourceDescription getDatasource() {
        return datasource;
    }

    public String getDisabledField() {
        return disabledField;
    }

    public void setDisabledField(String disabledField) {
        this.disabledField = disabledField;
    }

    public String getExpiredField() {
        return expiredField;
    }

    public void setExpiredField(String expiredField) {
        this.expiredField = expiredField;
    }

    public void setDatasource(DataSourceDescription datasource) {
        this.datasource = datasource;
    }

}
