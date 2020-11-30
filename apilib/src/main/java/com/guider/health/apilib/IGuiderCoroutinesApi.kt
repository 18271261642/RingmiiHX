package com.guider.health.apilib

import com.guider.health.apilib.bean.*
import com.guider.health.apilib.enums.AddressType
import com.guider.health.apilib.enums.PositionType
import com.guider.health.apilib.enums.PushNationType
import com.guider.health.apilib.model.WechatUserInfo
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @Package: com.guider.health.apilib
 * @ClassName: IGuiderConsApi
 * @Description: 盖德协程接口api
 * @Author: hjr
 * @CreateDate: 2020/10/10 9:59
 * Copyright (C), 1998-2020, GuiderTechnology
 */
interface IGuiderCoroutinesApi {

    /**
     * 上传文件
     */
    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(@Part requestBody: MultipartBody.Part?): String?

    /**线程内刷新token*/
    @POST("api/v1/refreshtoken")
    fun refreshTokenCall(@Header("token") token: String,
                         @Body refreshToken: String): Call<TokenBean>?

    /**
     * 用户名密码登录
     *
     * @param telAreaCode 国际区号
     * @param phone       手机号
     * @param passwd      密码
     * @return
     */
    @POST("api/v1/login/phonewithpasswd")
    suspend fun loginWithPassword(@Query("telAreaCode") telAreaCode: String?,
                                  @Query("phone") phone: String?,
                                  @Query("passwd") passwd: String?): TokenBean?

    /**
     * 注册
     *
     * @param data 请求体数据
     * @return
     */
    @POST("api/v2/register/phonewithpasswd")
    suspend fun register(@Body data: Any?): TokenInfo?

    /**
     * 第三方登录前检测是否绑定手机号, 返回数据带UserInfo
     * @param appId 调取三方登录的appId
     * @param openId sdk返回的openId或者userId
     * @param groupId 先固定传-1
     * @param doctorAccountId 先固定传-1
     * @return
     */
    @GET("api/v1/accountthird/verify/login")
    suspend fun lineVerifyLogin(@Query("appId") appId: String?,
                                @Query("openId") openId: String?,
                                @Query("groupId") groupId: Int,
                                @Query("doctorAccountId") doctorAccountId: Int)
            : ThirdLoginVerifyBean?

    /**
     * 第三方账号登录并绑定手机号
     * @param data
     * @return
     */
    @POST("api/v1/accountthird/phone/login")
    suspend fun lineBindLogin(@Body data: Any?): ThirdBindPhoneBean?

    /**
     * 微信登录
     * @param paramWeChatUserInfo 请求体body
     * @return
     */
    @POST("api/v2/third/login/wachat/tokeninfo")
    suspend fun weChatLoginToken(@Body paramWeChatUserInfo: WeChatInfo?): BeanOfWeChat?

    /**
     * 微信绑定手机号
     * @param phone 手机号
     * @param code 验证码
     * @param wechatAppId 微信的APPID
     * @return
     */
    @POST("api/v2/wechat/bind/phone/token")
    suspend fun weChatBindLogin(@Query("phone") phone: String?,
                                @Query("code") code: String?,
                                @Query("wechatAppId") wechatAppId: String?,
                                @Body sence: WechatUserInfo?): TokenInfo?

    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("api/v1/userinfo")
    suspend fun getUserInfo(@Query("accountId") accountID: Int): UserInfo?

    /**
     * 完善用户信息
     * @param userInfo 用户信息实体类
     * @return
     */
    @PUT("api/v1/usersimpleinfo")
    suspend fun simpUserInfo(@Body userInfo: UserInfo?): String?

    /**
     * 编辑用户完整信息（包括地址）
     * @param userInfo 用户信息
     * @return
     */
    @PUT("api/v1/userinfo")
    suspend fun editUserInfo(@Body userInfo: UserInfo?): UserInfo?

    /**
     * 验证是否绑定设备
     * @param accountId
     * @return
     */
    @GET("api/v1/opdevice/account/check")
    suspend fun checkIsBindDevice(@Query("accountId") accountId: Int): Any

    /**
     * 绑定登录账号的设备
     * @param accountId 用户ID
     * @param deviceCode 设备号
     * @return
     */
    @GET("api/v1/opdevice/account/bind")
    suspend fun bindDeviceWithAccount(@Query("accountId") accountId: Int,
                                      @Query("deviceCode") deviceCode: String): Any

    /**
     * 解绑登录账号绑定的所属设备
     * @param accountId 用户ID
     * @param deviceCode 设备号
     */
    @DELETE("api/v1/user/{accountId}/device/unbind")
    suspend fun unBindDeviceWithAccount(@Path("accountId") accountId: Int,
                                        @Query("deviceCode") deviceCode: String): Any?

    /**
     * 通过设备号添加家人
     * @param body 设备所需信息
     */
    @POST("api/v1/opdevice/joingroup")
    suspend fun memberJoinGroup(@Body body: Any): Any?

    /**
     * 验证设备是否有绑定
     * @param deviceCode 设备的code
     */
    @Deprecated("废弃")
    @GET("api/v1/opdevice/device/check")
    suspend fun verifyDeviceBind(@Query("deviceCode") deviceCode: String): String

    /**
     * 验证手机号是否存在并绑定设备、添加家人
     * @param userGroupId 群组id
     * @param deviceCode 设备code码
     * @param phone 手机号
     * @param relationShip 设备昵称
     */
    @Deprecated("废弃")
    @GET("api/v1/opdevice/phone/check")
    suspend fun devicePhoneVerify(@Query("userGroupId") userGroupId: Int,
                                  @Query("deviceCode") deviceCode: String,
                                  @Query("phone") phone: String,
                                  @Query("relationShip") relationShip: String,
                                  @Query("telAreaCode") telAreaCode: String?
    ): Any

    /**
     * 为指定群组添加用户
     * @param userGroupId 群组ID
     * @param accountId 家人帐号id
     * @param relationShip 家人关系
     */
    @POST("api/v3/group/bind")
    suspend fun groupAddMemberDevice(
            @Query("userGroupId") userGroupId: Int,
            @Query("accountId") accountId: Int,
            @Query("relationShip") relationShip: String,
    ): List<UserInfo>?

    /**
     * 获取登录用户的群下所有群员
     * @param accountId 登录ID
     */
    @GET("api/v2/group/userinfos")
    suspend fun getGroupBindMember(@Query("accountId") accountId: Int): CheckBindDeviceBean?

    /**
     * 解除群与用户绑定关系
     * @param userGroupId 群组id
     * @param accountId 用户id
     */
    @DELETE("api/v1/group/remove")
    suspend fun unBindGroupMember(@Query("userGroupId") userGroupId: Int,
                                  @Query("accountId") accountId: Int): List<UserInfo>?

    /**
     * 获取特定类型区域
     * @param areaType 地区的枚举类型
     * @param parentId 父ID
     */
    @GET("api/v1/area")
    suspend fun getAddressCode(@Query("areaType") areaType: AddressType,
                               @Query("parentId") parentId: Int
    ): List<AddressWithCodeBean>

    /**
     * 手机号注册验证
     * @param telAreaCode 国际区号
     * @param phone 手机号
     */
    @GET("api/v1/register/phone/check")
    suspend fun checkPhoneIsRegister(@Query("telAreaCode") telAreaCode: String?,
                                     @Query("phone") phone: String?): String

    /**
     * 获取手环设备电子围栏设置
     */
    @GET("api/v2/opdevice/fence")
    suspend fun getElectronicFence(@Query("accountId") accountId: Int,
                                   @Query("row") row: Int,
                                   @Query("isOpen") isOpen: Boolean
    ): Any

    /**
     * 设置设备的电子围栏信息
     */
    @POST("api/v2/opdevice/fence")
    suspend fun setElectronicFence(@Body data: Any): String

    /**
     * 设置单个电子围栏开关
     */
    @PUT("api/v1/opdevice/fence/open")
    suspend fun fenceSwitchSet(@Query("fenceId") fenceId: Int,
                               @Query("isOpen") row: Boolean): String

    /**
     * 设置单个电子围栏开关
     */
    @DELETE("api/v1/opdevice/fence")
    suspend fun deleteFence(@Query("fenceId") fenceId: Int): String

    /**
     * 运动轨迹
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/userpostion")
    suspend fun userPosition(@Query("accountId") accountId: Int,
                             @Query("page") page: Int,
                             @Query("row") row: Int,
                             @Query("start") startTime: String,
                             @Query("end") endTime: String): List<UserPositionListBean>

    /**
     * 获取手环设备定位频率设置
     */
    @GET("api/v1/opdevice/optionrate")
    suspend fun locationFrequencySet(@Query("accountId") accountId: Int?): Any

    /**
     * 设置手环设备定位频率
     */
    @GET("api/v1/opdevice/option/mode")
    suspend fun setLocationFrequency(@Query("accountId") accountId: Int,
                                     @Query("mode") mode: PositionType): Any

    /**
     * 发送咨询聊天内容
     */
    @POST("api/v1/consult/chat")
    suspend fun sendAnswerMsg(@Body body: Any): SendAnswerListBean?

    /**
     * 分页获取会话聊天内容
     * @param fromAccount 咨询发送者id
     * @param toAccount 咨询接受者id
     */
    @GET("api/v1/consult/chat/page")
    suspend fun getAnswerMsgList(@Query("fromAccount") fromAccount: Int,
                                 @Query("toAccount") toAccount: Int,
                                 @Query("page") page: Boolean,
                                 @Query("row") row: Int,
                                 @Query("currDate") currDate: String
    ): Map<String, List<AnswerListBean>>?

    /**
     * 设置用户的运动目标
     */
    @POST("api/v2/opdevice/walktarget")
    suspend fun setWalkTarget(@Query("accountId") accountId: Int,
                              @Query("walkTarget") walkTarget: Int
    ): String?

    /**
     * 获取用户设置的运动目标
     */
    @GET("api/v2/opdevice/walktarget")
    suspend fun getUserTargetStep(@Query("accountId") accountId: Int): String

    /**
     * 设置手环心率测量间隔
     * @param interval 时间间隔 分钟
     * @param open true：开启，false：关闭
     */
    @PATCH("api/v1/opdevice/hr/set")
    suspend fun setHeartRateAlarm(@Query("accountId") accountId: Int,
                                  @Query("interval") interval: Int,
                                  @Query("open") open: Boolean
    ): Any?

    /**
     * 设置手环体温测量间隔
     * @param interval 时间间隔 分钟
     * @param open true：开启，false：关闭
     */
    @PATCH("api/v1/opdevice/bt/set")
    suspend fun setBodyTempAlarm(@Query("accountId") accountId: Int,
                                 @Query("interval") interval: Int,
                                 @Query("open") open: Boolean
    ): Any?

    /**
     * 查询指定用户最新消息提醒信息
     * @param accountId 用户id
     * @param now 当前时间
     */
    @GET("api/v1/opdevice/systemmessage/now")
    suspend fun getSystemMsgLatest(@Query("accountId") accountId: Int,
                                   @Query("now") now: String): Any?

    /**
     * 发起主动寻址
     * @param accountId 用户id
     */
    @GET("api/v1/opdevice/activeposition")
    suspend fun initiateActiveAddressing(@Query("accountId") accountId: Int): String?

    /**
     * 轮训获取发起时间后主动寻址结果
     * @param accountId 用户id
     * @param now 当前时间
     */
    @GET("api/v1/opdevice/user/position")
    suspend fun proactivelyAddressingData(@Query("accountId") accountId: Int,
                                          @Query("now") now: String): Any?

    /**
     * 查询指定用户消息提醒列表
     * @param accountId 用户id
     * @param page 页数
     */
    @GET("api/v1/opdevice/systemmessage")
    suspend fun getSystemMsgList(@Query("accountId") accountId: Int,
                                 @Query("page") page: Int,
                                 @Query("row") row: Int): List<SystemMsgBean>?

    /**
     * 分页获取用户主动寻址记录
     * @param accountId 用户id
     * @param page 页数
     */
    @GET("api/v1/user/activepostion")
    suspend fun getProactivelyAddressingList(@Query("accountId") accountId: Int,
                                             @Query("page") page: Int,
                                             @Query("row") row: Int)
            : List<UserPositionListBean>?

    /**
     * 编辑家人备注
     */
    @PUT("api/v1/group/user/relationship")
    suspend fun updateRelationShipData(@Query("groupId") groupId: Int,
                                       @Query("accountId") accountId: Int,
                                       @Query("relationShip") relationShip: String
    ): Any?

    /**
     * 获取用户关联的医生列表
     */
    @GET("api/v1/user/doctors")
    suspend fun getDoctorListData(@Query("accountId") accountId: Int): List<DoctorListBean>

    /**
     * 更新用户firebase token
     */
    @GET("api/v1/sendmessage/token")
    suspend fun uploadPushToken(@Query("accountId") accountId: Int,
                                @Query("nationality") pushNationType: PushNationType,
                                @Query("type") type: String,
                                @Query("token") token: String): Any?

    /**
     * 用户注销消息推送
     */
    @DELETE("api/v1/sendmessage/user")
    suspend fun deletePushToken(@Query("accountId") accountId: Int,
                                @Query("type") type: String): Any?

    /**
     * 获取短信验证码,通过line
     */
    @GET("api/v1/phonecode/line")
    suspend fun sendLineCode(@Query("phone") phone: String): Any?

    /**
     * 验证手机号验证码是否正确
     */
    @PUT("api/v1/account/phonecode/verify")
    suspend fun verifyCode(@Query("telAreaCode") telAreaCode: String,
                           @Query("phoneNum") phoneNum: String,
                           @Query("code") code: String): String

    /**
     * 验证指定用户原密码输入是否正确
     */
    @PUT("api/v1/account/pwd/verify")
    suspend fun verifyOldPassword(@Query("pwd") pwd: String,
                                  @Query("accountId") accountId: Int): String

    /**
     * 通过原密码修改密码
     */
    @POST("api/v1/account/pwd")
    suspend fun changePassword(@Body body: Any): String?

    /**
     * 直接修改登录帐号密码
     */
    @PUT("api/v1/account/pwd/bandpwd")
    suspend fun forgotPassword(
            @Query("telAreaCode") telAreaCode: String,
            @Query("phoneNum") phoneNum: String,
            @Query("pwd") pwd: String,
            @Query("bandPwd") bandPwd: String,
    ): Any?
}