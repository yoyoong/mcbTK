package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class RArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("a BED file")
    public String bed = "";
    @Annotation("sampleID")
    public String sampleID = "";
    @Annotation("one region, in the format of chr:start-end")
    public String region = "";
    @Annotation("minimal number of samples that cover the CpGs for R calculation [20]")
    public Integer nSample = 20;
    @Annotation("prefix of the output file(s) [R]")
    public String output = "R";

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getNSample() {
        return nSample;
    }

    public void setNSample(Integer nSample) {
        this.nSample = nSample;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
