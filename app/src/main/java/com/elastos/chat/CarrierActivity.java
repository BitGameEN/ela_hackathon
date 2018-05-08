package com.elastos.chat;

import com.elastos.helper.Synchronizer;
import android.content.Context;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;


import com.elastos.helper.TestOptions;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class CarrierActivity extends AppCompatActivity {


    Carrier carrierInst = null;
    String carrierAddr = null;
    String carrierUserID = null;
    String TAG = "DemoTag";

    private Button addFrindBnt;
    private Button sendMessageBnt;

    //更加好友地址不同替换
    final String friendAddr = "7Ntu9ev1SY71c74ySL1hmsrejnAivaPD5DUUjkPYXDZjnLQu3Kao";
    final String friendUserID = "3uHutUcGb9Sc2VX87bwxuoejX7oDFRc2FuUkyYHxbNpG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier);

        TestOptions options = new TestOptions(getAppPath());
        TestHandler handler = new TestHandler();


        //1.初始化实例，获得相关信息
        try {
            //1.1获得Carrier的实例
            carrierInst = Carrier.getInstance(options, handler);

            //1.2获得Carrier的地址
            carrierAddr = carrierInst.getAddress();
            Log.i(TAG,"address: " + carrierAddr);

            //1.3获得Carrier的用户ID
            carrierUserID = carrierInst.getUserId();
            Log.i(TAG,"userID: " + carrierUserID);

            //1.4启动网络
            carrierInst.start(1000);
            handler.synch.await();
            Log.i(TAG,"carrier client is ready now");

        } catch (ElastosException e) {
            e.printStackTrace();
        }


        addFrindBnt = findViewById(R.id.addFriend);
        addFrindBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2.1添加好友
                try {
                    Log.i(TAG,"start add Frind");
                    carrierInst.addFriend(friendAddr, "auto-accepted");
                    Log.i(TAG,"end add Frind");
                }catch (ElastosException e) {
                    e.printStackTrace();
                }

            }
        });


        sendMessageBnt = findViewById(R.id.sendMessage);
        sendMessageBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.1发送信息
                try {
                    Log.i(TAG,"start send message");
                    carrierInst.sendFriendMessage("3uHutUcGb9Sc2VX87bwxuoejX7oDFRc2FuUkyYHxbNpG", "hello e");
                    Log.i(TAG,"end send message");
                }catch (ElastosException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private String getAppPath() {

        Context context=this;
        File file=context.getFilesDir();
        String path=file.getAbsolutePath();
        return path;
    }


    static class TestHandler extends AbstractCarrierHandler {
        Synchronizer synch = new Synchronizer();
        String from;
        ConnectionStatus friendStatus;
        String CALLBACK="call back";

        public void onReady(Carrier carrier) {
            synch.wakeup();
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {

            Log.i(CALLBACK,"friendid:" + friendId + "connection changed to: " + status);
            from = friendId;
            friendStatus = status;
            if (friendStatus == ConnectionStatus.Connected)
                synch.wakeup();
        }

        //2.2 通过好友验证
        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            try {

                if (hello.equals("auto-accepted")) {
                    carrier.AcceptFriend(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        //3.2 接受好友信息
        public void onFriendMessage(Carrier carrier,String fromId, String message) {

            Log.i(CALLBACK,"address:" + fromId + "connection changed to: " + message);
        }

    }
}
