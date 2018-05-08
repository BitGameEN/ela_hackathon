package com.elastos.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.adapter.MainFragmentPagerAdapter;
import com.elastos.helper.Synchronizer;
import com.elastos.helper.TestOptions;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.io.File;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;

    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    Carrier carrierInst = null;
    String carrierAddr = null;
    String carrierUserID = null;
    String TAG = "DemoTag";

    private static final int DELAY = 2000;
    private static final int INIT_CARRIER = 0;

    private TextView txtInitProgress;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainFragmentPagerAdapter.saveInstanceState(outState);
    }

    @Override
    protected void initVariables() {
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setupViewPager(savedInstanceState);
    }

    private void setupViewPager(Bundle savedInstanceState) {
        mainFragmentPagerAdapter.restoreInstanceState(savedInstanceState);
        viewPager.setOffscreenPageLimit(mainFragmentPagerAdapter.getCount());
        viewPager.setAdapter(mainFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgHandler.sendEmptyMessageDelayed(INIT_CARRIER, DELAY);

    }

    Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CARRIER: {
                    txtInitProgress = findViewById(R.id.init_progress);
                    TestOptions options = new TestOptions(getAppPath());
                    TestHandler handler = new TestHandler();
                    //1.初始化实例，获得相关信息
                    try {
                        //1.1获得Carrier的实例
                        carrierInst = Carrier.getInstance(options, handler);

                        //1.2获得Carrier的地址
                        carrierAddr = carrierInst.getAddress();
                        txtInitProgress.setText("address: " + carrierAddr);
                        Log.i(TAG,"address: " + carrierAddr);

                        //1.3获得Carrier的用户ID
                        carrierUserID = carrierInst.getUserId();
                        txtInitProgress.setText("user_id: " + carrierUserID);
                        Log.i(TAG,"user_id: " + carrierUserID);

                        //1.4启动网络
                        txtInitProgress.setText("connecting to carrier....");
                        carrierInst.start(1000);
                        handler.synch.await();
                        txtInitProgress.setText("carrier client is ready now");
                        Log.i(TAG,"carrier client is ready now");
                    } catch (ElastosException e) {
                        txtInitProgress.setText("carrier client connect failed");
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

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
