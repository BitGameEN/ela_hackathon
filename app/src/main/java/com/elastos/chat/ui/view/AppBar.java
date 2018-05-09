package com.elastos.chat.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elastos.chat.R;

/**
 * @author rczhang on 2018/05/08.
 */
public class AppBar extends RelativeLayout {

    private View back;
    private TextView title;

    public AppBar(Context context) {
        super(context);
        init(context);
    }

    public AppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        parseAttrs(attrs);
    }

    public void init(final Context context) {
        setClickable(true);
        LayoutInflater.from(context).inflate(R.layout.view_appbar, this);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });
    }

    private void parseAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AppBar);
        try {
            String title = a.getString(R.styleable.AppBar_title);
            setTitle(title);
        } finally {
            a.recycle();
        }
    }

    public void setTitle(String titleString) {
        if (titleString != null) {
            title.setText(titleString);
        }
    }
}
