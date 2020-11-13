package com.guider.health.apilib

import com.guider.health.apilib.ApiConsts.KEY_API_HOST_HD
import com.guider.health.apilib.bean.*
import com.guider.health.apilib.enums.SortType
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface IGuiderCoroutinesHDApi {

    /**
     * 欧孚血压数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/bloodpressure")
    suspend fun getHealthBloodChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String? = null,
            @Query("endTime") endTime: String? = null,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<BloodListBeann>

    /**
     * 欧孚体温数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/bodytemp")
    suspend fun getHealthTempChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<BodyTempListBean>

    /**
     * 欧孚心率数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/heartbeat")
    suspend fun getHealthHeartChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<HeartRateListBean>

    /**
     * 血氧数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v2/bo/page")
    suspend fun getHealthBloodOxygenData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("sTime") startTime: String? = null,
            @Query("eTime") endTime: String? = null,
            @Query("sort") sort: SortType? = SortType.ASC
    ): Any?

    /**
     * 欧孚睡眠数据查询
     * @param accountId 查询用户的id
     * @param startTime 查询的日期
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/sleep")
    suspend fun getHealthSleepChartData(
            @Query("accountId") accountId: Int,
            @Query("startTime") startTime: String,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<SleepDataListBean>

    /**
     * 欧孚血糖数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/bloodsugar")
    suspend fun getHealthBloodSugarChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String? = null,
            @Query("endTime") endTime: String? = null,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<BloodSugarListBean>

    /**
     * 欧孚步数数据查询
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/op/healthdata/walkrecord")
    suspend fun getHealthSportChartData(
            @Query("accountId") accountId: Int,
            @Query("page") page: Int,
            @Query("row") row: Int,
            @Query("startTime") startTime: String,
            @Query("endTime") endTime: String,
            @Query("sort") sort: SortType? = SortType.ASC
    ): List<SportListBean>

    /**
     * 查询指定用户健康预警信息
     * @param dataType -1全部、1血压、2血糖、3血氧
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v2/healthwarn/user")
    suspend fun getAbnormalMsgList(@Query("accountId") accountId: Int,
                                   @Query("dataType") dataType: Int,
                                   @Query("page") page: Int,
                                   @Query("row") row: Int): List<AbnormalRingMsgListBean>

    /**
     * 根据用户id健康建议未读条数
     * @param accountId 用户id
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/healthadvice/readCnt")
    suspend fun getCareMsgUndo(@Query("accountId") accountId: Int): String?

    /**
     * 获取指定用户健康预警未读数量
     * @param accountId 用户id
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/healthwarn/unread/user/cnt")
    suspend fun getAbnormalMsgUndo(@Query("accountId") accountId: Int): String?

    /**
     * 查询指定用户健康预警信息
     * 数据类型
     * @param state 状态 默认暂时传-1
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/healthadvice/user")
    suspend fun getCareMsgList(@Query("userAccountId") accountId: Int,
                               @Query("page") page: Int,
                               @Query("row") row: Int,
                               @Query("state") state: Int): List<CareMsgListBean>

    /**
     * 重置用户所有健康预警未读记录为已读
     * @param accountId 用户id
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v1/healthwarn/unread/user")
    suspend fun resetAbnormalMsgReadStatus(@Query("accountId") accountId: Int): Any?

    /**
     * 根据用户id更新已读健康建议
     * @param accountId 用户id
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @POST("api/v1/healthadvice/read")
    suspend fun resetCareMsgReadStatus(@Query("accountId") accountId: Int): String

    /**
     * 欧孚指定日期计步数据及运动目标
     * @param accountId 用户id
     */
    @Headers(RetrofitUrlManager.DOMAIN_NAME_HEADER + KEY_API_HOST_HD)
    @GET("api/v2/op/healthdata/walk")
    suspend fun sportStepAndTargetStep(@Query("accountId") accountId: Int,
                                       @Query("startTime") startTime: String):
            HealthSportAndTargetStepBean?
}
