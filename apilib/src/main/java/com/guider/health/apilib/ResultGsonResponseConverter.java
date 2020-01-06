package com.guider.health.apilib;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ResultGsonResponseConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    ResultGsonResponseConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String body = value.string();
            JSONObject json = new JSONObject(body);
            int code = json.optInt("code");
            if (code < 0)
                throw new RuntimeException(json.optString("msg"));

            String str = json.optString("data");
            if (str != null) {
                if (!str.trim().startsWith("{") && !str.trim().startsWith("[")) {
                    return (T) str;
                }
            }
            return adapter.fromJson(str);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            value.close();
        }
    }
}
