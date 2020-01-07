package com.guider.health.adapter;

import android.view.View;
import android.widget.TextView;

public class TooLazyToWrite {

    public static void setTextView(View view , int id, String text) {
        View viewById = view.findViewById(id);
        if (viewById instanceof TextView) {
            ((TextView) viewById).setText(text);
        }
    }
}
