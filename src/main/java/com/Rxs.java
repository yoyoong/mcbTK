package com;

import com.File.BedFile;
import com.File.ChipFile;
import com.File.OutputFile;
import com.args.RxsArgs;
import com.bean.ChipInfo;
import com.bean.Region;
import com.common.Util;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rxs {
    public static final Logger log = LoggerFactory.getLogger(Rxs.class);

    RxsArgs args = new RxsArgs();
    Util util = new Util();

    public void rxs(RxsArgs rxsArgs) throws Exception {
        log.info("command.rxs start!");
        args = rxsArgs;

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        // init the object
        ChipFile chipFile = new ChipFile(args.getInput());
        BedFile bedFile = new BedFile(args.getBed());

        // get the region list from bedfile
        List<Region> regionList = bedFile.parseWholeFile();
        if (regionList.size() < 1) {
            log.info("The region list in bedfile is null, please check!");
        }

        List<String> sampleIdList = new ArrayList<>();
        if (args.getSampleID() != null && !args.getSampleID().equals("")) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args.getSampleID()));
            String sampleId = "";
            while ((sampleId = bufferedReader.readLine()) != null && !sampleId.equals("")) {
                sampleIdList.add(sampleId);
            }
        } else {
            sampleIdList = chipFile.getSampleIdList();
        }

        // create the output file and write the head
        String headString = "chrom\t" + "start\t" + "end\t" + "cov";
        for (String sampleId : sampleIdList) {
            headString += "\t" + sampleId;
        }
        OutputFile outputFileMean = null;
        OutputFile outputFileMedian = null;
        OutputFile outputFileVar = null;
        if (args.getMetrics().contains("mean")) {
            outputFileMean = new OutputFile("", args.getOutput() + ".mean.txt");
            outputFileMean.writeHead(headString + "\n");
        }
        if (args.getMetrics().contains("median")) {
            outputFileMedian = new OutputFile("", args.getOutput() + ".median.txt");
            outputFileMedian.writeHead(headString + "\n");
        }
        if (args.getMetrics().contains("var")) {
            outputFileVar = new OutputFile("", args.getOutput() + ".var.txt");
            outputFileVar.writeHead(headString + "\n");
        }

        for (Region region : regionList) {
            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, sampleIdList);
            if (chipInfoList.size() < 1) {
                log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            // calculate the metrics and write to output file
            Integer cov = chipInfoList.size();
            String lineStringMean = region.getChrom() + "\t" + region.getStart() + "\t" + region.getEnd() + "\t" + cov;
            String lineStringMedian = region.getChrom() + "\t" + region.getStart() + "\t" + region.getEnd() + "\t" + cov;
            String lineStringVar = region.getChrom() + "\t" + region.getStart() + "\t" + region.getEnd() + "\t" + cov;
            for (int i = 0; i < sampleIdList.size(); i++) {
                double[] data = new double[chipInfoList.size()];
                for (int j = 0; j < chipInfoList.size(); j++) {
                    data[j] = chipInfoList.get(j).getDataArray()[i];
                }
                Double mean = StatUtils.mean(data);
                Double median = StatUtils.percentile(data, 50);
                Double var = StatUtils.variance(data);
                if (args.getMetrics().contains("mean")) {
                    lineStringMean += "\t" + mean;
                }
                if (args.getMetrics().contains("median")) {
                    lineStringMedian += "\t" + median;
                }
                if (args.getMetrics().contains("var")) {
                    lineStringVar += "\t" + var;
                }
            }

            if (args.getMetrics().contains("mean")) {
                outputFileMean.writeLine(lineStringMean + "\n");
            }
            if (args.getMetrics().contains("median")) {
                outputFileMedian.writeLine(lineStringMedian + "\n");
            }
            if (args.getMetrics().contains("var")) {
                outputFileVar.writeLine(lineStringVar + "\n");
            }

        }
        if (args.getMetrics().contains("mean")) {
            outputFileMean.close();
        }
        if (args.getMetrics().contains("median")) {
            outputFileMedian.close();
        }
        if (args.getMetrics().contains("var")) {
            outputFileVar.close();
        }

        log.info("command.rxs end!");
    }

    private boolean checkArgs() {
        if (args.getInput() == null || args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        if (args.getBed() == null || args.getBed().equals("")) {
            log.error("bed can not be null.");
            return false;
        }
        if (args.getMetrics() == null || args.getMetrics().equals("")) {
            log.error("metrics can not be null.");
            return false;
        }
        return true;
    }

}
