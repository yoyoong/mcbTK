package com.bean;

import java.io.Serializable;

public class Region implements Serializable {
    public String chrom = "";
    public Integer start = 0;
    public Integer end = 0;

    public Region(String regionStr) {
        this.chrom = regionStr.split(":")[0];
        this.start = Integer.valueOf(regionStr.split(":")[1].split("-")[0]);
        this.end = Integer.valueOf(regionStr.split(":")[1].split("-")[1]);
    }

    public Region(String chrom, Integer start, Integer end) {
        this.chrom = chrom;
        this.start = start;
        this.end = end;
    }

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

    public String toFileString() { return this.chrom + "_" + this.start + "_" + this.end; }

    public String toHeadString() {
        return this.chrom + ":" + this.start + "-" + this.end;
    }
}
