package com.guider.health.common.cache;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.DateTypeAdapter;
import com.guider.health.apilib.IUserHDApi;
import com.guider.health.apilib.model.hd.ArtMeasure;
import com.guider.health.apilib.model.hd.BloodoxygenMeasure;
import com.guider.health.apilib.model.hd.BloodsugarMeasure;
import com.guider.health.apilib.model.hd.BpMeasure;
import com.guider.health.apilib.model.hd.Heart12Measure;
import com.guider.health.apilib.model.hd.HeartBpmMeasure;
import com.guider.health.apilib.model.hd.HeartStateMeasure;
import com.guider.health.apilib.model.hd.HeartStateMeasure_Hd;
import com.guider.health.common.core.HearRate12;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.NetworkUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.POST;


public class MeasureDataUploader {

    Context context;

    boolean isWorking = false;

    /**
     * 重新提交缓存数据
     *
     * @param cacheData
     * @return 如果返回null , 就是匹配上了 , 要是返回CacheData就是没匹配上URL
     */
    public CacheData reuploadCacheData(CacheData cacheData) {
        String url = cacheData.getUrl();
        Method[] methods = IUserHDApi.class.getMethods();
        for (Method method : methods) {
            POST post = method.getAnnotation(POST.class);
            if (post != null) {
                String value = post.value();
                if (url.contains(value)) {
                    String name = method.getName();
                    switch (name) {
                        case "sendHeartBpm":
                            // 心率
                            HeartBpmMeasure heartBpm = fromGson(cacheData.getGson(), HeartBpmMeasure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendHeartBpm(Arrays.asList(heartBpm)).enqueue(new CacheCallback(cacheData));
                            return null;
                        case "sendBp":
                            // 血压
                            BpMeasure bpMeasure = fromGson(cacheData.getGson(), BpMeasure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendBp(Arrays.asList(bpMeasure)).enqueue(new CacheCallback(cacheData));
                            return null;
                        case "sendArt":
                            // 动脉硬化
                            ArtMeasure artMeasure = fromGson(cacheData.getGson(), ArtMeasure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendArt(Arrays.asList(artMeasure)).enqueue(new CacheCallback(cacheData));
                            return null;
                        case "sendBloodSugar":
                            // 血糖
                            BloodsugarMeasure bloodsugarMeasure = fromGson(cacheData.getGson(), BloodsugarMeasure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendBloodSugar(Arrays.asList(bloodsugarMeasure)).enqueue(new CacheCallback(cacheData));
                            return null;
                        case "sendBloodOxygen":
                            // 血氧
                            BloodoxygenMeasure bloodoxygenMeasure = fromGson(cacheData.getGson(), BloodoxygenMeasure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendBloodOxygen(Arrays.asList(bloodoxygenMeasure)).enqueue(new CacheCallback(cacheData));
                            return null;
                        case "sendHeartState":
                            // 心脏状态
                            uploadHeartState(cacheData);
                            return null;
                        case "sendEcg12":
                            // 心脏状态
                            Heart12Measure ecg12 = fromGson(cacheData.getGson(), Heart12Measure.class);
                            ApiUtil.createHDApi(IUserHDApi.class).sendEcg12(Arrays.asList(ecg12)).enqueue(new CacheCallback(cacheData));
                            return null;
                    }
                }
            }
        }
        return cacheData;
    }

    private MeasureDataUploader(Context context) {
        this.context = context.getApplicationContext();
    }

    private volatile static MeasureDataUploader instance;

    public static MeasureDataUploader getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            synchronized (MeasureDataUploader.class) {
                if (instance == null) {
                    instance = new MeasureDataUploader(context);
                }
            }
        }
        return instance;
    }

    /**
     * 上传血压
     */
    public void uploadBP(String deviceMac , String dbp, String sbp, String heart) {
        final BpMeasure bpMeasure = new BpMeasure();
        bpMeasure.setAccountId(UserManager.getInstance().getAccountId());
        bpMeasure.setDeviceCode(deviceMac);
        bpMeasure.setDbp(dbp);
        bpMeasure.setSbp(sbp);
        bpMeasure.setHeartBeat(heart);
        ApiUtil.createHDApi(IUserHDApi.class).sendBp(Arrays.asList(bpMeasure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(bpMeasure, call.request().url().toString());
            }
        });
    }

    /**
     * 上传动脉硬化
     */
    public void uploadArt(String deviceMac , int avi, int api, int asi, int dbp, int sbp, int hr) {
        final ArtMeasure artMeasure = new ArtMeasure();
        final BpMeasure bpMeasure = new BpMeasure();
        artMeasure.setAccountId(UserManager.getInstance().getAccountId());
        artMeasure.setDeviceCode(deviceMac);
        artMeasure.setAvi(avi);
        artMeasure.setApi(api);
        artMeasure.setAsi(asi);
        artMeasure.setDbp(dbp);
        artMeasure.setSbp(sbp);
        artMeasure.setHr(hr);
        ApiUtil.createHDApi(IUserHDApi.class).sendArt(Arrays.asList(artMeasure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(artMeasure, call.request().url().toString());
            }
        });

    }

    /**
     * 上传血糖
     */
    public void uploadBloodSugar(String deviceMac , float speed, float bs, float hemoglobin, String bsTime) {
        final BloodsugarMeasure bloodsugarMeasure = new BloodsugarMeasure();
        bloodsugarMeasure.setAccountId(UserManager.getInstance().getAccountId());
        bloodsugarMeasure.setDeviceCode(deviceMac);
        bloodsugarMeasure.setBloodSpeed(speed);
        bloodsugarMeasure.setBs(bs);
        bloodsugarMeasure.setBsTime(bsTime);
        bloodsugarMeasure.setHemoglobin(hemoglobin);
        ApiUtil.createHDApi(IUserHDApi.class).sendBloodSugar(Arrays.asList(bloodsugarMeasure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(bloodsugarMeasure, call.request().url().toString());
            }
        });
    }

    /**
     * 上传血氧
     */
    public void uploadBloodOxygen(String deviceMac , int bo, int heartBeat) {
        final BloodoxygenMeasure bloodoxygenMeasure = new BloodoxygenMeasure();
        bloodoxygenMeasure.setAccountId(UserManager.getInstance().getAccountId());
        bloodoxygenMeasure.setDeviceCode(deviceMac);
        bloodoxygenMeasure.setBo(bo);
        bloodoxygenMeasure.setHeartBeat(heartBeat);
        ApiUtil.createHDApi(IUserHDApi.class).sendBloodOxygen(Arrays.asList(bloodoxygenMeasure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(bloodoxygenMeasure, call.request().url().toString());
            }
        });
    }

    /**
     * 上传心率
     */
    public void uploadHeartBpm(int hb) {
//        final HeartBpmMeasure heartBpmMeasure = new HeartBpmMeasure();
//        heartBpmMeasure.setAccountId(UserManager.getInstance().getAccountId());
//        heartBpmMeasure.setDeviceCode(deviceMac);
//        heartBpmMeasure.setHb(hb);
//        ApiUtil.createHDApi(IUserHDApi.class).sendHeartBpm(Arrays.asList(heartBpmMeasure)).enqueue(new ApiCallBack<String>() {
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                super.onFailure(call, t);
//                addCache(heartBpmMeasure, call.request().url().toString());
//            }
//        });
    }

    /**
     * 上传心脏状态
     */
    public void uploadHeartState(String deviceMac , String diaDescribes, final String healthLight, String healthLightOriginal,
                                 String heartRate, String heartRateLight, String lfhf,
                                 String nervous, String pervous, String pnn50,
                                 String sdnn, String nn50, String predicted, String stressLight , final String filePath) {
        final HeartStateMeasure heartStateMeasure = new HeartStateMeasure();
        heartStateMeasure.setAccountId(UserManager.getInstance().getAccountId());
        heartStateMeasure.setDeviceCode(deviceMac);
        heartStateMeasure.setDiaDescribes(diaDescribes);
        heartStateMeasure.setHealthLight(healthLight);
        heartStateMeasure.setHealthLightOriginal(healthLightOriginal);
        heartStateMeasure.setHeartRate(heartRate);
        heartStateMeasure.setHeartRateLight(heartRateLight);
        heartStateMeasure.setLfhf(lfhf);
        heartStateMeasure.setNervousSystemBalanceLight(nervous);
        heartStateMeasure.setPervousSystemBalanceLight(pervous);
        heartStateMeasure.setPnn50(pnn50);
        heartStateMeasure.setSdnn(sdnn);
        heartStateMeasure.setNn50(nn50);
        heartStateMeasure.setPredictedSymptoms(predicted);
        heartStateMeasure.setStressLight(stressLight);
        heartStateMeasure.setEcgImageUrl(filePath);

        ApiUtil.uploadFile(null, filePath, new ApiCallBack<String>(context) {

            @Override
            public void onApiResponse(Call<String> call, Response<String> response) {
                super.onApiResponse(call, response);
                // 删除本地图片
                new File(filePath).delete();
                // 图片上传成功后 , 上传心电数据
                String imgUrl = response.body();
                heartStateMeasure.setEcgImageUrl(imgUrl);
                Log.i("eeeecdgdddata", "上传图片成功");
                ApiUtil.createHDApi(IUserHDApi.class).sendHeartState(Arrays.asList(heartStateMeasure)).enqueue(new ApiCallBack<String>() {
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                        // 数据上传失败 , 将心电数据加入缓存
                        addCache(heartStateMeasure, call.request().url().toString());
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                Log.i("eeeecdgdddata", "上传图片失败");
                // 图片上传失败 , 将心电数据加入缓存 , EcgImageUrl字段为本地文件的路径 , URL存心电数据的URL
                addCache(heartStateMeasure ,  "api/v1/heartstate");
            }
        });
    }

    /**
     * 上传心电缓存数据的方法
     * 上传心电比较特殊 , 需要先上传图片 , 再上传数据
     *
     */
    public void uploadHeartState(final CacheData cacheData) {
        // 读取缓存数据
        final HeartStateMeasure heartStateMeasure = fromGson(cacheData.getGson(), HeartStateMeasure.class);
        // 判断是图片上传失败还是数据上传失败
        final String ecgImageUrl = heartStateMeasure.getEcgImageUrl();
        if (!TextUtils.isEmpty(ecgImageUrl)) {
            if (ecgImageUrl.contains("http")) {
                // 如果ectUrl字段存的是一个网络地址 , 说明是图片上传成功了, 直接重新提交数据即可
                ApiUtil.createHDApi(IUserHDApi.class).sendHeartState(Arrays.asList(heartStateMeasure)).enqueue(new CacheCallback(cacheData));
            } else {
                // 如果是本地文件路径 , 则先上传图片
                // 这里的Callback依旧使用CacheCallback , 只重写onApiResponse方法即可
                ApiUtil.uploadFile(null, ecgImageUrl, new CacheCallback(cacheData){
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        // 图片上传成功
                        // 删除本地图片
                        new File(ecgImageUrl).delete();
                        // 补全心电数据, 上传心电数据
                        String imgUrl = response.body();
                        heartStateMeasure.setEcgImageUrl(imgUrl);
                        ApiUtil.createHDApi(IUserHDApi.class).sendHeartState(Arrays.asList(heartStateMeasure)).enqueue(new CacheCallback(cacheData));
                    }
                });
            }
        } else {
            // 如果图片字段为空 , 应该是数据有问题 , 直接删除该条缓存
            CacheHelper.getInstance(context).deleteCache(cacheData);
        }
    }

    public void uploadEcd12(String deviceMac , HearRate12 instance) {
        final Heart12Measure measure = new Heart12Measure();
        measure.setAccountId(UserManager.getInstance().getAccountId());
        measure.setDeviceCode(deviceMac);
        measure.setEcgData(instance.getEcgData());
        measure.setHeartRate(instance.getHeartRate());
        measure.setAnalysisResults(instance.getAnalysisResults());
        measure.setPaxis(instance.getPaxis());
        measure.setAnalysisList(instance.getAnalysisList());
        measure.setPrInterval(instance.getPrInterval());
        measure.setQrsAxis(instance.getQrsAxis());
        measure.setQrsDuration(instance.getQrsDuration());
        measure.setQtc(instance.getQtc());
        measure.setQtd(instance.getQtd());
        measure.setRrInterval(instance.getRrInterval());
        measure.setRv5(instance.getRv5());
        measure.setSv1(instance.getSv1());
        measure.setTaxis(instance.getTaxis());
        ApiUtil.createHDApi(IUserHDApi.class).sendEcg12(Arrays.asList(measure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(measure, call.request().url().toString());
            }
        });
    }

    /**
     * 这里是上传红豆心电用的
     * @param heartRate
     */
    public void uploadEcd12(String deviceMac , HeartStateMeasure_Hd measure_hd , String heartRate) {
        final Heart12Measure measure = new Heart12Measure();
        measure.setHeartRate(heartRate);
        measure.setCustomAnalysis(new Gson().toJson(measure_hd));
        measure.setCustomType(1); // 这个字段是gson的类型 , 1是红豆心电的, 目前只有红豆心电用到这个接口了
        measure.setAccountId(UserManager.getInstance().getAccountId());
        measure.setDeviceCode(deviceMac);
        ApiUtil.createHDApi(IUserHDApi.class).sendEcg12(Arrays.asList(measure)).enqueue(new ApiCallBack<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                addCache(measure, call.request().url().toString());
            }
        });
    }

    /**
     * 检查缓存数据并重新上传
     */
    public void checkAndReuploadFaillData() {
        Log.i("Cache", "定时提交数据监测工作状态");
        if (isWorking || !NetworkUtils.isNetworkAvailable(context)) {
            // 如果上传数据的工具类正在工作, 那就先不去管缓存的问题
            Log.i("Cache", "工作状态或网络不可用");
            return;
        }
        Log.i("Cache", "开始检查缓存");
        // 如果是空闲状态就循环提交缓存数据
        List<CacheData> allData = CacheHelper.getInstance(context).getAllData();
        if (allData != null && allData.size() > 0) {
            Logger.i("提交缓存数据, 共 " + allData.size() + " 个");
            for (int i = 0; i < allData.size(); i++) {
                CacheData data = allData.get(i);
                CacheData cacheData = reuploadCacheData(data);
                if (cacheData != null) {
                    CacheHelper.getInstance(context).deleteCache(cacheData);
                }
            }
        } else {
            Log.i("Cache", "没有换成数据");
        }
    }

    private void addCache(Object o, String url) {
        String gsonStr = toGson(o);
        CacheData data = new CacheData();
        data.setTime(System.currentTimeMillis());
        data.setUrl(url);
        data.setGson(gsonStr);
        CacheHelper.getInstance(context).addCacheData(data);
    }

    class CacheCallback extends ApiCallBack<String> {
        CacheData cacheData;

        public CacheCallback(CacheData cacheData) {
            this.cacheData = cacheData;
        }

        @Override
        public void onApiResponse(Call<String> call, Response<String> response) {
            super.onApiResponse(call, response);
            CacheHelper.getInstance(context).deleteCache(cacheData);
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
            super.onFailure(call, t);
            CacheHelper.getInstance(context).updateReupload(cacheData);
        }
    }

    private String toGson(Object obj) {
        GsonBuilder gsonBuilder = new Gson().newBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").registerTypeAdapter(Date.class, new DateTypeAdapter());
        Gson gson = gsonBuilder.create();
        String s = gson.toJson(obj);
        return s;
    }

    private <T> T fromGson(String json, Class clazz) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json, clazz);
    }

    public void startWorking() {
        isWorking = true;
    }

    public void stopWorking() {
        isWorking = false;
    }
}
