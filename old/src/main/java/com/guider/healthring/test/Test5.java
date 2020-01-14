package com.guider.healthring.test;

import com.guider.healthring.siswatch.utils.WatchUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/11/15
 */
public class Test5 {

    public static void main(String[] arg){

        String yesDay = WatchUtils.obtainAroundDate(WatchUtils.getCurrentDate(),false,0);
        System.out.println("-----yee="+yesDay+"\n");
        Map<String,String> resultMap = new HashMap<>();
        for(int i = 0;i<10;i++){
            yesDay = WatchUtils.obtainAroundDate(yesDay,true,0);
            resultMap.put(yesDay,"0");
            System.out.println("------yestDay="+yesDay+"\n");
        }
    }
}
