package com.guider.healthring.test;

import android.os.Environment;

import com.guider.healthring.util.GetJsonDataUtil;

/**
 * Created by Admin
 * Date 2019/9/20
 */
public class Test3 {


    public static void main(String[] arg){
        long atr = 0L;
        System.out.print("--------aa="+(atr == 0)+"\n");

        String str = "[\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 66,\n" +
                "        \"testTime\": \"2019-09-19T16:00:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 94,\n" +
                "        \"testTime\": \"2019-09-19T16:30:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 72,\n" +
                "        \"testTime\": \"2019-09-19T17:00:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 64,\n" +
                "        \"testTime\": \"2019-09-19T17:30:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 66,\n" +
                "        \"testTime\": \"2019-09-19T18:00:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 60,\n" +
                "        \"testTime\": \"2019-09-19T18:30:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 58,\n" +
                "        \"testTime\": \"2019-09-19T19:00:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 61,\n" +
                "        \"testTime\": \"2019-09-19T19:30:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 58,\n" +
                "        \"testTime\": \"2019-09-19T20:00:00Z\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"accountId\": 31,\n" +
                "        \"hb\": 65,\n" +
                "        \"testTime\": \"2019-09-19T20:30:00Z\"\n" +
                "    }]";


        new GetJsonDataUtil().writeTxtToFile(str,Environment.getExternalStorageDirectory()+"/DCIM/","heartParams.json");
    }
}
