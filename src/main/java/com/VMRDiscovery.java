package com;

import com.File.BedFile;
import com.File.ChipFile;
import com.File.StatOutputFile;
import com.args.VMRDiscoveryArgs;
import com.bean.ChipInfo;
import com.bean.Region;
import com.common.Util;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class VMRDiscovery {
    public static final Logger log = LoggerFactory.getLogger(VMRDiscovery.class);

    VMRDiscoveryArgs args = new VMRDiscoveryArgs();
    Util util = new Util();

    public void vmrDiscovery(VMRDiscoveryArgs VMRDiscoveryArgs) throws Exception {
        log.info("command.VMRDiscovery start!");
        args = VMRDiscoveryArgs;

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        // init the object
        ChipFile chipFile = new ChipFile(args.getInput());

        // get regionList, from region or bedfile or genomeWide
        List<Region> regionList = new ArrayList<>();
        if (args.getBed() != null && !args.getBed().equals("")) {
            BedFile bedFile = new BedFile(args.getBed());
            List<Region> regionListInBed = bedFile.parseWholeFile();
            regionList.addAll(regionListInBed);
        } else {
            List<Region> wholeRegionList = new ArrayList<>();
            Map<String, List<Integer>> cpgPostListMap = chipFile.getWholeCpgList();
            Iterator<String> iterator = cpgPostListMap.keySet().iterator();
            while (iterator.hasNext()) {
                String chrom = iterator.next();
                List<Integer> cpgPostList = cpgPostListMap.get(chrom);
                if (cpgPostList.size() < 1) {
                    continue;
                }
                Region region = new Region(chrom, cpgPostList.get(0), cpgPostList.get(cpgPostList.size() - 1));
                wholeRegionList.add(region);
            }
            regionList.addAll(wholeRegionList);
        }

        // create the output file
        String outputFileName = args.getOutput() + ".txt";
        StatOutputFile outputFile = new StatOutputFile("", outputFileName);

        // get sample ID list
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

        for (Region region : regionList) {
            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, sampleIdList);
            if (chipInfoList.size() < 1) {
                // log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            // get the cpg postion in region
            List<Integer> cpgPosListInRegion = util.getCpgPosListInRegion(chipInfoList);

            // get every cpg position's SD significant
            double[] sdArray = new double[chipInfoList.size()];
            for (int i = 0; i < chipInfoList.size(); i++) {
                Double[] dataArray = chipInfoList.get(i).getDataArray();
                double[] dataArrayForCalculate = Arrays.stream(dataArray).filter(val -> !val.isNaN()) // filter the invalid data
                        .mapToDouble(Double::doubleValue).toArray(); // convert Double[] to double[]
                sdArray[i] = Math.sqrt(StatUtils.variance(dataArrayForCalculate));
            }
            double percentile95 = StatUtils.percentile(sdArray, args.getPercentile());

            // get whether cpg position fit the condition
            int[] statusArray = new int[chipInfoList.size()];
            for (int i = 0; i < chipInfoList.size(); i++) {
                if (sdArray[i] > percentile95) {
                    statusArray[i] = 1;
                } else {
                    statusArray[i] = 0;
                }
            }

            int yesNum = 0;
            Integer lastYesPos = 0;
            Integer thisYesPos = 0;
            List<Integer> distanceList = new ArrayList<Integer>();
            List<Integer> yesIndexList = new ArrayList<Integer>();
            for (int i = 0; i < statusArray.length - 1; i++) {
                if (statusArray[i] == 1) {
                    if (yesNum == 0) {
                        lastYesPos = cpgPosListInRegion.get(i);
                    } else {
                        thisYesPos = cpgPosListInRegion.get(i);
                        Integer distance = thisYesPos - lastYesPos;
                        distanceList.add(distance);
                        lastYesPos = thisYesPos;
                    }
                    yesIndexList.add(i);
                    yesNum++;
                }
            }

            Integer startIndex = 0; // start VMR position index
            Integer endIndex = 1; // end VMR position index
            Integer distance_index = 0;
            while (distance_index < distanceList.size() - 1) {
                if (distanceList.get(distance_index) <= args.getDistance()) {
                    startIndex = distance_index;
                    endIndex = startIndex + 1;
                    while (endIndex < distanceList.size() - 1) {
                        if (distanceList.get(endIndex) > args.getDistance()) {
                            break;
                        }
                        endIndex++;
                    }

                    Integer vmrStartIndex = yesIndexList.get(startIndex);
                    Integer vmrEndIndex = yesIndexList.get(endIndex);
                    int[] statusArrayInWindow = Arrays.copyOfRange(statusArray, vmrStartIndex, vmrEndIndex + 1);
                    double windowYesNum = 0.0;
                    for (int i = 0; i <= statusArrayInWindow.length - 1; i++) {
                        if (statusArrayInWindow[i] == 1) {
                            windowYesNum++;
                        }
                    }
                    double yesRate = windowYesNum / statusArrayInWindow.length;
                    if (windowYesNum >= 3 && (yesRate >= args.getRate())) {
                        outputFile.writeLine(region.getChrom() + "\t" + cpgPosListInRegion.get(vmrStartIndex) +
                                "\t" + cpgPosListInRegion.get(vmrEndIndex) + "\n");
                    }
                    distance_index = endIndex;
                } else {
                    distance_index++;
                }
            }

            log.info("Get VMR from region: " + region.toHeadString() + " end!");
        }

        outputFile.close();
        log.info("command.VMRDiscovery end!");
    }

    private boolean checkArgs() {
        if (args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        return true;
    }

}
