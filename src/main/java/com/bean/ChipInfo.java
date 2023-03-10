package com.bean;

import java.io.Serializable;

public class ChipInfo implements Serializable {
    public String chrom;
    public Integer start;
    public Integer end;
    public Double[] dataArray;

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Double[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(Double[] dataArray) {
        this.dataArray = dataArray;
    }
}
