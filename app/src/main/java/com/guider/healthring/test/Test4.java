package com.guider.healthring.test;

import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;

import java.util.Date;

/**
 * Created by Admin
 * Date 2019/10/5
 */
public class Test4 {

    public static void main(String[] arg){
        //String str = "2019-11-14 00:39:00";
        String str = "2019-11-14 03:14:00";
        Date dateStart = W30BasicUtils.stringToDate(str.replace(":", "-"), "yyyy-MM-dd HH-mm-ss");
        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);

        System.out.println("-------iso="+iso8601Timestamp+"\n");
    }



}
