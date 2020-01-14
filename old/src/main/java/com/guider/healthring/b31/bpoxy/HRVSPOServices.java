//package com.example.bozhilun.android.b31.bpoxy;
//
//import android.app.IntentService;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Intent;
//import android.content.Context;
//import android.content.IntentFilter;
//import android.os.AsyncTask;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.example.bozhilun.android.MyApp;
//import com.example.bozhilun.android.b31.model.B31HRVBean;
//import com.example.bozhilun.android.b31.model.B31Spo2hBean;
//import com.example.bozhilun.android.siswatch.utils.WatchUtils;
//import com.example.bozhilun.android.util.LocalizeTool;
//import com.google.gson.Gson;
//import com.veepoo.protocol.VPOperateManager;
//import com.veepoo.protocol.listener.base.IBleWriteResponse;
//import com.veepoo.protocol.listener.data.IHRVOriginDataListener;
//import com.veepoo.protocol.listener.data.ISpo2hOriginDataListener;
//import com.veepoo.protocol.model.datas.HRVOriginData;
//import com.veepoo.protocol.model.datas.Spo2hOriginData;
//import com.veepoo.protocol.model.settings.ReadOriginSetting;
//
//import org.litepal.LitePal;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * An {@link IntentService} subclass for handling asynchronous task requests in
// * a service on a separate handler thread.
// * <p>
// * helper methods.
// */
//public class HRVSPOServices extends Service {
//
//    private ReadHRVSoDataForDevices upDataToGDServices;
//    private LocalizeTool mLocalTool;
//    private boolean isToday = true;
//
//
//    boolean isGETDATAS = false;
//    public static boolean UpdataHRV = false;//HRV是否在更新中
//    public static boolean UpdataO2O = false;//O2O是否在更新中
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WatchUtils.B31_HRV_COMPLETE);
//        intentFilter.addAction(WatchUtils.B31_SPO2_COMPLETE);
//        registerReceiver(broadcastReceiver, intentFilter);
//
//
//        try {
//            mLocalTool = new LocalizeTool(MyApp.getContext());
//            String date = mLocalTool.getSpo2AdHRVUpdateDate();// 最后更新总数据的日期
//            if (WatchUtils.isEmpty(date))
//                date = WatchUtils.obtainFormatDate(1);
//            isToday = date.equals(WatchUtils.getCurrentDate());
//            if (upDataToGDServices != null && isGETDATAS) {
//                return;
//            }
//            if (upDataToGDServices != null && upDataToGDServices.getStatus() != null && upDataToGDServices.getStatus() == AsyncTask.Status.RUNNING) {
//                upDataToGDServices.cancel(true); // 如果Task还在运行，则先取消它
////                Log.e("-------AAA--", "先取消异步，在去重新开始");
//            } else {
//                upDataToGDServices = new ReadHRVSoDataForDevices();
//            }
//            upDataToGDServices.execute();
//        } catch (Error e) {
//        }
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (broadcastReceiver != null)
//            MyApp.getContext().unregisterReceiver(broadcastReceiver);
//    }
//
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    };
//
//
//    /**
//     * 异步读取保存 HRV 和 血氧
//     */
//    public class ReadHRVSoDataForDevices extends AsyncTask<Void, Void, Void> {
//        private static final String TAG = "ReadHRVSoDataForDevices";
//        VPOperateManager vpOperateManager = null;
//        private List<B31HRVBean> b31HRVBeanList;
//        Gson gson = new Gson();
//
//        //血氧
//        private List<B31Spo2hBean> b31Spo2hBeanList;
//
//
//        // 方法1：onPreExecute（）
//        // 作用：执行 线程任务前的操作
//        // 注：根据需求复写
//        @Override
//        protected void onPreExecute() {
////            Log.e("-------AAA--", "异步初始化了 " + isGETDATAS);
//            vpOperateManager = MyApp.getInstance().getVpOperateManager();
//        }
//
//
//        private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
//            @Override
//            public void onResponse(int i) {
//
//            }
//        };
//
//
//        // 方法2：doInBackground（）
//        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
//        // 注：必须复写，从而自定义线程任务
//        @Override
//        protected Void doInBackground(Void... voids) {
//            // Task被取消了，马上退出循环
//            if (isCancelled() || isGETDATAS) return null;
////            Log.e("-------AAA--", "异步任务开始啦 " + isGETDATAS);
//            // 可调用publishProgress（）显示进度, 之后将执行onProgressUpdate（）
////        publishProgress();
//            try {
//                isGETDATAS = true;
//                UpdataHRV = true;
//                UpdataO2O = true;
//
//                readDeviceData();
//            } catch (Error e) {
//            }
//            return null;
//        }
//
//        //读取HRV的数据
//        private void readDeviceData() {
//
//            final long readTime = System.currentTimeMillis() / 1000;
//            if (b31HRVBeanList == null)
//                b31HRVBeanList = new ArrayList<>();
//            b31HRVBeanList.clear();
//            final List<B31HRVBean> yesHrvList = new ArrayList<>();
//            final List<B31HRVBean> threeDayHrvList = new ArrayList<>();
//            final Map<String, List<B31HRVBean>> hrvMap = new HashMap<>();
//
//            /**
//             * 参数:
//             * day - 读取位置，0表示今天，1表示昨天，2表示前天，以此类推。 读取顺序为 今天-昨天- 前天，
//             * position - 读取的条数位置,此值要求必须大于等于1
//             * onlyReadOneDay - true表示只读今天，false表示按顺序读取
//             * watchday - 手表存储的天数
//             */
//            ReadOriginSetting readOriginSetting = new ReadOriginSetting(0, 1, isToday, isToday ? 1 : 3);
//            vpOperateManager.readHRVOriginBySetting(bleWriteResponse, new IHRVOriginDataListener() {
//                @Override
//                public void onReadOriginProgress(float v) {
////                    Log.e(TAG, "-------AAA----------hrv--onReadOriginProgress=" + v + "--value=" + String.valueOf(v));
//                    if (String.valueOf(v).equals("1.0")) {
//                        if (isToday) {
//                            hrvMap.put("today", b31HRVBeanList);
//                        } else {
//                            hrvMap.put("today", b31HRVBeanList);
//                            hrvMap.put("yesDay", yesHrvList);
//                            hrvMap.put("threeDay", threeDayHrvList);
//                        }
//                        saveHRVToDBServer(hrvMap);
//                    }
//                }
//
//                @Override
//                public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {
////                    Log.e(TAG, "--------hrv--onReadOriginProgressDetail=" + i + "-----s=" + s + "---i1=" + i1 + "---i2=" + i2);
//                }
//
//                @Override
//                public void onHRVOriginListener(HRVOriginData hrvOriginData) {
////                    Log.e(TAG, "--------hrv--hrvOriginData=" + hrvOriginData.toString());
//                    if (isToday) {    //当天的
//                        B31HRVBean hrvBean = new B31HRVBean();
//                        hrvBean.setBleMac(MyApp.getInstance().getMacAddress());
//                        hrvBean.setDateStr(hrvOriginData.getDate());
//                        hrvBean.setHrvDataStr(gson.toJson(hrvOriginData));
//                        b31HRVBeanList.add(hrvBean);
//                    } else {
//                        if (hrvOriginData.getDate().equals(WatchUtils.getCurrentDate())) {    //当天
//                            B31HRVBean hrvBean = new B31HRVBean();
//                            hrvBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            hrvBean.setDateStr(hrvOriginData.getDate());
//                            hrvBean.setHrvDataStr(gson.toJson(hrvOriginData));
//                            b31HRVBeanList.add(hrvBean);
//                        } else if (hrvOriginData.getDate().equals(WatchUtils.obtainFormatDate(1))) {   //昨天
//                            B31HRVBean hrvBean = new B31HRVBean();
//                            hrvBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            hrvBean.setDateStr(hrvOriginData.getDate());
//                            hrvBean.setHrvDataStr(gson.toJson(hrvOriginData));
//                            yesHrvList.add(hrvBean);
//                        } else if (hrvOriginData.getDate().equals(WatchUtils.obtainFormatDate(2))) {   //前天
//                            B31HRVBean hrvBean = new B31HRVBean();
//                            hrvBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            hrvBean.setDateStr(hrvOriginData.getDate());
//                            hrvBean.setHrvDataStr(gson.toJson(hrvOriginData));
//                            threeDayHrvList.add(hrvBean);
//                        }
//                    }
//                }
//
//                @Override
//                public void onDayHrvScore(int i, String s, int i1) {
////                    Log.e(TAG, "-----------hrv=----onDayHrvScore=" + i + "--s=" + s + "---i1=" + i1);
//                }
//
//                @Override
//                public void onReadOriginComplete() {
//                    //Log.e(TAG, "-----------hrv=----onReadOriginComplete=" + System.currentTimeMillis() / 1000 + "------hrv差=" + (System.currentTimeMillis() / 1000 - readTime));
//
//                }
//            }, readOriginSetting);
//
//        }
//
//
//        //保存HRV的数据
//        private void saveHRVToDBServer(Map<String, List<B31HRVBean>> resultHrvMap) {
//            String where = "bleMac = ? and dateStr = ?";
//            String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());
//            String currDayStr = WatchUtils.getCurrentDate();
//            if (isToday) {
//                List<B31HRVBean> todayHrvList = resultHrvMap.get("today");
//                List<B31HRVBean> currHrvLt = LitePal.where(where,
//                        bleMac, currDayStr).find(B31HRVBean.class);
////                Log.e(TAG, "-------AAA-------今天currHrvLtsez=" + currHrvLt.size());
//                if (currHrvLt == null || currHrvLt.isEmpty()) {
//                    LitePal.saveAll(todayHrvList);
//                } else {
//                    if (currHrvLt.size() != 420) {
//                        int delCode = LitePal.deleteAll(B31HRVBean.class, "dateStr=? and bleMac=?", currDayStr
//                                , bleMac);
////                        Log.e(TAG, "-------AAA----hrv--111--delCode=" + delCode);
//                        LitePal.saveAll(todayHrvList);
//                    }
//                }
//
//
//            } else {
//                //今天的 直接保存
//                List<B31HRVBean> todayHrvList = resultHrvMap.get("today");
//
//                if (todayHrvList != null && !todayHrvList.isEmpty()) {
////                    Log.e(TAG, "-------AAA--今日 HRV" + todayHrvList.size() + "==" + todayHrvList.toString());
//                    LitePal.saveAll(todayHrvList);
//                }
//
//
//                //昨天的
//                List<B31HRVBean> yesDayResultHrvLt = resultHrvMap.get("yesDay");
//                //保存的 是否存在
//                List<B31HRVBean> yesDayHrvLt = LitePal.where(where,
//                        WatchUtils.getSherpBleMac(MyApp.getContext()), WatchUtils.obtainFormatDate(1)).find(B31HRVBean.class);
//                if (yesDayHrvLt == null || yesDayHrvLt.isEmpty()) {
////                    Log.e(TAG, "-------AAA--昨天 HRV" + todayHrvList.size() + "==" + todayHrvList.toString());
//                    LitePal.saveAll(yesDayResultHrvLt);
//                }
//
//
//                //前天的
//                List<B31HRVBean> threeDayResultHrvLt = resultHrvMap.get("threeDay");
//                List<B31HRVBean> threeDayHrvLt = LitePal.where(where,
//                        WatchUtils.getSherpBleMac(MyApp.getContext()), WatchUtils.obtainFormatDate(2)).find(B31HRVBean.class);
//                if (threeDayHrvLt == null || threeDayHrvLt.isEmpty()) {
////                    Log.e(TAG, "-------AAA--前天 HRV" + todayHrvList.size() + "==" + todayHrvList.toString());
//                    LitePal.saveAll(threeDayResultHrvLt);
//                }
//
//
//            }
//
//            UpdataHRV = false;
//
//            //发送广播，通知更新UI
//            Intent intent = new Intent();
//            intent.setAction(WatchUtils.B31_HRV_COMPLETE);
//            MyApp.getContext().sendBroadcast(intent);
//
//            readSpo2Data();
//
//
//        }
//
//
//        //读取血氧的数据
//        private void readSpo2Data() {
//            final long spo2CurrTime = System.currentTimeMillis() / 1000;
////            Log.e(TAG, "-------isToday=" + isToday);
//            if (b31Spo2hBeanList == null)
//                b31Spo2hBeanList = new ArrayList<>();
//            b31Spo2hBeanList.clear();
//
//            final List<B31Spo2hBean> yesSpo2List = new ArrayList<>();
//            final List<B31Spo2hBean> threeSpo2List = new ArrayList<>();
//            final Map<String, List<B31Spo2hBean>> spo2Map = new HashMap<>();
//
//            /**
//             * 参数:
//             * day - 读取位置，0表示今天，1表示昨天，2表示前天，以此类推。 读取顺序为 今天-昨天- 前天，
//             * position - 读取的条数位置,此值要求必须大于等于1
//             * onlyReadOneDay - true表示只读今天，false表示按顺序读取
//             * watchday - 手表存储的天数
//             */
//            ReadOriginSetting readOriginSetting = new ReadOriginSetting(0, 1, isToday, isToday ? 1 : 3);
//
//            vpOperateManager.readSpo2hOriginBySetting(bleWriteResponse, new ISpo2hOriginDataListener() {
//                @Override
//                public void onReadOriginProgress(float v) {
////                    Log.e(TAG, "---------spo2---onReadOriginProgress=" + v + "---value=" + String.valueOf(v));
//                    //spo2DataProgress =  v;
//                    if (String.valueOf(v).equals("1.0")) {
//                        if (isToday) {
//                            spo2Map.put("today", b31Spo2hBeanList);
//                        } else {
//                            spo2Map.put("today", b31Spo2hBeanList);
//                            spo2Map.put("yesToday", yesSpo2List);
//                            spo2Map.put("threeDay", threeSpo2List);
//                        }
//
//                        saveSpo2Data(spo2Map);
//                        mLocalTool.putSpo2AdHRVUpdateDate(WatchUtils.getCurrentDate());
//                    }
//                }
//
//                @Override
//                public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onSpo2hOriginListener(Spo2hOriginData spo2hOriginData) {
//                    if (spo2hOriginData == null)
//                        return;
//                    if (isToday) {    //只获取当天的，当天
//                        B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
//                        b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
//                        b31Spo2hBean.setDateStr(spo2hOriginData.getDate());
//                        b31Spo2hBean.setSpo2hOriginData(gson.toJson(spo2hOriginData));
//                        b31Spo2hBeanList.add(b31Spo2hBean);
//                    } else {  //今天 昨天 前天
//                        if (spo2hOriginData.getDate().equals(WatchUtils.getCurrentDate())) {  //今天
//                            B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
//                            b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            b31Spo2hBean.setDateStr(spo2hOriginData.getDate());
//                            b31Spo2hBean.setSpo2hOriginData(gson.toJson(spo2hOriginData));
//                            b31Spo2hBeanList.add(b31Spo2hBean);
//                        } else if (spo2hOriginData.getDate().equals(WatchUtils.obtainFormatDate(1))) { //昨天
//                            B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
//                            b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            b31Spo2hBean.setDateStr(spo2hOriginData.getDate());
//                            b31Spo2hBean.setSpo2hOriginData(gson.toJson(spo2hOriginData));
//                            yesSpo2List.add(b31Spo2hBean);
//                        } else if (spo2hOriginData.getDate().equals(WatchUtils.obtainFormatDate(2))) { //前天
//                            B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
//                            b31Spo2hBean.setBleMac(MyApp.getInstance().getMacAddress());
//                            b31Spo2hBean.setDateStr(spo2hOriginData.getDate());
//                            b31Spo2hBean.setSpo2hOriginData(gson.toJson(spo2hOriginData));
//                            threeSpo2List.add(b31Spo2hBean);
//                        }
//                    }
//                }
//
//                @Override
//                public void onReadOriginComplete() {
////                    Log.e(TAG, "-----------spo2-onReadOriginComplete=-差值=" + (System.currentTimeMillis() / 1000 - spo2CurrTime));
//
//                }
//            }, readOriginSetting);
//        }
//
//        //保存spo2数据
//        private void saveSpo2Data(Map<String, List<B31Spo2hBean>> resultMap) {
//            if (resultMap == null || resultMap.isEmpty())
//                return;
//            String where = "bleMac = ? and dateStr = ?";
//            String bleMac = WatchUtils.getSherpBleMac(MyApp.getContext());
//            String currDayStr = WatchUtils.getCurrentDate();
//            if (isToday) {    //今天
//                List<B31Spo2hBean> todayLt = resultMap.get("today");
////                Log.e(TAG, "---todayLt=" + todayLt.size());
//                //查询一下是否存在
//                List<B31Spo2hBean> currList = LitePal.where(where, WatchUtils.getSherpBleMac(MyApp.getContext()),
//                        WatchUtils.getCurrentDate()).find(B31Spo2hBean.class);
////                Log.e(TAG,"-----------11今天="+currList.size());
//                if (currList == null || currList.isEmpty()) {
//                    LitePal.saveAll(todayLt);
//                } else {
//                    if (currList.size() != 420) {
//                        int delCode = LitePal.deleteAll(B31Spo2hBean.class, "dateStr=? and bleMac=?", currDayStr
//                                , bleMac);
////                        Log.e(TAG, "--------delCode=" + delCode);
//
//                        LitePal.saveAll(todayLt);
//
//                    }
//                }
//            } else {
//                //今天
//                List<B31Spo2hBean> todayLt = resultMap.get("today");
//                if (todayLt != null && !todayLt.isEmpty()) {
//                    LitePal.saveAll(todayLt);
//                }
//
//                //昨天
//                List<B31Spo2hBean> yesDayResult = resultMap.get("yesToday");
//                if (yesDayResult == null || yesDayResult.isEmpty())
//                    return;
//                //查询一下是否存在
//                List<B31Spo2hBean> yesDayList = LitePal.where(where, WatchUtils.getSherpBleMac(MyApp.getContext()),
//                        WatchUtils.obtainFormatDate(1)).find(B31Spo2hBean.class);
////                Log.e(TAG,"-----------22昨天="+todayLt.size());
//                if (yesDayList == null || yesDayList.isEmpty()) {
//                    LitePal.saveAll(yesDayResult);
//                }
//
//                //前天
//                List<B31Spo2hBean> threeDayResult = resultMap.get("threeDay");
//                if (threeDayResult == null || threeDayResult.isEmpty())
//                    return;
//                //查询一下是否存在
//                List<B31Spo2hBean> threeDayList = LitePal.where(where, WatchUtils.getSherpBleMac(MyApp.getContext()),
//                        WatchUtils.obtainFormatDate(2)).find(B31Spo2hBean.class);
//                //Log.e(TAG,"--------333-threeDayList="+threeDayList.size());
//                if (threeDayList == null || threeDayList.isEmpty()) {
//                    LitePal.saveAll(threeDayResult);
//                }
//            }
////            UpdataHRV = false;
//            UpdataO2O = false;
//
//            //发送广播，通知更新UI  HRV
////            Intent intent = new Intent();
////            intent.setAction(WatchUtils.B31_HRV_COMPLETE);
////            MyApp.getContext().sendBroadcast(intent);
////
//            //血氧
//            Intent intent1 = new Intent();
//            intent1.setAction(WatchUtils.B31_SPO2_COMPLETE);
//            MyApp.getContext().sendBroadcast(intent1);
//        }
//
//
//        // 方法3：onProgressUpdate（）
//        // 作用：在主线程 显示线程任务执行的进度
//        // 注：根据需求复写
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            if (isCancelled()) return;
//        }
//
//        // 方法4：onPostExecute（）
//        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
//        // 注：必须复写，从而自定义UI操作
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            isGETDATAS = false;
////            Log.e("-------AAA--", "异步任务读取结束啦 " + isGETDATAS);
//        }
//
//        // 方法5：onCancelled()
//        // 作用：将异步任务设置为：取消状态
//        @Override
//        protected void onCancelled() {
//            //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
//            if (getStatus() == AsyncTask.Status.RUNNING) {
//                cancel(true);
//            }
//            // 正在读取数据,写到全局,保证同时只有一个本服务在运行
//            isGETDATAS = false;
////            Log.e("-------AAA--", "异步任务取消了 " + isGETDATAS);
//
//        }
//
//
//    }
//
//
//}
