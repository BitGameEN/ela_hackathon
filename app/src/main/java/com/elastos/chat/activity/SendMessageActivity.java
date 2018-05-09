package com.elastos.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.helper.QRCodeHelper;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import butterknife.BindView;

/**
 * Created by fuwei on 2018/5/9.
 */

public class SendMessageActivity extends BaseActivity implements MainActivity.FriendMessage{

    @BindView(R.id.message_firend_id) TextView tvFriendid;
    @BindView(R.id.message_firend_send_message) TextView tvFriendMessage;
    @BindView(R.id.message_et_my_send_message)
    EditText edSendMessage;
    @BindView(R.id.message_but_send_message)
    Button butSendMyMessage;
    MainActivity.FriendMessage friendMessage;
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
        MainActivity fm = new MainActivity();
        fm.setOnFriendMessage(this);
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
    public void Message(String sFromID,String sMessage) {
        Log.v("","Message = "+sMessage);
        if(sFromID.equals(FriendID)){
            tvFriendMessage.setText(sMessage);
        }
    }



}
