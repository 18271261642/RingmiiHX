package com.guider.health.common.utils;

import android.content.Context;

import com.guider.health.common.device.IUnit;
import com.guider.health.common.device.UnitCN;
import com.guider.health.common.device.UnitImpl;
import com.guider.health.common.device.UnitTW;

import java.util.Locale;

public class UnitUtil {
    public static IUnit getIUnit(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getCountry().toLowerCase();
        if (language.contains("tw"))
            return new UnitImpl(new UnitCN());
        else
            return new UnitImpl(new UnitTW());
    }
}
