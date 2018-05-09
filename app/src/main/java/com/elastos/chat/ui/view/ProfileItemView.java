package com.elastos.chat.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elastos.chat.R;

/**
 * @author rczhang on 2018/05/08.
 */
public class ProfileItemView extends RelativeLayout {

    private ImageView icon;
    private TextView name;
    private TextView desc;
    private View divider;

    public ProfileItemView(Context context) {
        super(context);
        init(context);
    }

    public ProfileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        parseAttrs(attrs);
    }

    public void init(Context context) {
        setClickable(true);
        setBackgroundResource(R.drawable.bg_white_pressed_gray);

        LayoutInflater.from(context).inflate(R.layout.view_profile_item, this);
        icon = findViewById(R.id.profile_icon);
        name = findViewById(R.id.profile_name);
        desc = findViewById(R.id.profile_desc);
        divider = findViewById(R.id.profile_line);
    }

    public void parseAttrs(AttributeSet attributeSet) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ProfileItemView);

        try {
            String name = typedArray.getString(R.styleable.ProfileItemView_name);
            setName(name);

            int icon = typedArray.getResourceId(R.styleable.ProfileItemView_iconRes, 0);
            setIcon(icon);

            String desc = typedArray.getString(R.styleable.ProfileItemView_desc);
            setDesc(desc);

            boolean dividerShow = typedArray.getBoolean(R.styleable.ProfileItemView_showLine, true);
            showLine(dividerShow);
        } finally {

            typedArray.recycle();
        }
    }

    public void setIcon(int res) {
        if (res != 0) {
            icon.setImageResource(res);
            icon.setVisibility(VISIBLE);
        } else {
            icon.setVisibility(GONE);
        }
    }

    public void setName(String s) {
        if (!TextUtils.isEmpty(s)) {
            name.setText(s);
            name.setVisibility(VISIBLE);
        } else {
            name.setVisibility(GONE);
        }
    }

    public void setDesc(String s) {
        if (!TextUtils.isEmpty(s)) {
            desc.setText(s);
            desc.setVisibility(VISIBLE);
        } else {
            desc.setText("");
            desc.setVisibility(GONE);
        }
    }

    public String getDesc() {
        return desc.getText().toString();
    }

    public void showLine(boolean isShow) {
        if (isShow) {
            divider.setVisibility(VISIBLE);
        } else {
            divider.setVisibility(GONE);
        }
    }
}
