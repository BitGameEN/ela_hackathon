package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.AppBar;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class UserIdActivity extends BaseActivity {

    @BindView(R.id.appbar)
    AppBar appBar;
    @BindView(R.id.user_id)
    TextView userIdView;

    public String userId;

    public static Intent getStartIntent(Context context, String userId) {
        Intent intent = new Intent(context, UserIdActivity.class);
        intent.putExtra(Extra.USER_ID, userId);
        return intent;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_user_id;
    }

    @Override
    protected void initVariables() {
        userId = getIntent().getStringExtra(Extra.USER_ID);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        appBar.setRightTextEnable(false);
        userIdView.setText(userId);
    }
}
