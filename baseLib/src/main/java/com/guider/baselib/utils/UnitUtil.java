package com.guider.baselib.utils;

import android.content.Context;

import com.guider.baselib.device.IUnit;
import com.guider.baselib.device.UnitCN;
import com.guider.baselib.device.UnitImpl;
import com.guider.baselib.device.UnitTW;

import java.util.Locale;

public class UnitUtil {
    public static IUnit getIUnit(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getCountry().toLowerCase();
        if (language.contains("cn"))
            return new UnitImpl(new UnitCN());
        else
            return new UnitImpl(new UnitTW());
    }
}
