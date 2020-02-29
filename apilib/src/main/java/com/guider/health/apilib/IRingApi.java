package com.guider.health.apilib;

import com.guider.health.apilib.model.AreaCode;
import com.guider.health.apilib.model.BeanOfWecaht;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRingApi {
    @GET("areaCode.json")
    Call<List<AreaCode>> verifyThirdAccount();
}
