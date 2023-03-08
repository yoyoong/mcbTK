package com.common;

import com.bean.ChipInfo;
import com.bean.RInfo;
import com.bean.Region;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Util {
    public static final Logger log = LoggerFactory.getLogger(Util.class);

    public Integer indexOfList(List<Integer> list, Integer findValue) {
        Integer low = 0;
        Integer high = list.size() - 1;
        while(low <= high){
            Integer mid = (low + high) / 2;
            Integer midVal = list.get(mid);
            if(midVal < Integer.valueOf(findValue)) {
                low = mid+1;
            } else if (midVal > findValue) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    public List<Integer> getCpgPosListInRegion(List<ChipInfo> chipInfoList, Region region) {
        List<Integer> cpgPosListInRegion = new ArrayList<>();
        for (ChipInfo chipInfo : chipInfoList) {
            cpgPosListInRegion.add(chipInfo.getStart());
        }
        return cpgPosListInRegion;
    }

    public RInfo calculateRvalue(Double[] array1, Double[] array2, Integer nSample) {
        RInfo  rInfo = new RInfo();
        // filter the NaN value
        double[] dataArray1 = new double[array1.length];
        double[] dataArray2 = new double[array2.length];
        int index = 0;
        for (int k = 0; k < array1.length; k++) {
            if (k == array1.length - 1) {
                int d = 4;
            }
            if (!array1[k].isNaN() && !array2[k].isNaN()) {
                dataArray1[index] = array1[k];
                dataArray2[index] = array2[k];
                index++;
            }
        }
        if (index < nSample) {
            rInfo.setRvalue(Double.NaN);
            return rInfo;
        }
        double[] dataArray1ForCalculate = Arrays.copyOfRange(dataArray1, 0, index);
        double[] dataArray2ForCalculate = Arrays.copyOfRange(dataArray2, 0, index);

        // calculate the pearsons correlation
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        Double rvalue = pearsonsCorrelation.correlation(dataArray1ForCalculate, dataArray2ForCalculate);
        Double pvalue = TestUtils.pairedTTest(dataArray1ForCalculate, dataArray2ForCalculate);
        rInfo.setRvalue(rvalue);
        rInfo.setPvalue(pvalue);
        return rInfo;
    }

    public List<Region> splitRegionToSmallRegion(Region region, Integer splitSize, Integer shift) {
        List<Region> regionList = new ArrayList<>();
        if (region.getEnd() - region.getStart() > splitSize) {
            Integer regionNum = (region.getEnd() - region.getStart()) / splitSize + 1;
            for (int i = 0; i < regionNum; i++) {
                Integer end = 0;
                if (region.getStart() + splitSize + shift - 1 <= region.getEnd()) {
                    end = region.getStart() + splitSize + shift - 1;
                } else {
                    end = region.getEnd();
                }
                Region newRegion = new Region(region.getChrom(), region.getStart(), end);
                regionList.add(newRegion);
                if (newRegion.getEnd() - shift + 1 < 1) {
                    region.setStart(newRegion.getEnd() + 1);
                } else {
                    region.setStart(newRegion.getEnd() - shift + 1);
                }
            }
        } else {
            regionList.add(region);
        }
        return regionList;
    }
}
