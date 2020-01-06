package com.guider.health.common.net.net;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * retrofit的所有功能
 */

public interface RestService {
    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> params);


    //提交表单的 需要加上 FormUrlEncoded
    @FormUrlEncoded
    @POST
    Call<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @PUT
    Call<ResponseBody> put(@Url String url, @FieldMap Map<String, Object> params);


    @PUT
    Call<ResponseBody> put(@Url String url, @Body RequestBody requestBody);

    @DELETE
    Call<ResponseBody> delete(@Url String url, @QueryMap Map<String, Object> params);

    //下载是直接到内存,所以需要 @Streaming,  会往url路径上存
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url, @QueryMap Map<String, Object> params);

    //上传
    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file);

    //原始数据
    @POST
    Call<ResponseBody> postRaw(@Url String url, @Body RequestBody body);

    @PUT
    Call<String> putRaw(@Url String url, @Body RequestBody body);


    @POST
        //@FormUrlEncoded
    Call<ResponseBody> postBody(@Url String url, @QueryMap Map<String, Object> params, @Body RequestBody requestBody);


    @POST
        //@FormUrlEncoded
    Call<ResponseBody> postOnlyBody(@Url String url,  @Body RequestBody requestBody);
    @POST
        //@FormUrlEncoded
    Call<ResponseBody> postOnlyBody2(@Url String url,
                                     @Query("phone") String phone ,
                                     @Query("code") String code ,
                                     @Query("groupId") long groupId ,
                                     @Body RequestBody requestBody);
    @PUT
        //@FormUrlEncoded
    Call<ResponseBody> putWithBody(@Url String url,  @Body RequestBody requestBody);

}










