package com.guider.healthring.test;

import com.google.gson.Gson;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.w30s.utils.W30BasicUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2019/9/17
 */
public class Test1 {

    public static void main(String[] arg){

     int[] art1 = new int[]{-390, -513, -627, -732, -831, -930, -1035, -1146, -1263, -1386, -1509, -1623, -1716, -1779, -1809, -1797, -1743, -1653, -1533, -1395, -1251, -1110, -981, -873, -786};

     int[] art2 = new int[]{-183, -162, -141, -117, -93, -66, -39, -12, 15, 42, 72, 102, 129, 159, 186, 213, 240, 264, 288, 309, 333, 351, 369, 387, 402};


        List<int[]>  list = new ArrayList<>();
        list.add(art1);
        list.add(art2);


        System.out.println("---list="+new Gson().toJson(list));

    }
}
