package com.guider.healthring.commdbserver;

import com.guider.healthring.siswatch.utils.WatchUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin
 * Date 2021/5/21
 */
public class AnalysisDateUtils {

    public AnalysisDateUtils() {
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yy/MM", Locale.CHINA);
    private final SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    private final SimpleDateFormat mDSdf = new SimpleDateFormat("MM/dd",Locale.CHINA);

    String currDay = WatchUtils.getCurrentDate();
    String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");

    public synchronized Map<String,Integer> getDateMap(int code){
        String currDayStr = WatchUtils.getCurrentDate();
        String currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
        Map<String,Integer> yearMap = new HashMap<>();
        if(code == 12){ //年
            for (int i = 0; i < 12; i++) {
                yearMap.put(currMonthStr, 0);
                currMonthStr = getLastMonth(currMonthStr);
            }
            return yearMap;
        }

        for(int i = 0;i<code;i++){
            yearMap.put(dayFormatDay(currDayStr), 0);
            currDayStr = WatchUtils.obtainAroundDate(currDayStr, true);
        }
        return yearMap;
    }



    public List<String> getXAxisList(int code){
        currDay = WatchUtils.getCurrentDate();
        currMonthStr = WatchUtils.getCurrentDateFormat("yy/MM");
        List<String> xAxList = new ArrayList<>();
        if(code == 12){
            for (int i = 0; i < 12; i++) {
                xAxList.add(currMonthStr);
                currMonthStr = getLastMonth(currMonthStr);
            }
            Collections.sort(xAxList, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    return s.compareTo(t1);
                }
            });
            return xAxList;
        }

        for(int i = 0;i<code;i++){
            xAxList.add(dayFormatDay(currDay));
            currDay = WatchUtils.obtainAroundDate(currDay, true);
        }
        Collections.sort(xAxList, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        return xAxList;
    }


    //获取当前月份的上一个月
    private String getLastMonth(String cuuMonth) {
        try {
            Date currDate = sdf.parse(cuuMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private synchronized String dayFormatDay(String day) {
        try {
            Date tmpDay = daySdf.parse(day);
            assert tmpDay != null;
            return mDSdf.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public synchronized String getCusFormatDay(SimpleDateFormat sourceFormat,SimpleDateFormat resultFormat,String day){
        try {
            Date tmpDay = sourceFormat.parse(day);
            assert tmpDay != null;
            return resultFormat.format(tmpDay);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
