package com.guider.healthring.b30.bean;

import android.os.Handler;
import android.util.Log;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.b31.model.B31HRVBean;
import com.guider.healthring.siswatch.utils.WatchUtils;

import org.litepal.LitePal;
import java.util.List;

import hat.bemo.DataBase.TABLE_0x20;

/**
 * 数据库操作: B30半小时数据源
 *
 * @author XuBo 2018-09-19
 */

public class B30HalfHourDao {

    Handler handler = new Handler();

    /**
     * 单例
     */
    private static B30HalfHourDao mInstance;

    private B30HalfHourDao() {
    }

    /**
     * 获取单例
     */
    public static B30HalfHourDao getInstance() {
        if (mInstance == null) {
            mInstance = new B30HalfHourDao();
        }
        return mInstance;
    }

    /**
     * 数据源类型: 步数数据
     */
    public static final String TYPE_STEP = "step";
    //步数，kcak,dis字段
    public static final String TYPE_STEP_DETAIL = "step_detail";
    /**
     * 数据源类型: 运动数据
     */
    public static final String TYPE_SPORT = "sport";
    /**
     * 数据源类型: 心率数据
     */
    public static final String TYPE_RATE = "rate";
    /**
     * 数据源类型: 血压数据
     */
    public static final String TYPE_BP = "bp";
    /**
     * 数据源类型: 睡眠数据
     */
    public static final String TYPE_SLEEP = "sleep";

    //精准睡眠
    public static final String TYPE_PRECISION_SLEEP = "precision_sleep";

    //心电数据key
    public static final String TYPE_ECG_DATA = "ecg_data";


    // List<TempB31HRVBean> resultList = new ArrayList<>();

    /**
     * 获取单条数据源
     *
     * @param address 手环MAC地址
     * @param date    日期
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 数据源Json字符串
     */
    private B30HalfHourDB getOriginData(String address, String date, String type) {
        try {
            String where = "address = ? and date = ? and type = ?";
            List<B30HalfHourDB> resultList = LitePal.where(where, address, date, type).limit(1).find
                    (B30HalfHourDB.class);// 一个类型,同一天只有一条数据
            return (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查找单条数据源
     *
     * @param address 手环MAC地址
     * @param date    日期
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 数据源Json字符串
     */
    public String findOriginData(String address, String date, String type) {
        try {
            B30HalfHourDB result = getOriginData(address, date, type);
            return result == null ? null : result.getOriginData();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public void findBetweenStep(String address, String date, String type) {


        try {
            String startDate = "2019-03-01";
            String endDate = "2019-03-04";
            String mac = MyApp.getInstance().getMacAddress();
            //List<B30HalfHourDB> ltH = LitePal.where("date  between  ? and ?"+startDate+endDate).find(B30HalfHourDB.class);
            String where = "dateStr = ? ";
            List<B31HRVBean> resultList = LitePal.where(where, date).find(B31HRVBean.class);

            if (resultList == null) {
                for (B31HRVBean bd : resultList) {
                    if (Commont.isDebug)Log.e("DB", "--------bd=" + bd.toString());
                }
            } else {
                if (Commont.isDebug)Log.e("DB", "------------数据查询为null---");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 保存(更新)单条数据源
     *
     * @param db 数据源实体类
     */
    public synchronized void saveOriginData(B30HalfHourDB db) {
        try {
            boolean result;
            String bMac = db.getAddress();
            String strDate = db.getDate();
            String type = db.getType();
            Log.e("DB","------bleMac="+bMac);

            result = db.saveOrUpdate("address=? and date =? and type=?", bMac, strDate, type);
            if (Commont.isDebug)Log.e("DB", "--------数据存储=" + result);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 根据类型查找所有没上传服务器的数据源,不分日期
     *
     * @param address 手环MAC地址
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 指定类型的, 没有上传服务器的所有数据源
     */
    public List<B30HalfHourDB> findNotUploadData(String address, String type) {
        try {
            String where = "upload = 0 and address = ? and type = ?";
//        String where = "address = ? and type = ?";
            return LitePal.where(where, address, type).find(B30HalfHourDB.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据类型查找所有没上传服务器的数据源,不分日期
     *
     * @param address 手环MAC地址
     * @param type    数据类型{@link #TYPE_STEP}
     * @return 指定类型的, 没有上传服务器的所有数据源
     */
    public List<B30HalfHourDB> findNotUploadDataGD(String address, String type) {
        try {
            String where = "uploadGD = 0 and address = ? and type = ?";
//        String where = "address = ? and type = ?";
            return LitePal.where(where, address, type).find(B30HalfHourDB.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 查找盖德当天的数据
     * @param address
     * @param type
     * @return
     */
    public List<B30HalfHourDB> findGDTodayData(String address, String type){
        try {
            String where = "address = ? and type = ? and date = ?";
            return LitePal.where(where,address,type,WatchUtils.getCurrentDate()).find(B30HalfHourDB.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 查找数据库中三天的数据
     * @param address
     * @param type
     * @return
     */
    public List<B30HalfHourDB> findGDThreeDaysData(String address,String type){
        try {
            String where = "address = ? and type = ? and date between ? and ?";
            List<B30HalfHourDB> threeDataList = LitePal.where(where,address,type,
                    WatchUtils.obtainFormatDate(2),WatchUtils.getCurrentDate()).find(B30HalfHourDB.class);
            return  threeDataList == null || threeDataList.isEmpty() ? null : threeDataList;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }
}