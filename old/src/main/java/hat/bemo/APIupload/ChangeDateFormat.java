package hat.bemo.APIupload;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hat.bemo.MyApplication;
import hat.bemo.setting.SharedPreferences_status;

/**
 * Created by apple on 2017/11/10.
 */

public class ChangeDateFormat {

    /*
     * yyyy-MM-dd 1
     * MM-dd-yyyy 2
     * dd-MM-yyyy 3
     * */
    private static SharedPreferences_status sh = new SharedPreferences_status(MyApplication.context);
    private static String Time_Status;
    private static String Date_Status;

    @SuppressLint("SimpleDateFormat")
    public static String CreateDate(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String BsTime(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String UpdataDate(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String Year(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMonth(){
        SimpleDateFormat dateformat = new SimpleDateFormat("MM");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDay(){
        SimpleDateFormat dateformat = new SimpleDateFormat("dd");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getHour(){
        SimpleDateFormat dateformat = new SimpleDateFormat("HH");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMinute(){
        SimpleDateFormat dateformat = new SimpleDateFormat("mm");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getSecond(){
        SimpleDateFormat dateformat = new SimpleDateFormat("ss");
        String format_Date = dateformat.format(new Date());
        return format_Date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String TimeStamp(){
        return String.valueOf(new Date().getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetDate(String name){
        try {
            String format_Date="";
            SimpleDateFormat dateformat = null;

            Time_Status = sh.Get_TimeFormat("TIMEFORMAT", "TIMEFORMAT");
            Date_Status = sh.Get_DateFormat("DATEFORMAT", "DATEFORMAT");

            if(Date_Status.equals("DATE_1") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_1") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_2") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_2") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_3") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_3") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS 'GMT'Z");
                format_Date = dateformat.format(new Date());
            }
            return format_Date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DateFormat(){
        try {
            String CreateDate = "";
            SimpleDateFormat dateformat = null;
            Date_Status = sh.Get_DateFormat("DATEFORMAT", "DATEFORMAT");

            if(Date_Status.equals("DATE_1")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd");
                CreateDate = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_2")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy");
                CreateDate = dateformat.format(new Date());
            }else if(Date_Status.equals("DATE_3")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy");
                CreateDate = dateformat.format(new Date());
            }
            return CreateDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DateFormat2(Date date){
        try {
            String CreateDate = "";
            SimpleDateFormat dateformat = null;
            Date_Status = sh.Get_DateFormat("DATEFORMAT", "DATEFORMAT");

            if(Date_Status.equals("DATE_1")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd");
                CreateDate = dateformat.format(date);
            }else if(Date_Status.equals("DATE_2")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy");
                CreateDate = dateformat.format(date);
            }else if(Date_Status.equals("DATE_3")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy");
                CreateDate = dateformat.format(date);
            }
            return CreateDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String TimeFormat(){
        try {
            String CreateDate = "";
            SimpleDateFormat dateformat = null;
            Time_Status = sh.Get_TimeFormat("TIMEFORMAT", "TIMEFORMAT");

            if(Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("HH:mm");
                CreateDate = dateformat.format(new Date());
            }else if(Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("hh:mm");
                CreateDate = dateformat.format(new Date());
            }
            return CreateDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String TimeFormat2(Date date){
        try {
            String CreateDate = "";
            SimpleDateFormat dateformat = null;
            Time_Status = sh.Get_TimeFormat("TIMEFORMAT", "TIMEFORMAT");

            if(Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("HH:mm");
                CreateDate = dateformat.format(date);
            }else if(Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("hh:mm");
                CreateDate = dateformat.format(date);
            }
            return CreateDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String SimpleDateTimeFormat(Date Date){
        try {
            String format_Date="";
            SimpleDateFormat dateformat = null;

            Time_Status = sh.Get_TimeFormat("TIMEFORMAT", "TIMEFORMAT");
            Date_Status = sh.Get_DateFormat("DATEFORMAT", "DATEFORMAT");

            if(Date_Status.equals("DATE_1") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                format_Date = dateformat.format(Date);
            }else if(Date_Status.equals("DATE_1") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                format_Date = dateformat.format(Date);
            }else if(Date_Status.equals("DATE_2") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                format_Date = dateformat.format(Date);
            }else if(Date_Status.equals("DATE_2") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("MM-dd-yyyy hh:mm");
                format_Date = dateformat.format(Date);
            }else if(Date_Status.equals("DATE_3") & Time_Status.equals("TIME_0")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                format_Date = dateformat.format(Date);
            }else if(Date_Status.equals("DATE_3") & Time_Status.equals("TIME_1")){
                dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                format_Date = dateformat.format(Date);
            }
            return format_Date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDatebirt(String dateString){
        try {
            SimpleDateFormat dateformat = null;
            dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");
            Date date = dateformat.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
