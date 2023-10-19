package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class RxsArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("a BED file")
    public String bed = "";
    @Annotation("input sampleID file, .txt format, one line is a sample ID")
    public String sampleID = "";
    @Annotation("stat metrics, including mean, median, var")
    public String metrics = "";
    @Annotation("prefix of the output file(s) [Rxs]")
    public String output = "Rxs";

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

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
