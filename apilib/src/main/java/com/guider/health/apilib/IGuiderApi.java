package com.guider.health.apilib;

import com.guider.health.apilib.model.BeanOfWecaht;
import com.guider.health.apilib.model.CardInfo;
import com.guider.health.apilib.model.Devices;
import com.guider.health.apilib.model.DoctorInfo;
import com.guider.health.apilib.model.HasWechatId;
import com.guider.health.apilib.model.HealthAdvice;
import com.guider.health.apilib.model.IsFocusWechat;
import com.guider.health.apilib.model.ParamThirdUserAccount;
import com.guider.health.apilib.model.SimpleUserInfo;
import com.guider.health.apilib.model.TokenInfo;
import com.guider.health.apilib.model.UserInfo;
import com.guider.health.apilib.model.WechatInfo;
import com.guider.health.apilib.model.WechatUserInfo;
import com.guider.health.apilib.model.doctor.City;
import com.guider.health.apilib.model.doctor.JiGou;
import com.guider.health.apilib.model.doctor.MinZu;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IGuiderApi {
    @GET("api/v1/healthadvice/page")
    Call<List<HealthAdvice>> getHealthAdvice(@Query("accountId") long accountId, @Query("page") int page, @Query("row") int row);

    @Multipart
    @POST("upload/file")
    Call<String> uploadFile(@Part MultipartBody.Part requestBody);

    @POST("api/v2/wechat/scan/login/userinfo")
    Call<BeanOfWecaht> getWechatInfo(@Query("doctorAccountId") long doctorAccountId,
                                     @Query("deviceCode") String deviceCode,
                                     @Query("wechatAppId") String wechatAppId,
                                     @Query("sence") String sence);

    @POST("api/v2/wechat/scan/login/tokeninfo")
    Call<BeanOfWecaht> getWechatTocken(@Query("doctorAccountId") long doctorAccountId,
                                       @Query("deviceCode") String deviceCode,
                                       @Query("wechatAppId") String wechatAppId,
                                       @Query("sence") String sence);

    /**
     * 获取当前PAD所属机构的AppId
     * @param mac
     * @return
     */
    @GET("api/v1/device/{mac}/wechatappid")
    Call<String> getWechatAppId(@Path("mac") String mac);

    /**
     * 微信登录
     *
     *                          JSONObject jsonObject = new JSONObject();
     *                         //HashMap<String, Object> request = new HashMap<>();
     *                         jsonObject.put("appId", "wx1971c8a28a09ad4b");
     *                         jsonObject.put("headimgurl", data.get("iconurl"));
     *                         jsonObject.put("nickname", data.get("name"));
     *                         jsonObject.put("openid", data.get("openid"));
     *
     *                         int gender = data.get("gender").equals("男") ? 0 : 1;
     *                         jsonObject.put("sex", gender);
     *                         jsonObject.put("unionid", data.get("unionid"));
     */
    @POST("api/v2/third/login/wachat/tokeninfo")
    Call<BeanOfWecaht> wechatLoginToken(@Body() WechatInfo paramWechatUserInfo);


    /**
     * 在需要关注微信公众号的时候轮询监测用户是否已经关注了公众号
     * @param sence
     * @return
     */
    @POST("api/v2/wechat/scan/group/wechatBind")
    Call<IsFocusWechat> checkIsFocus(@Query("sence") String sence);

    /**
     * 微信绑定群组
     *
     * @param sence
     * @param deviceCode
     * @return
     */
    @POST("api/v2/wechat/scan/group/userinfo")
    Call<BeanOfWecaht> getWechatGroup(@Query("sence") String sence,
                                      @Query("deviceCode") String deviceCode,
                                      @Query("wechatAppId") String wechatAppId
    );

    /**
     * 微信绑定手机号
     *
     * @param phone
     * @param code
     * @param sence
     * @return
     */
    @POST("api/v2/wechat/bind/phone/userinfo")
    Call<UserInfo> wechatBindPhone(@Query("phone") String phone,
                                   @Query("code") String code,
                                   @Query("deviceCode") String deviceCode,
                                   @Query("doctorAccountId") String doctorAccountId,
                                   @Query("wechatAppId") String wechatAppId,
                                   @Body() WechatUserInfo sence);

    /**
     * 身份证登录
     *
     * @return
     */
    @POST("api/v2/login/cardid/userinfo")
    Call<BeanOfWecaht> cardLogin(@Body() CardInfo cardInfo);

    /**
     * 身份证绑定手机号
     *
     * @param phone
     * @param code
     * @return
     */
    @POST("api/v2/cardid/bind/phone/userinfo")
    Call<UserInfo> cardBindPhone(@Query("phone") String phone,
                                 @Query("code") String code,
                                 @Query("cardId") String cardId,
                                 @Query("deviceCode") String deviceCode,
                                 @Query("doctorAccountId") String doctorAccountId,
                                 @Body() CardInfo cardInfo);

    /**
     * 微信绑定手机号
     *
     * @param phone
     * @param code
     * @param sence
     * @return
     */
    @POST("api/v2/wechat/bind/phone/token")
    Call<TokenInfo> wechatRegister(@Query("phone") String phone,
                                   @Query("code") String code,
                                   @Query("wechatAppId") String wechatAppId,
                                   @Body() WechatUserInfo sence);

    /**
     * 完善用户信息
     *
     * @param userInfo
     * @return
     */
    @PUT("api/v1/usersimpleinfo")
    Call<String> simpUserInfo(@Body() UserInfo userInfo);
    @PUT("api/v1/usersimpleinfo")
    Call<SimpleUserInfo> editSimpUserInfo(@Body() SimpleUserInfo userInfo);
    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("api/v1/userinfo")
    Call<UserInfo> getUserInfo(@Query("accountId") String accountID);

    /**
     * 获取用户下的医生列表
     *
     * @param accountId
     * @return
     */
    @GET("api/v1/user/doctors")
    Call<List<DoctorInfo>> getDoctorList(@Query("accountId") int accountId);

    /**
     * 获取医生信息
     */
    @GET("api/v1/doctorinfo")
    Call<DoctorInfo> getDoctorInfo(@Query("accountId") int accountId);

    @GET("api/v1/healthdevice/type/bind")
    Call<List<Devices>> getDeviceList(@Query("mac") String mac);

    @GET("api/v1/wechat/subscribe")
    Call<HasWechatId> checkUserHasWechatId(@Query("appId") String appId, @Query("accountId") String accountId);


    /**
     * 绑定手机号获取验证码 4位
     * @return
     */
    @GET("api/v1/bind/sendcode")
    Call<String> getPhoneCode4(@Query("phone") String phone);

    /**
     * 登录获取验证码 6位
     * @return
     */
    @GET("api/v1/phonecode")
    Call<String> getPhoneCode6(@Query("phone") String phone);


    /*------------------------------------医生端------------------------------------*/
    /**
     * 获取民族列表
     * @return
     */
    @GET("api/v1/consts?parentSortId=1")
    Call<List<MinZu>> getMinZuList();

    /**
     * 获取医生所管理的用户列表
     */
    @GET("api/v1/doctor/{accountId}/user")
    Call<List<UserInfo>> getUserList(@Path("accountId") int accountId);

    /**
     * 获取医生所属的机构
     */
    @GET("api/v1/doctor/{accountId}/company")
    Call<List<JiGou>> getJiGou(@Path("accountId") int accountId);

    /**
     * 获取所有省市区
     */
    @GET("api/v1/allarea")
    Call<List<City>> getCitys(@Query("page") int page, @Query("row") int row);

    /**
     * 解除与该用户的关系
     * @return
     */
    @DELETE("api/v1/doctor/{accountId}/user")
    Call<String> deleteUser(@Path("accountId") int accountId, @Query("userAccountId") int userAccountId);

    /**
     *
     * @param appId
     * @param openId
     * @return
     */
    @GET("api/v1/accountthird/verify/login")
    Call<BeanOfWecaht> verifyThirdAccount(@Query("appId") String appId, @Query("openId") String openId,
                                          @Query("groupId") long groupId, @Query("doctorAccountId") long doctorAccountId);
    @POST("api/v1/accountthird/phone/login")
    Call<BeanOfWecaht> bindPhoneAndLogin(@Body ParamThirdUserAccount param);

    @GET("api/v1/accountthird/type")
    Call<List<String>> getUserBindThidAccount(@Query("accountId ") long appId);

    // 根据accountId查询用户是否关注公众号
    @GET("api/v1/wechat/subscribe")
    Call<String> getUserBindWxPublic(@Query("appId") String appId, @Query("accountId") long accountId);

    @GET("api/v1/wechat/qr")
    Call<String> createWXQr(@Query("appId") String appId, @Query("expireSeconds") long expireSeconds, @Query("sceneStr") String sceneStr);
    //修改手环登录密码
    @PUT("api/v1/bandpwd")
    Call<String> backupOldPwd(@Query("accountId") int accountId , @Query("bandPwd") String bandPwd);
}