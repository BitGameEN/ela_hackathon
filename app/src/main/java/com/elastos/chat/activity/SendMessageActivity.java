package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.common.Extra;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import butterknife.BindView;

/**
 * Created by fuwei on 2018/5/9.
 */

public class SendMessageActivity extends BaseActivity {

    @BindView(R.id.message_et_my_send_message) EditText edSendMessage;
    @BindView(R.id.message_but_send_message) Button butSendMyMessage;
    @BindView(R.id.message_but_recommend) Button butRecommend;
    @BindView(R.id.reward) Button reward;
    public String FriendID;

    public static void start(Context context, String friendid) {
        Intent intent = new Intent(context, SendMessageActivity.class);
        intent.putExtra(Extra.SEND_MESSAGE_FRIENDID, friendid);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables() {
        FriendID = getIntent().getStringExtra(Extra.SEND_MESSAGE_FRIENDID);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_send_message;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpectDialog();
            }
        });

        butSendMyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String toSendMsg = edSendMessage.getText().toString().trim();
                    if (!toSendMsg.isEmpty()) {
                        Log.v("", FriendID + "  " + edSendMessage.getText());
                        Carrier.getInstance().sendFriendMessage(FriendID, String.valueOf(edSendMessage.getText()));
                        ToastUtils.shortT("发送消息成功");
                    }
                } catch (ElastosException e) {
                    e.printStackTrace();
                    ToastUtils.shortT("发送消息失败");
                }

                finish();
            }
        });
        butRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.v("", FriendID + " recommended");
                    String recommendAddr = SharedPreferencesHelper.get(FriendID);
                    List<FriendInfo> friendInfos = Carrier.getInstance().getFriends();
                    if (friendInfos.size() > 0) {
                        for (FriendInfo fi : friendInfos) {
                            if (!fi.getUserId().equals(FriendID)) {
                                Log.v("", fi.getUserId());
                                try {
                                    Carrier.getInstance().sendFriendMessage(fi.getUserId(), "{recommend}:" + recommendAddr);
                                } catch (ElastosException e) {
                                }
                            }
                        }
                    }
                    ToastUtils.shortT("推荐好友成功");
                } catch (ElastosException e) {
                    e.printStackTrace();
                    ToastUtils.shortT("推荐好友失败");
                }

                finish();

            }
        });
    }


    private void showExpectDialog() {
        new MaterialDialog.Builder(this)
                .content("敬请期待")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

}
