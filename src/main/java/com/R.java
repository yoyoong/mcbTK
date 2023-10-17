package com;

import com.File.BedFile;
import com.File.ChipFile;
import com.File.StatOutputFile;
import com.args.RArgs;
import com.args.StatArgs;
import com.bean.ChipInfo;
import com.bean.RInfo;
import com.bean.Region;
import com.common.Util;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class R {
    public static final Logger log = LoggerFactory.getLogger(R.class);

    RArgs args = new RArgs();
    Util util = new Util();

    public void r(RArgs rArgs) throws Exception {
        log.info("command.r start!");
        args = rArgs;

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        // init the object
        ChipFile chipFile = new ChipFile(args.getInput());

        // get the region list from region or bedfile
        List<Region> regionList = new ArrayList<>();
        if (args.getRegion() != null && !args.getRegion().equals("")) {
            Region region = new Region(args.getRegion());
            regionList.add(region);
        } else if (args.getBed() != null && !args.getBed().equals("")) {
            BedFile bedFile = new BedFile(args.getBed());
            regionList = bedFile.parseWholeFile();
        }

        // get sample ID list
        List<String> sampleIdList = new ArrayList<>();
        if (args.getSampleID() != null && !args.getSampleID().equals("")) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args.getSampleID()));
            String sampleId = "";
            while ((sampleId = bufferedReader.readLine()) != null && !sampleId.equals("")) {
                sampleIdList.add(sampleId);
            }
        }

        // create the output file and write the head
        String outputFileName = "R.output.txt";
        StatOutputFile outputFile = new StatOutputFile("", outputFileName);

        for (Region region : regionList) {
            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, sampleIdList);
            if (chipInfoList.size() < 1) {
                log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            for (int i = 0; i < chipInfoList.size(); i++) {
                for (int j = i + 1; j < chipInfoList.size(); j++) {
                    Integer cpgPos1 = chipInfoList.get(i).getStart();
                    Integer cpgPos2 = chipInfoList.get(j).getStart();
                    Double[] dataArray1 = chipInfoList.get(i).getDataArray();
                    Double[] dataArray2 = chipInfoList.get(j).getDataArray();

                    // calculate the r value
                    RInfo rInfo = util.calculateRvalue(dataArray1, dataArray2, args.getNSample());
                    if (rInfo.getRvalue().isNaN()) {
                        log.info("The count of sample which data is not NA in " + region.getChrom() + "\t" +
                                cpgPos1 + "\t" + cpgPos2 + " is less than nSample");
                        continue;
                    }

                    // write the output file
                    String line = region.getChrom() + "\t" + cpgPos1 + "\t" + cpgPos2 + "\t" + rInfo.getRvalue() + "\n";
                    outputFile.writeLine(line);
                }
            }
        }
        outputFile.close();

        log.info("command.r end!");
    }

    private boolean checkArgs() {
        if (args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        if (args.getBed().equals("") && args.getRegion().equals("")) {
            log.error("Should input the one of region or bed.");
            return false;
        }
        if (!args.getBed().equals("") && !args.getRegion().equals("")) {
            log.error("Can not input region and bed at the same time.");
            return false;
        }

        return true;
    }

}
