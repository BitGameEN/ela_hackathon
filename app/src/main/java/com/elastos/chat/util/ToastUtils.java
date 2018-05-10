package com.elastos.chat.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.elastos.chat.MyApplication;

public class ToastUtils {
    private static Toast toast;

    public static void shortT(@StringRes int resId) {
        shortT(MyApplication.getContext().getString(resId));
    }

    public static void shortT(String msg) {
        doToastShow(msg, Toast.LENGTH_SHORT);
    }

    public static void longT(@StringRes int resId) {
        longT(MyApplication.getContext().getString(resId));
    }

    public static void longT(String msg) {
        doToastShow(msg, Toast.LENGTH_LONG);
    }

    private static void doToastShow(final String msg, final int duration) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(MyApplication.getContext(), msg, duration);
                    toast.show();
                } else {
                    toast.setText(msg);
                    toast.setDuration(duration);
                    toast.show();
                }
            }
        });
    }
}
