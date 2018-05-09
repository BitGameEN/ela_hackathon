package com.elastos.chat.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elastos.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author rczhang on 2018/05/08.
 */
public class AppBar extends RelativeLayout {

    @BindView(R.id.back) View back;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.right_text) TextView rightText;

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
        ButterKnife.bind(this, this);
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

            String rightTextContent = a.getString(R.styleable.AppBar_rightText);
            setRightText(rightTextContent);
        } finally {
            a.recycle();
        }
    }

    public void setTitle(String titleString) {
        if (titleString != null) {
            title.setText(titleString);
        }
    }

    public void setRightText(String content) {
        if (TextUtils.isEmpty(content)) {
            rightText.setText("");
            rightText.setVisibility(View.GONE);
        } else {
            rightText.setText(content);
            rightText.setVisibility(View.VISIBLE);
        }
    }

    public void setRightTextEnable(boolean isEnable) {
        rightText.setEnabled(isEnable);
    }

    public void setRightClickListener(OnClickListener l) {
        rightText.setOnClickListener(l);
    }
}
