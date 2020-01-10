package com.guider.health.apilib;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatAsString = format.format(src);
        return new JsonPrimitive(dateFormatAsString);
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            DateTime dateTime = new DateTime(format.parse(json.getAsString()));
            Date date = dateTime.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC+08"))).toDate();
            return date;
//            return format.parse(json.getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("日期反序列化失败");
        }

    }
}