package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.ui.view.AppBar;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/11.
 */
public class PublishMessageActivity extends BaseActivity {

    @BindView(R.id.appbar)
    AppBar appBar;
    @BindView(R.id.input)
    EditText input;

    public static void start(Context context) {
        Intent intent = new Intent(context, PublishMessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_publish_message;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        appBar.setRightTextEnable(false);
        appBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper.put("public_message", getInput());
                String msg = SharedPreferencesHelper.get("public_message");
                try {
                    List<FriendInfo> friendInfos = Carrier.getInstance().getFriends();
                    if (friendInfos.size() > 0) {
                        for (FriendInfo fi : friendInfos) {
                            Log.v("", fi.getUserId());
                            if (fi.getConnectionStatus() == ConnectionStatus.Connected) {
                                Carrier.getInstance().sendFriendMessage(fi.getUserId(), msg);
                            }
                        }
                    }
                    ToastUtils.shortT("发布消息成功");
                    finish();
                } catch (ElastosException e) {
                    e.printStackTrace();
                    ToastUtils.shortT("发布消息失败");
                }
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                appBar.setRightTextEnable(!TextUtils.isEmpty(getInput()));
            }
        });
    }

    private String getInput() {
        return input.getText().toString().trim();
    }
}
