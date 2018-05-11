package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.AppBar;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class NicknameSetActivity extends BaseActivity {

    @BindView(R.id.appbar)
    AppBar appBar;
    @BindView(R.id.input)
    EditText input;

    public String nickname;

    public static Intent getStartIntent(Context context, String nickname) {
        Intent intent = new Intent(context, NicknameSetActivity.class);
        intent.putExtra(Extra.NICKNAME, nickname);
        return intent;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_nickname_set;
    }

    @Override
    protected void initVariables() {
        nickname = getIntent().getStringExtra(Extra.NICKNAME);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        appBar.setRightTextEnable(false);
        input.setText(nickname);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newName = s.toString().trim();
                if (newName.equals(nickname) || TextUtils.isEmpty(newName)) {
                    appBar.setRightTextEnable(false);
                } else {
                    appBar.setRightTextEnable(true);
                }
            }
        });

        appBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserInfo selfInfo = Carrier.getInstance().getSelfInfo();
                    String newName = input.getText().toString().trim();
                    selfInfo.setName(newName);
                    Carrier.getInstance().setSelfInfo(selfInfo);
                    Intent intent = new Intent();
                    intent.putExtra(Extra.NICKNAME, newName);
                    setResult(RESULT_OK, intent);
                    finish();
                    ToastUtils.shortT("设置个人信息成功");
                } catch (ElastosException e) {
                    e.printStackTrace();
                    ToastUtils.shortT("设置个人信息失败");
                }
            }
        });
    }
}
