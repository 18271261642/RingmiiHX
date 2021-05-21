package com.guider.healthring.test;

import com.google.gson.Gson;
import com.guider.healthring.siswatch.H8ShareActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/9/17
 */
public class Test1 {

    public static void main(String[] arg){

     Map<String,String> map = new HashMap<>();
     map.put("var1","abc");
     map.put("var2","efg");

     System.out.println("-map="+map.toString());

     map.clear();

     System.out.println("---2-map="+map.toString());

    }
}
