package com.xych.jvm.practice;

import java.util.ArrayList;
import java.util.List;

public class StringOOM {
    public static void main(String[] args) {
        List<String> strList = new ArrayList<>();
        String str = "a";
        long idx = 0;
        try {
            for(;; idx++) {
                String temp = str + idx;
                str = str + str;
                strList.add(temp.intern());
            }
        }
        catch(Exception e) {
            System.out.println(idx);
        }
    }
}
