package com.elastos.chat;

import android.app.Application;
import android.content.Context;

/**
 * @author rczhang on 2018/05/10.
 */
public class MyApplication extends Application {

    private static Context context;
    public static Context getApplicationContent() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
