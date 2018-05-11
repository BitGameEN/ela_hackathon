package com.elastos.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.StringRes;

import com.elastos.chat.MyApplication;
import com.elastos.chat.R;
import com.elastos.chat.util.ToastUtils;

/**
 * @author rczhang on 2018/05/11.
 */
public class CopyHelper {

    public static void copyText(String text) {
        copyText(text, R.string.already_copy);
    }

    public static void copyText(String text, @StringRes int toast) {
        ClipboardManager clipboardManager = (ClipboardManager) MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("ela", text));
        ToastUtils.shortT(toast);
    }
}
