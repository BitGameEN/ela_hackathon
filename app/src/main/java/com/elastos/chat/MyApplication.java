package com.elastos.chat;

import android.app.Application;
import android.content.Context;

import com.elastos.chat.util.AndroidUtilities;

/**
 * @author rczhang on 2018/05/10.
 */
public class MyApplication extends Application {

    private static Context context;
    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AndroidUtilities.init(context);
    }
}
