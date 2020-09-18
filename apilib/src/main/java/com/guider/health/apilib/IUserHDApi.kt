package com.guider.health.apilib

import com.guider.health.apilib.bean.*
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
    @GET("api/v1/op/healthdata/bloodpressure")
    fun getHealthBloodChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<BloodListBeann>>

    /**
     * 欧孚体温数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/op/healthdata/bodytemp")
    fun getHealthTempChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<BodyTempListBean>>

    /**
     * 欧孚心率数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/op/healthdata/heartbeat")
    fun getHealthHeartChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<HeartRateListBean>>

    /**
     * 血氧数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v2/bo/page")
    fun getHealthBloodOxygenData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String,
            @Query("eTime") endTime: String
    ): Call<Any>

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
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<SleepDataListBean>>

    /**
     * 欧孚血糖数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/op/healthdata/bloodsugar")
    fun getHealthBloodSugarChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<BloodSugarListBean>>

    /**
     * 欧孚步数数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/op/healthdata/walkrecord")
    fun getHealthSportChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String
    ): Call<List<SportListBean>>

    /**
     * 查询指定用户健康预警信息
     * @param dataType -1全部、1血压、2血糖、3血氧
     */
    @GET("api/v2/healthwarn/user")
    fun getAbnormalMsgList(@Query("accountId") accountId: Int,
                           @Query("dataType") dataType: Int,
                           @Query("page") page: Int,
                           @Query("row") row: Int): Call<List<AbnormalRingMsgListBean>>
    /**
     * 根据用户id健康建议未读条数
     * @param accountId 用户id
     */
    @GET("api/v1/healthadvice/readCnt")
    fun getCareMsgUndo(@Query("accountId") accountId: Int): Call<String>

    /**
     * 获取指定用户健康预警未读数量
     * @param accountId 用户id
     */
    @GET("api/v1/healthwarn/unread/user/cnt")
    fun getAbnormalMsgUndo(@Query("accountId") accountId: Int): Call<String>

    /**
     * 查询指定用户健康预警信息
     * 数据类型
     * @param state 状态 默认暂时传-1
     */
    @GET("api/v1/healthadvice/user")
    fun getCareMsgList(@Query("userAccountId") accountId: Int,
                       @Query("page") page: Int,
                       @Query("row") row: Int,
                       @Query("state") state: Int): Call<List<CareMsgListBean>>
    /**
     * 重置用户所有健康预警未读记录为已读
     * @param accountId 用户id
     */
    @GET("api/v1/healthwarn/unread/user")
    fun resetAbnormalMsgReadStatus(@Query("accountId") accountId: Int): Call<Any>

    /**
     * 根据用户id更新已读健康建议
     * @param accountId 用户id
     */
    @POST("api/v1/healthadvice/read")
    fun resetCareMsgReadStatus(@Query("accountId") accountId: Int): Call<String>


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