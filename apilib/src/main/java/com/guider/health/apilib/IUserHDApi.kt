package com.guider.health.apilib

import com.guider.health.apilib.bean.BloodListBeann
import com.guider.health.apilib.bean.BloodSugarListBean
import com.guider.health.apilib.bean.HeartListBean
import com.guider.health.apilib.bean.SportListBean
import com.guider.health.apilib.model.TempMeasure
import com.guider.health.apilib.model.hd.*
import com.guider.health.apilib.model.hd.standard.StandardRequestBean
import com.guider.health.apilib.model.hd.standard.StandardResultBean
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 个人用户健康数据
 */
interface IUserHDApi {

    /**
     * 欧孚血压数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/bloodpressure/page")
    fun getHealthBloodChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
            ):Call<List<BloodListBeann>>
    /**
     * 欧孚体温数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/bodytemp/page")
    fun getHealthTempChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ):Call<List<Any>>

    /**
     * 欧孚心率数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/heartbeat/page")
    fun getHealthHeartChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ):Call<List<HeartListBean>>

    /**
     * 欧孚睡眠数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/op/healthdata/sleep")
    fun getHealthSleepChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ):Call<List<Any>>

    /**
     * 欧孚血糖数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/bloodsugar/page")
    fun getHealthBloodSugarChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ):Call<List<BloodSugarListBean>>

    /**
     * 欧孚步数数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/walkrecord/page")
    fun getHealthSportChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ):Call<List<SportListBean>>

    //老版接口
    /**
     * 心率入库
     *
     * @param body
     * @return
     */
    @POST("api/v1/heartbeat")
    fun sendHeartBpm(@Body body: List<HeartBpmMeasure?>?): Call<String?>?

    /**
     * 血压入库
     */
    @POST("api/v1/bloodpressure")
    fun sendBp(@Body body: List<BpMeasure?>?): Call<String?>?

    /**
     * 动脉硬化
     */
    @POST("api/v1/arteriosclerosis")
    fun sendArt(@Body body: List<ArtMeasure?>?): Call<String?>?

    /**
     * 血糖
     */
    @POST("api/v1/bloodsugar")
    fun sendBloodSugar(@Body body: List<BloodsugarMeasure?>?): Call<String?>?

    /**
     * 血氧
     */
    @POST("api/v1/bloodoxygen")
    fun sendBloodOxygen(@Body body: List<BloodoxygenMeasure?>?): Call<String?>?

    /**
     * 体温
     */
    @POST("api/v1/bodytemp")
    fun sendTemp(@Body body: List<TempMeasure?>?): Call<String?>?

    /**
     * 心电
     */
    @POST("api/v1/heartstate")
    fun sendHeartState(@Body body: List<HeartStateMeasure?>?): Call<String?>?

    /**
     * 12导
     */
    @POST("api/v1/ecg")
    fun sendEcg12(@Body body: List<Heart12Measure?>?): Call<String?>?

    /**
     * 获取标准化
     *
     * @param body
     * @return
     */
    @POST("api/v1/healthrange/anlysis")
    fun getStandard(@Body body: List<StandardRequestBean?>?): Call<List<StandardResultBean?>?>?
}