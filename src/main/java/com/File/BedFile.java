package com.File;

import com.bean.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BedFile implements InputFile{
    public static final Logger log = LoggerFactory.getLogger(BedFile.class);

    File bedFile;

    public BedFile(String bedPath) {
        bedFile = new File(bedPath);
    }

    @Override
    public List<?> parseByRegion(Region region) throws Exception {
        return null;
    }

    @Override
    public List<Region> parseWholeFile() throws Exception {
        List<Region> regionList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(bedFile));
        String bedLine = "";
        while ((bedLine = bufferedReader.readLine()) != null && !bedLine.equals("")) {
            if (bedLine.split("\t").length < 3) {
                log.error("Interval not in correct format.");
                break;
            }
            Region region = new Region(bedLine.split("\t")[0], Integer.valueOf(bedLine.split("\t")[1]) + 1,
                    Integer.valueOf(bedLine.split("\t")[2]));
            regionList.add(region);
        }
        return regionList;
    }

    @Override
    public Map<?, ?> parseWholeFileGroupByChr() throws Exception {
        return null;
    }

    public List<String> getLineList() throws Exception {
        List<String> lineList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(bedFile));
        String bedLine = "";
        while ((bedLine = bufferedReader.readLine()) != null && !bedLine.equals("")) {
            lineList.add(bedLine);
        }
        return lineList;
    }

    @Override
    public void close() {
    }
}
