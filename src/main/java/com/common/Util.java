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
}
