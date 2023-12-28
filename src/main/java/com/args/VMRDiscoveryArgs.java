package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class VMRDiscoveryArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("a BED file")
    public String bed = "";
    @Annotation("input sampleID file, .txt format, one line is a sample ID")
    public String sampleID = "";
    @Annotation("SD percentile cutoff, input 0-100 [95]")
    public Integer percentile = 95;
    @Annotation("maximum distance of VMR region [1000]")
    public Integer distance = 1000;
    @Annotation("rate cutoff of cpg sites meeting the significance level [0.5]")
    public Double rate = 0.5;
    @Annotation("minimal number of samples that cover the CpGs for R calculation [20]")
    public Integer nSample = 20;
    @Annotation("prefix of the output file(s) [VMRDiscovery]")
    public String output = "VMRDiscovery";

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

    public Integer getPercentile() {
        return percentile;
    }

    public void setPercentile(Integer percentile) {
        this.percentile = percentile;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getnSample() {
        return nSample;
    }

    public void setnSample(Integer nSample) {
        this.nSample = nSample;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
