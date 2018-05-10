package com.elastos.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.AppBar;
import com.elastos.helper.BusProvider;
import com.elastos.helper.QRCodeHelper;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.squareup.otto.Subscribe;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import butterknife.BindView;

/**
 * Created by fuwei on 2018/5/9.
 */

public class SendMessageActivity extends BaseActivity {

    @BindView(R.id.message_firend_id) TextView tvFriendid;
    @BindView(R.id.message_firend_send_message) TextView tvFriendMessage;
    @BindView(R.id.message_et_my_send_message) EditText edSendMessage;
    @BindView(R.id.message_appbar) AppBar appBar;
    @BindView(R.id.message_but_send_message)
    Button butSendMyMessage;
    public String FriendID;
    private CompoundBarcodeView barcodeScannerView;

    public static void start(Context context, String friendid) {
        Intent intent = new Intent(context, SendMessageActivity.class);
        intent.putExtra(Extra.SEND_MESSAGE_FRIENDID, friendid);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables() {
        FriendID = getIntent().getStringExtra(Extra.SEND_MESSAGE_FRIENDID);
    }

    public void updateFriendMessage(String sFromId, String sMessage) {
        if(FriendID.equals(sFromId)) {
            tvFriendMessage.setText(sMessage);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        tvFriendid.setText("FriendID:"+FriendID);
        appBar.setTitle(FriendID);
        butSendMyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.v("",FriendID+"  "+edSendMessage.getText());
                    Carrier.getInstance().sendFriendMessage(FriendID, String.valueOf(edSendMessage.getText()));
//                    ((MainActivity) getApplicationContext()).carrierInst.sendFriendMessage(FriendID, String.valueOf(edSendMessage.getText()));
                } catch (ElastosException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册到bus事件总线中
        BusProvider.getInstance().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }
    /**
     * 定义订阅者，Activity中发布的消息，在此处会接收到，在此之前需要先在程序中register，看
     * 上面的onStart和onStop函数
     */
    @Subscribe
    public void setContent(MainActivity.FriendMessage data) {
        tvFriendMessage.setText(data.getsFriendId());
        if(data.getsFriendId().equals(FriendID)){
            tvFriendMessage.setText(data.getsMessage());
        }
    }

    @Subscribe
    public void onDataChange(String sss) {
        System.out.println("====" + sss);
    }
}
