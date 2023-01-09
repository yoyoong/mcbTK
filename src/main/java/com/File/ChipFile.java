package com.File;

import com.bean.ChipInfo;
import com.bean.Region;
import htsjdk.tribble.readers.TabixReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ChipFile {
    public static final Logger log = LoggerFactory.getLogger(ChipFile.class);

    File chipFile;
    TabixReader tabixReader;

    public ChipFile(String chipPath) throws IOException {
        chipFile = new File(chipPath);
        tabixReader = new TabixReader(chipPath);
    }

    public List<ChipInfo> parseByRegionAndSampleID(Region region, String sampleID) throws IOException {
        // get the sampleID and the sampleID index in all sample from the first line
        List<Integer> sampleIndexList = new ArrayList<>();
        if (sampleID != null && !sampleID.equals("")) {
            FileInputStream fileInputStream = new FileInputStream(chipFile.getName());
            GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String[] chipFirstLineArray = bufferedReader.readLine().split("\t");
            List<String> sampleIDList = Arrays.asList(chipFirstLineArray).subList(3, chipFirstLineArray.length);

            String[] sampleIDArray = sampleID.split(" ");
            for (String sample : sampleIDArray) {
                if (sampleIDList.contains(sample)) {
                    sampleIndexList.add(sampleIDList.indexOf(sample));
                }
            }
        }

        // get the data from chip file
        TabixReader tabixReader = new TabixReader(chipFile.getAbsolutePath());
        TabixReader.Iterator iterator = tabixReader.query(region.getChrom(), region.getStart() - 1, region.getEnd());
        List<ChipInfo> chipInfoList = new ArrayList<>();
        String chipLine = "";
        while((chipLine = iterator.next()) != null) {
            ChipInfo chipInfo = new ChipInfo();
            String[] chipLineArray = chipLine.split("\t");
            chipInfo.setChrom(chipLineArray[0]);
            chipInfo.setStart(Integer.valueOf(chipLineArray[1]));
            chipInfo.setEnd(Integer.valueOf(chipLineArray[2]));

            List<String> dataList = Arrays.asList(chipLineArray).subList(3, chipLineArray.length);
            Double[] dataArray;
            if (sampleID != null && !sampleID.equals("")) {
                dataArray = new Double[sampleIndexList.size()];
            } else {
                dataArray = new Double[dataList.size()];
            }
            Integer colNum = 0;
            for (String data : dataList) {
                if (sampleID != null && !sampleID.equals("")) { // sampleID no null, filter the data
                    if (sampleIndexList.contains(colNum)) { // only get the data which sampleID index in sampleIndexList
                        dataArray[colNum] = data.equals("NA") ? Double.NaN : Double.valueOf(data);
                    } else {
                        continue;
                    }
                } else { // sampleID is null, get all data
                    dataArray[colNum] = data.equals("NA") ? Double.NaN : Double.valueOf(data);
                }
                colNum++;
            }
            chipInfo.setDataArray(dataArray);
            chipInfoList.add(chipInfo);
        }

        tabixReader.close();
        return chipInfoList;
    }

    public Map<String, List<Integer>> getWholeCpgList() throws Exception {
        TreeMap<String, List<Integer>> cpgPosListMap = new TreeMap<>();

        List<Integer> cpgPosList = new ArrayList<>();
        String chipLine = tabixReader.readLine();
        String lastChr = chipLine.split("\t")[0];
        chipLine = tabixReader.readLine();
        while(chipLine != null && !chipLine.equals("")) {
            if (chipLine.split("\t").length < 3) {
                continue;
            } else {
                if (lastChr.equals(chipLine.split("\t")[0])) {
                    cpgPosList.add(Integer.valueOf(chipLine.split("\t")[1]));
                } else {
                    cpgPosListMap.put(lastChr, cpgPosList);
                    lastChr = chipLine.split("\t")[0];
                    cpgPosList = new ArrayList<>();
                    cpgPosList.add(Integer.valueOf(chipLine.split("\t")[1]));
                }
                chipLine = tabixReader.readLine();
            }
        }
        cpgPosListMap.put(lastChr, cpgPosList);
        log.info("Read input file success.");

        tabixReader.close();
        return cpgPosListMap;
    }
}
