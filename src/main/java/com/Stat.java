package com;

import com.File.BedFile;
import com.File.ChipFile;
import com.File.StatOutputFile;
import com.args.StatArgs;
import com.bean.ChipInfo;
import com.bean.Region;
import com.common.Util;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stat {
    public static final Logger log = LoggerFactory.getLogger(Stat.class);

    StatArgs args = new StatArgs();
    Util util = new Util();

    public void stat(StatArgs statArgs) throws Exception {
        log.info("command.stat start!");
        args = statArgs;

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
        String outputFileName = args.getOutput() + ".txt";
        StatOutputFile outputFile = new StatOutputFile("", outputFileName);
        String headString = "chrom\t" + "start\t" + "end\t" + "cov\t" + args.getMetrics();
        outputFile.writeHead(headString);

        for (Region region : regionList) {
            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, sampleIdList);
            if (chipInfoList.size() < 1) {
                log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            // calculate a cpg site metrics value
            double[] meanArray = new double[chipInfoList.size()];
            double[] medianArray = new double[chipInfoList.size()];
            double[] varArray = new double[chipInfoList.size()];
            for (int i = 0; i < chipInfoList.size(); i++) {
                Double[] dataArray = chipInfoList.get(i).getDataArray();
                double[] dataArrayForCalculate = Arrays.stream(dataArray).filter(val -> !val.isNaN()) // filter the invalid data
                        .mapToDouble(Double::doubleValue).toArray(); // convert Double[] to double[]
                meanArray[i] = StatUtils.mean(dataArrayForCalculate);
                medianArray[i] = StatUtils.percentile(dataArrayForCalculate, 50);
                varArray[i] = StatUtils.variance(dataArrayForCalculate);
            }

            // calculate the region metrics value
            Integer cov = chipInfoList.size();
            Double mean = StatUtils.mean(meanArray);
            Double median = StatUtils.mean(medianArray);
            Double var = StatUtils.mean(varArray);

            // write the output file
            String lineString = region.getChrom() + "\t" + region.getStart() + "\t" + region.getEnd() + "\t" + cov;
            if (args.getMetrics().contains("mean")) {
                lineString += "\t" + mean;
            }
            if (args.getMetrics().contains("median")) {
                lineString += "\t" + median;
            }
            if (args.getMetrics().contains("var")) {
                lineString += "\t" + var;
            }
            outputFile.writeLine(lineString + "\n");
        }
        outputFile.close();

        log.info("command.stat end!");
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
