package com.chebuso.chargetimer.helpers;

import android.support.annotation.NonNull;

public class StringHelper {
    @NonNull
    public static String emptyIfNull(String value){
        return value == null ? "" : value;
    }

    public static boolean isNullOrEmpty(String value){
        return value == null || "".equals(value);
    }
}
