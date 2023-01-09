package com.bean;

import java.io.Serializable;

public class RInfo implements Serializable {
    public Double rvalue;
    public Double pvalue;

    public Double getRvalue() {
        return rvalue;
    }

    public void setRvalue(Double rvalue) {
        this.rvalue = rvalue;
    }

    public Double getPvalue() {
        return pvalue;
    }

    public void setPvalue(Double pvalue) {
        this.pvalue = pvalue;
    }
}
