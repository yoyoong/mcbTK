package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class MCBDiscoveryArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("a BED file")
    public String bed = "";
    @Annotation("sampleID")
    public String sampleID = "";
    @Annotation("R cutoff")
    public Double R;
    @Annotation("p value cutoff")
    public Double pvalue;
    @Annotation("size of window")
    public Integer window;
    @Annotation("distance")
    public Integer distance;
    @Annotation("minimal number of samples that cover the CpGs for R calculation [20]")
    public Integer nSample = 20;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getSampleID() {
        return sampleID;
    }

    public void setSampleID(String sampleID) {
        this.sampleID = sampleID;
    }

    public Double getR() {
        return R;
    }

    public void setR(Double r) {
        R = r;
    }

    public Double getPvalue() {
        return pvalue;
    }

    public void setPvalue(Double pvalue) {
        this.pvalue = pvalue;
    }

    public Integer getWindow() {
        return window;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getNSample() {
        return nSample;
    }

    public void setNSample(Integer nSample) {
        this.nSample = nSample;
    }
}
