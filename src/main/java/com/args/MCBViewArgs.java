package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class MCBViewArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("one region, in the format of chr:start-end")
    public String region = "";
    @Annotation("input sampleID file, .txt format, one line is a sample ID")
    public String sampleID = "";
    @Annotation("minimal number of samples that cover the CpGs for R calculation [20]")
    public Integer nSample = 20;
    @Annotation("output format,pdf or png [pdf]")
    public String outFormat = "pdf"; // output format,pdf or png [pdf]
    @Annotation("prefix of the output file(s)")
    public String output = "";

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSampleID() {
        return sampleID;
    }

    public void setSampleID(String sampleID) {
        this.sampleID = sampleID;
    }

    public Integer getNSample() {
        return nSample;
    }

    public void setNSample(Integer nSample) {
        this.nSample = nSample;
    }

    public String getOutFormat() {
        return outFormat;
    }

    public void setOutFormat(String outFormat) {
        this.outFormat = outFormat;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
