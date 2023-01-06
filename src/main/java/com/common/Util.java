package com.common;

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

    public Map<String, Integer> getPatternInWindow(List<MHapInfo> mHapList, List<Integer> cpgPosListInWindow,
                                                   Integer startCpg, Integer endCpg) {
        Map<String, Integer> patternMap = new HashMap<>();

        List<MHapInfo> mHapListInWindow = new ArrayList<>();
        for (MHapInfo mHapInfo : mHapList) {
            if (mHapInfo.getStart() <= startCpg && mHapInfo.getEnd() >= endCpg) {
                mHapListInWindow.add(mHapInfo);
            }
            if (mHapInfo.getStart() > startCpg) {
                break;
            }
        }

        for (MHapInfo mHapInfo : mHapListInWindow) {
            Integer startCpgIndex = indexOfList(cpgPosListInWindow, startCpg) - indexOfList(cpgPosListInWindow, mHapInfo.getStart());
            Integer endCpgIndex = indexOfList(cpgPosListInWindow, endCpg) - indexOfList(cpgPosListInWindow, mHapInfo.getStart());
            String pattern = mHapInfo.getCpg().substring(startCpgIndex, endCpgIndex + 1);
            if (patternMap.containsKey(pattern)) {
                patternMap.put(pattern, patternMap.get(pattern) + mHapInfo.getCnt());
            } else {
                patternMap.put(pattern, mHapInfo.getCnt());
            }
        }
        return patternMap;
    }
}
