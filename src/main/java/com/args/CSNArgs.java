package com.args;

import com.common.Annotation;

import java.io.Serializable;

public class CSNArgs implements Serializable {
    @Annotation("input file")
    public String input = "";
    @Annotation("a BED file")
    public String bed = "";
    @Annotation("output directory, created in advance")
    public String outputDir = "";
    @Annotation("prefix of the output file(s)")
    public String tag = "CSN.out";
    @Annotation("Size of neighborhood, Default = 0.1 (nx(k) = ny(k) = 0.1*n)")
    public Double boxSize = 0.1;
    @Annotation("Significant level (eg. 0.001, 0.01, 0.05 ...), Default = 0.01")
    public Double alpha = 0.01;
    @Annotation("whether generate NDM matrix")
    public boolean ndmFlag = false;

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

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(Double boxSize) {
        this.boxSize = boxSize;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public boolean isNdmFlag() {
        return ndmFlag;
    }

    public void setNdmFlag(boolean ndmFlag) {
        this.ndmFlag = ndmFlag;
    }
}
