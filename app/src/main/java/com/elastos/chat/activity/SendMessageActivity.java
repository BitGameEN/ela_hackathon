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
import com.elastos.helper.CarrierHelper;

import android.net.Uri;
import android.content.ContentResolver;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import java.util.List;

import butterknife.BindView;

/**
 * Created by fuwei on 2018/5/9.
 */

public class SendMessageActivity extends BaseActivity {

    @BindView(R.id.message_et_my_send_message) EditText edSendMessage;
    @BindView(R.id.message_but_send_message) Button butSendMyMessage;
    @BindView(R.id.message_but_send_picture) Button butSendMyPicture;
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
        butSendMyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            final InputStream inputStream;
            try {
                inputStream = cr.openInputStream(uri);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 读取
                            byte temp[] = new byte[2048];
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            int len = 0;
                            while ((len = inputStream.read(temp)) > 0){
                                outStream.write(temp, 0, len);
                            }
                            inputStream.close();
                            outStream.close();
                            // 发送
                            if (CarrierHelper.sendDataWithSession(FriendID, outStream.toByteArray())) {
                                ToastUtils.shortT("发送图片成功");
                            }else{
                                ToastUtils.shortT("发送图片失败");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.shortT("发送图片失败");
                        }
                    }
                }).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
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
