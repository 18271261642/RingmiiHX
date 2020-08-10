package com.guider.health.apilib;

import com.guider.health.apilib.model.BloodPressure;
import com.guider.health.apilib.model.BloodSugar;
import com.guider.health.apilib.model.HeartMeasureResultListBean;
import com.guider.health.apilib.model.Stethoscope;
import com.guider.health.apilib.model.TempMeasure;
import com.guider.health.apilib.model.hd.ArtMeasure;
import com.guider.health.apilib.model.hd.BloodoxygenMeasure;
import com.guider.health.apilib.model.hd.BloodsugarMeasure;
import com.guider.health.apilib.model.hd.BpMeasure;
import com.guider.health.apilib.model.hd.EcgHeartbeat;
import com.guider.health.apilib.model.hd.EcgRecorderData;
import com.guider.health.apilib.model.hd.Healthrange;
import com.guider.health.apilib.model.hd.Heart12Measure;
import com.guider.health.apilib.model.hd.HeartBpmMeasure;
import com.guider.health.apilib.model.hd.HeartStateMeasure;
import com.guider.health.apilib.model.hd.NonbsBean;
import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 个人用户健康数据
 */
public interface IUserHDApi {
    @GET("api/v2/bp/page")
    Call<List<BloodPressure>> getBP(@Query("accountId") long accountId, @Query("sTime") Date sTime, @Query("eTime") Date eTime,
                                    @Query("page") int page, @Query("row") int row);

    @GET("api/v2/bs/page")
    Call<List<BloodSugar>> getBS(@Query("accountId") long accountId, @Query("sTime") Date sTime, @Query("eTime") Date eTime,
                                 @Query("page") int page, @Query("row") int row);

    @GET("api/v2/hb/page")
    Call<List<EcgHeartbeat>> getECG(@Query("accountId") long accountId, @Query("sTime") Date sTime, @Query("eTime") Date eTime,
                                    @Query("page") int page, @Query("row") int row);

    @POST("api/v1/stethoscope")
    Call<Stethoscope> uploadStethoscope(@Body Stethoscope body);

    /**
     * 心率入库
     *
     * @param body
     * @return
     */
    @POST("api/v1/heartbeat")
    Call<String> sendHeartBpm(@Body List<HeartBpmMeasure> body);

    /**
     * 血压入库
     */
    @POST("api/v1/bloodpressure")
    Call<String> sendBp(@Body List<BpMeasure> body);

    /**
     * 动脉硬化
     */
    @POST("api/v1/arteriosclerosis")
    Call<String> sendArt(@Body List<ArtMeasure> body);

    /**
     * 血糖
     */
    @POST("api/v1/bloodsugar")
    Call<String> sendBloodSugar(@Body List<BloodsugarMeasure> body);

    /**
     * 血氧
     */
    @POST("api/v1/bloodoxygen")
    Call<String> sendBloodOxygen(@Body List<BloodoxygenMeasure> body);

    /**
     * 体温
     */
    @POST("api/v1/bodytemp")
    Call<String> sendTemp(@Body List<TempMeasure> body);

    /**
     * 心电
     */
    @POST("api/v1/heartstate")
    Call<String> sendHeartState(@Body List<HeartStateMeasure> body);


    /**
     * 12导
     */
    @POST("api/v1/ecg")
    Call<String> sendEcg12(@Body List<Heart12Measure> body);


    /**
     * 获取标准化
     *
     * @param body
     * @return
     */
    @POST("api/v1/healthrange/anlysis")
    Call<List<StandardResultBean>> getStandard(@Body List<StandardRequestBean> body);


    /**
     * 获取六导心电的测量历史数据
     *
     * @param accountId
     * @param page
     * @param row
     * @return
     */
    @GET("api/v2/heartstate/page")
    Call<List<EcgRecorderData>> getEcg(
            @Query("accountId") int accountId,
            @Query("page") int page,
            @Query("row") int row);


    /**
     * 设置血糖状态
     *
     * @param body
     * @return
     */
    @POST("api/v1/nonbs/set")
    Call<NonbsBean> setNonbs(@Body NonbsBean body);

    /**
     * 获取血糖状态
     *
     * @return
     */
    @GET("api/v1/nonbs/set")
    Call<NonbsBean> getNonbs(@Query("accountId") int accountId);

    /**
     * 获取个人设置的个人预警
     *
     * @return
     */
    @GET("api/v1/healthrange")
    Call<Healthrange> getHealthrange(@Query("accountId") int accountId);

    /**
     * 查询用户某段时间每天最新一次的血糖测量记录
     *
     * @param accountId 用户ID
     * @param sTime     开始时间
     * @param eTime     结束时间
     * @return
     */
    @GET("api/v2/bs/newest")
    Call<List<HeartMeasureResultListBean>> getBsDayLatestMeasureListByTime(
            @Query("accountId") int accountId,
            @Query("sTime") String sTime,
            @Query("eTime") String eTime);

    /**
     * 查询用户某段时间每天最新一次的血氧测量记录
     *
     * @param accountId 用户ID
     * @param sTime     开始时间
     * @param eTime     结束时间
     * @return
     */
    @GET("api/v2/bo/newest")
    Call<List<HeartMeasureResultListBean>> getBoDayLatestMeasureListByTime(
            @Query("accountId") int accountId,
            @Query("sTime") String sTime,
            @Query("eTime") String eTime);

    /**
     * 查询用户某段时间每天最新一次的心率测量记录
     *
     * @param accountId 用户ID
     * @param sTime     开始时间
     * @param eTime     结束时间
     * @return
     */
    @GET("api/v2/hb/newest")
    Call<List<HeartMeasureResultListBean>> getHbDayLatestMeasureListByTime(
            @Query("accountId") int accountId,
            @Query("sTime") String sTime,
            @Query("eTime") String eTime);
}
