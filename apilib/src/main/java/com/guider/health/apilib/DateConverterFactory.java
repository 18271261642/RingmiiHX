package com.guider.health.apilib;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class DateConverterFactory extends Converter.Factory {
    private final Gson gson;

    public static DateConverterFactory create() {
        return create(new Gson());
    }

    public static DateConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new DateConverterFactory(gson);
    }

    private DateConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == Date.class)
            return new DateToStringConverter();
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    public class DateToStringConverter implements Converter<Date, String> {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'dd:mm:ss'Z'");
        @Override
        public String convert(Date value) throws IOException {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.format(value);
        }
    }

    public class DateFromStringConverter implements Converter<String, Date> {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'dd:mm:ss'Z'");

        @Override
        public Date convert(String value) throws IOException {
            try {
                format.setTimeZone(TimeZone.getDefault());
                return format.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("日期反序列化失败");
            }
        }
    }
}
