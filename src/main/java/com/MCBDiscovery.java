package com;

import com.File.BedFile;
import com.File.ChipFile;
import com.File.StatOutputFile;
import com.args.MCBDiscoveryArgs;
import com.bean.ChipInfo;
import com.bean.MCBInfo;
import com.bean.RInfo;
import com.bean.Region;
import com.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MCBDiscovery {
    public static final Logger log = LoggerFactory.getLogger(MCBDiscovery.class);

    MCBDiscoveryArgs args = new MCBDiscoveryArgs();
    Util util = new Util();

    public void mcbDiscovery(MCBDiscoveryArgs mcbDiscoveryArgs) throws Exception {
        log.info("command.mcbDiscovery start!");
        args = mcbDiscoveryArgs;

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
            for (Region region : regionListInBed) {
                regionList.addAll(util.splitRegionToSmallRegion(region, 1000000, 1000));
            }
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

            for (Region region : wholeRegionList) {
                regionList.addAll(util.splitRegionToSmallRegion(region, 1000000, 1000));
            }
        }

        // create the output file
        String outputFileName = "MCBDiscovery.output.txt";
        StatOutputFile outputFile = new StatOutputFile("", outputFileName);

        Map<String, String> mcbInfoListMap = new HashMap<>();
        for (Region region : regionList) {
            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, args.getSampleID());
            if (chipInfoList.size() < 1) {
                // log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            // get the cpg postion in region
            List<Integer> cpgPosListInRegion = util.getCpgPosListInRegion(chipInfoList, region);

            Integer startIndex = 0; // start mcb position index in cpgPosListInRegion
            Integer endIndex = 0; // end mcb position index in cpgPosListInRegion
            Integer index = 0;
            while (endIndex < cpgPosListInRegion.size() - 1) {
                endIndex++;
                Boolean extendFlag = true;
                for (int i = 1; i < args.getWindow(); i++) {
                    index = endIndex - i; // cpg site index in cpgPosListInRegion for loop
                    if (index < 0) {
                        break;
                    }
                    if (cpgPosListInRegion.get(endIndex) - cpgPosListInRegion.get(index) > args.getDistance()) {
                        extendFlag = false;
                        break;
                    }

                    // get r2 and pvalue of start array and end array
                    Double[] dataArray1 = chipInfoList.get(index).getDataArray();
                    Double[] dataArray2 = chipInfoList.get(endIndex).getDataArray();
                    RInfo rInfo = util.calculateRvalue(dataArray1, dataArray2, args.getNSample());
                    if (rInfo == null || rInfo.getRvalue() < args.getR() || rInfo.getPvalue() > args.getPvalue()) {
                        extendFlag = false;
                        break;
                    }
                }

                if (!extendFlag) {
                    MCBInfo mcbInfo = new MCBInfo();
                    Integer mcbSize = endIndex - startIndex;
                    mcbInfo.setChrom(region.getChrom());
                    mcbInfo.setStart(cpgPosListInRegion.get(startIndex));
                    mcbInfo.setEnd(cpgPosListInRegion.get(endIndex - 1));
                    startIndex = index + 1 > startIndex ? index + 1 : startIndex;
                    if (mcbSize >= args.getWindow() && !mcbInfoListMap.containsKey(mcbInfo.toString())) {
                        mcbInfoListMap.put(mcbInfo.toString(), mcbInfo.toString());
                        //log.info("discovery a mhb in : " + mhbInfo.getChrom() + ":" + mhbInfo.getStart() + "-" + mhbInfo.getEnd());
                        outputFile.writeLine(mcbInfo.getChrom() + "\t" + mcbInfo.getStart() + "\t" + mcbInfo.getEnd() + "\n");
                    }
                }
            }

            if (endIndex - startIndex >= args.getWindow()) {
                MCBInfo mcbInfo = new MCBInfo();
                mcbInfo.setChrom(region.getChrom());
                mcbInfo.setStart(cpgPosListInRegion.get(startIndex));
                mcbInfo.setEnd(cpgPosListInRegion.get(endIndex - 1));
                if (!mcbInfoListMap.containsKey(mcbInfo.toString())) {
                    mcbInfoListMap.put(mcbInfo.toString(), mcbInfo.toString());
                    outputFile.writeLine(mcbInfo.getChrom() + "\t" + mcbInfo.getStart() + "\t" + mcbInfo.getEnd() + "\n");
                }
            }
            log.info("Get MHB from region: " + region.toHeadString() + " end!");
        }

        outputFile.close();
        log.info("command.mcbDiscovery end!");
    }

    private boolean checkArgs() {
        if (args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        return true;
    }

}
