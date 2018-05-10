package com.elastos.chat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author rczhang on 2018/05/10.
 */
public class SharedPreferencesHelper {

    public static final String NAME = "ela_hackathon";

    public static void put(String strKey, String strValue) {
        SharedPreferences sharedPreferences = MyApplication.getContext().
                getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(strKey, strValue);
        editor.apply();
    }

    public static String get(String strKey) {
        SharedPreferences sharedPreferences = MyApplication.getContext().
                getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(strKey, "");
    }
}
