package com.guider.health.apilib

import com.guider.health.apilib.bean.*
import com.guider.health.apilib.model.WechatUserInfo
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IGuiderApi {
    @Multipart
    @POST("upload/file")
    fun uploadFile(@Part requestBody: MultipartBody.Part?): Call<String?>?

    /**
     * 用户名密码登录
     *
     * @param telAreaCode 国际区号
     * @param phone       手机号
     * @param passwd      密码
     * @return
     */
    @POST("api/v1/login/phonewithpasswd")
    fun loginWithPassword(@Query("telAreaCode") telAreaCode: String?,
                          @Query("phone") phone: String?,
                          @Query("passwd") passwd: String?): Call<TokenInfo?>?

    /**
     * 注册
     *
     * @param data 请求体数据
     * @return
     */
    @POST("api/v2/register/phonewithpasswd")
    fun register(@Body data: Any?): Call<TokenInfo?>?

    /**
     * 第三方登录前检测是否绑定手机号, 返回数据带UserInfo
     * @param appId 调取三方登录的appId
     * @param openId sdk返回的openId或者userId
     * @param groupId 先固定传-1
     * @param doctorAccountId 先固定传-1
     * @return
     */
    @GET("api/v1/accountthird/verify/login")
    fun lineVerifyLogin(@Query("appId") appId: String?,
                        @Query("openId") openId: String?,
                        @Query("groupId") groupId: Int,
                        @Query("doctorAccountId") doctorAccountId: Int): Call<ThirdLoginVerifyBean?>?

    /**
     * 第三方账号登录并绑定手机号
     * @param data
     * @return
     */
    @POST("api/v1/accountthird/phone/login")
    fun lineBindLogin(@Body data: Any?): Call<ThirdBindPhoneBean?>?

    /**
     * 微信登录
     * @param paramWeChatUserInfo 请求体body
     * @return
     */
    @POST("api/v2/third/login/wachat/tokeninfo")
    fun weChatLoginToken(@Body paramWeChatUserInfo: WeChatInfo?): Call<BeanOfWeChat?>?

    /**
     * 微信绑定手机号
     * @param phone 手机号
     * @param code 验证码
     * @param wechatAppId 微信的APPID
     * @param sence 实体类
     * @return
     */
    @POST("api/v2/wechat/bind/phone/token")
    fun weChatBindLogin(@Query("phone") phone: String?,
                        @Query("code") code: String?,
                        @Query("wechatAppId") wechatAppId: String?,
                        @Body sence: WechatUserInfo?): Call<TokenInfo?>?

    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("api/v1/userinfo")
    fun getUserInfo(@Query("accountId") accountID: Int): Call<UserInfo?>?

    /**
     * 完善用户信息
     * @param userInfo 用户信息实体类
     * @return
     */
    @PUT("api/v1/usersimpleinfo")
    fun simpUserInfo(@Body userInfo: UserInfo?): Call<String?>?

    /**
     * 编辑用户完整信息（包括地址）
     * @param userInfo 用户信息
     * @return
     */
    @PUT("api/v1/userinfo")
    fun editUserInfo(@Body userInfo: UserInfo?): Call<UserInfo?>

    /**
     * 验证是否绑定设备
     * @param accountId
     * @return
     */
    @GET("api/v1/opdevice/account/check")
    fun checkIsBindDevice(@Query("accountId") accountId: Int): Call<Any>

    /**
     * 绑定登录账号的设备
     * @param accountId 用户ID
     * @param deviceCode 设备号
     * @return
     */
    @GET("api/v1/opdevice/account/bind")
    fun bindDeviceWithAccount(@Query("accountId") accountId: Int,
                              @Query("deviceCode") deviceCode: String):
            Call<Any?>

    /**
     * 解绑登录账号绑定的所属设备
     * @param accountId 用户ID
     * @param deviceCode 设备号
     */
    @DELETE("api/v1/user/{accountId}/device/unbind")
    fun unBindDeviceWithAccount(@Query("accountId") accountId: Int,
                                @Query("deviceCode") deviceCode: String):Call<Any>

    /**
     * 验证设备是否有绑定
     * @param deviceCode 设备的code
     */
    @GET("api/v1/opdevice/device/check")
    fun verifyDeviceBind(@Query("deviceCode") deviceCode: String): Call<String>

    /**
     * 验证手机号是否存在并绑定设备、添加家人
     * @param userGroupId 群组id
     * @param deviceCode 设备code码
     * @param phone 手机号
     * @param relationShip 设备昵称
     */
    @GET("api/v1/opdevice/phone/check")
    fun devicePhoneVerify(@Query("userGroupId") userGroupId: Int,
                          @Query("deviceCode") deviceCode: String,
                          @Query("phone") phone: String,
                          @Query("relationShip") relationShip: String,
                          @Query("telAreaCode") telAreaCode: String?
    ): Call<Any?>

    /**
     * 为指定群组添加用户
     * @param userGroupId 群组ID
     * @param accountId 家人帐号id
     * @param relationShip 家人关系
     */
    @POST("api/v3/group/bind")
    fun groupAddMemberDevice(
            @Query("userGroupId") userGroupId: Int,
            @Query("accountId") accountId: Int,
            @Query("relationShip") relationShip: String,
    ): Call<List<UserInfo>?>

    /**
     * 获取登录用户的群下所有群员
     * @param accountId 登录ID
     */
    @GET("api/v2/group/userinfos")
    fun getGroupBindMember(@Query("accountId") accountId: Int): Call<CheckBindDeviceBean?>

    /**
     * 解除群与用户绑定关系
     * @param userGroupId 群组id
     * @param accountId 用户id
     */
    @DELETE("api/v1/group/remove")
    fun unBindGroupMember(@Query("userGroupId") userGroupId: Int,
                          @Query("accountId") accountId: Int): Call<List<UserInfo>?>

    /**
     * 获取特定类型区域
     * @param areaType 地区的枚举类型
     * @param parentId 父ID
     */
    @GET("api/v1/area")
    fun getAddressCode(@Query("areaType") areaType: AddressType,
                       @Query("parentId") parentId: Int
    ): Call<List<AddressWithCodeBean>>

    /**
     * 手机号注册验证
     * @param telAreaCode 国际区号
     * @param phone 手机号
     */
    @GET("api/v1/register/phone/check")
    fun checkPhoneIsRegister(@Query("telAreaCode") telAreaCode: String?,
                             @Query("phone") phone: String?): Call<String>

    /**
     * 查询设备的电子围栏信息
     * @param deviceCode 设备码
     */
    @GET("api/v1/opdevice/fence")
    fun getElectronicFence(@Query("deviceCode") deviceCode: String?):
            Call<Any>

    /**
     * 设置设备的电子围栏信息
     * @param data 设备码+地址围栏信息的latlng列表
     */
    @POST("api/v1/opdevice/fence")
    fun setElectronicFence(@Body data: Any): Call<String>

    /**
     * 运动轨迹
     * @param accountId 查询用户的id
     * @param page 页数，查全部的时候为-1
     * @param row 当查全部的时候row不生效
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GET("api/v1/userpostion")
    fun userPosition( @Query("accountId") accountId: Int,
                      @Query("page") page: Int,
                      @Query("row") row: Int,
                      @Query("startTime") startTime: String,
                      @Query("endTime") endTime: String):Call<List<UserPositionListBean>>
}