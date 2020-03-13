package com.guider.health.common.core;

import java.util.Locale;

public class LocaleUtil {
    public static boolean isZh() {
        return Locale.getDefault().getLanguage().startsWith("zh");
    }

    public static String getLanguageStr() {
        String strLanguage = Locale.getDefault().getLanguage();
        if (strLanguage.startsWith("zh_tw") || strLanguage.startsWith("zh_hk"))
            return "tw";
        else if (strLanguage.startsWith("zh"))
            return "zh";
        return "en";
    }
}
