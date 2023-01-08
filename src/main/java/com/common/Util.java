package com.common;

import com.bean.ChipInfo;
import com.bean.Region;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
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

    public Double calculateRvalue(Double[] array1, Double[] array2, Integer nSample) {
        // filter the NaN value
        double[] dataArray1ForCalculate = new double[array1.length];
        double[] dataArray2ForCalculate = new double[array2.length];
        int index = 0;
        for (int k = 0; k < array1.length; k++) {
            if (!array1[k].isNaN() && !array2[k].isNaN()) {
                dataArray1ForCalculate[index] = array1[k];
                dataArray2ForCalculate[index] = array2[k];
                index++;
            }
        }
        if (index < nSample) {
            return Double.NaN;
        }

        // calculate the pearsons correlation
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        Double rvalue = pearsonsCorrelation.correlation(dataArray1ForCalculate, dataArray2ForCalculate);
        return rvalue;
    }
}
