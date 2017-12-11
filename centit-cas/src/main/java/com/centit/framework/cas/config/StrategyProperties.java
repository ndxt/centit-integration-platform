package com.centit.framework.cas.config;

import java.io.Serializable;

public class StrategyProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer maxFailTimesBeforeValidateCode;

    public Integer getMaxFailTimesBeforeValidateCode() {
        return maxFailTimesBeforeValidateCode==null?1:maxFailTimesBeforeValidateCode;
    }

    public void setMaxFailTimesBeforeValidateCode(Integer maxFailTimesBeforeValidateCode) {
        this.maxFailTimesBeforeValidateCode = maxFailTimesBeforeValidateCode;
    }
}
