package com.centit.framework.users.controller;

import com.centit.support.algorithm.DatetimeOpt;

import java.text.ParseException;
import java.util.Date;

/**
 * @author zfg
 */
public class TestUtil {

    public static void main(String[] args) {
        System.out.println(DatetimeOpt.currentDatetime());
        System.out.println(System.currentTimeMillis()/1000);
        System.out.println(System.currentTimeMillis()/1000 + 7200);
        System.out.println(DatetimeOpt.convertDateToString(
            new Date((System.currentTimeMillis()+7200*1000)), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DatetimeOpt.convertDatetimeToString(new Date((System.currentTimeMillis()+7200*1000))));
        try {
            System.out.println(DatetimeOpt.convertStringToDate(DatetimeOpt.currentDatetime(), "yyyy-MM-dd HH:mm:ss").getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
