package com.guider.health.apilib;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import android.util.Log;

import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        String str = gson.toJson(value);
        Log.i("gsonss", str);
        return RequestBody.create(MEDIA_TYPE, str);
    }
}
