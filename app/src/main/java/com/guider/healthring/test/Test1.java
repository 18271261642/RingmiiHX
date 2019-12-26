package com.guider.healthring.test;

import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2019/9/17
 */
public class Test1 {

    public static void main(String[] arg){

       System.out.print("-----str="+Arrays.toString(new String[]{})+"\n");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);


        Date dateStart = W30BasicUtils.stringToDate("2019-09-18", "yyyy-MM-dd");

        try {
            Date dateStr = new Date(sdf.parse("2019-09-18").getTime());

            String str = WatchUtils.getISO8601Timestamp(dateStart,"yyyy-MM-dd");
            System.out.print("-------str="+str+"\n");

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String iso8601Timestamp = WatchUtils.getISO8601Timestamp(dateStart);
//        System.out.print("------ios="+iso8601Timestamp+"\n");



    }
}
