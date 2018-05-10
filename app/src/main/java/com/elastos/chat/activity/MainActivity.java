package com.elastos.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.adapter.MainFragmentPagerAdapter;
import com.elastos.chat.ui.HomeFragment;
import com.elastos.chat.util.ToastUtils;
import com.elastos.helper.BusProvider;
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

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MainActivity mainActivity = null;
    // 获取接口对象
    private static FriendMessage friendMessageListener;
    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    Carrier carrierInst = null;
    String carrierAddr = null;
    String carrierUserId = null;
    String TAG = "DemoTag";

    private static final int DELAY = 1000;
    private static final int INIT_CARRIER = 0;

    private TextView txtInitProgress;

    private void MainActivity() {
    }

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
        initViews(savedInstanceState);
        mainActivity = this;
        txtInitProgress = findViewById(R.id.init_progress);
        txtInitProgress.setText("连接中.... ");
        msgHandler.sendEmptyMessageDelayed(INIT_CARRIER, DELAY);
    }

    Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CARRIER: {
                    TestOptions options = new TestOptions(getAppPath());
                    TestHandler handler = new TestHandler();
                    //1.初始化实例，获得相关信息
                    try {
                        //1.1获得Carrier的实例
                        carrierInst = Carrier.getInstance(options, handler);

                        //1.2获得Carrier的地址
                        carrierAddr = carrierInst.getAddress();
                        Log.i(TAG, "address: " + carrierAddr);

                        //1.3获得Carrier的用户ID
                        carrierUserId = carrierInst.getUserId();
                        Log.i(TAG, "user_id: " + carrierUserId);

                        //1.4启动网络
                        carrierInst.start(1000);
                        handler.synch.await();
                        txtInitProgress.setText("已连接 ");
                        Log.i(TAG, "carrier client is ready now");

                        //更新UI
                        HomeFragment homeFragment = (HomeFragment) mainFragmentPagerAdapter.getItem(MainFragmentPagerAdapter.HOME);
                        homeFragment.updateInfo(carrierAddr, carrierUserId);
                    } catch (ElastosException e) {
                        txtInitProgress.setText("连接失败 ");
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

        Context context = this;
        File file = context.getFilesDir();
        String path = file.getAbsolutePath();
        return path;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        carrierInst.kill();
    }

    static class TestHandler extends AbstractCarrierHandler {
        Synchronizer synch = new Synchronizer();
        String from;
        ConnectionStatus friendStatus;
        String CALLBACK = "call back";

        TestHandler() {
            super();
        }

        public void onReady(Carrier carrier) {
            synch.wakeup();
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
            Log.i(CALLBACK, "friendid:" + friendId + "connection changed to: " + status);
            from = friendId;
            friendStatus = status;
            if (friendStatus == ConnectionStatus.Connected) {
                synch.wakeup();
                String msg = SharedPreferencesHelper.get("public_message");
                if (!msg.trim().isEmpty()) {
                    try {
                        Carrier.getInstance().sendFriendMessage(friendId, msg);
                    } catch (ElastosException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //2.2 通过好友验证
        public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
            try {

                Log.v("", "接收到" + userId + "发来的好友申请，内容 " + hello);
                // 先全部自动接受为好友
                carrier.AcceptFriend(userId);
                SharedPreferencesHelper.put(userId, hello);
                ToastUtils.shortT("自动添加好友成功");
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.shortT("自动添加好友失败");
            }
        }

        @Override
        //3.2 接受好友信息
        public void onFriendMessage(Carrier carrier, String fromId, String message) {
            try {
                Log.i(CALLBACK, "address:" + fromId + "message: " + message);
                if (message.startsWith("{recommend}:")) {
                    String recommendAddr = message.substring(12);
                    carrier.addFriend(recommendAddr, carrier.getAddress());
                    ToastUtils.shortT("自动添加推荐好友成功");
                } else {
                    //if (!(friendMessageListener == null)) {
                    //    friendMessageListener.Message(fromId, message);
                    //}
                    BusProvider.getInstance().post(new FriendMessage(fromId, message));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (message.startsWith("{recommend}:")) {
                    ToastUtils.shortT("自动添加推荐好友失败");
                }
            }
        }
    }

    //    // 定义接口
    //    public interface FriendMessage{
    //        public void Message(String sFromID,String sMessage);
    //    }
    //
    //    //用于B绑定接口
    //    public void setOnFriendMessage(FriendMessage mListener) {
    //        this.friendMessageListener = mListener;
    //    }
    public static class FriendMessage {
        private String sFriendId;
        private String sMessage;

        public FriendMessage(String sFromID, String sMessage) {
            this.sFriendId = sFromID;
            this.sMessage = sMessage;
        }

        public String getsFriendId() {
            return sFriendId;
        }

        public String getsMessage() {
            return sMessage;
        }

        public void setsFriendId(String sFriendId) {
            this.sFriendId = sFriendId;
        }

        public void setsMessage(String sMessage) {
            this.sMessage = sMessage;
        }
    }
}
