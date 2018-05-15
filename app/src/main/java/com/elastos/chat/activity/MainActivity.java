package com.elastos.chat.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.adapter.MainFragmentPagerAdapter;
import com.elastos.chat.ui.FriendsFragment;
import com.elastos.chat.util.AndroidUtilities;
import com.elastos.chat.util.ToastUtils;
import com.elastos.helper.BusProvider;
import com.elastos.helper.CarrierHelper;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mainFragmentPagerAdapter != null) {
            mainFragmentPagerAdapter.saveInstanceState(outState);
        }
    }

    @Override
    protected void initVariables() {
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
        CarrierHelper.setCallback(new CarrierHelper.CallbackHandler() {
            public void onFriendConnection(Carrier carrier, final String friendId, final ConnectionStatus status) {
                Log.i("call back", "friendid:" + friendId + "connection changed to: " + status);
                if (status == ConnectionStatus.Connected) {
                    String msg = SharedPreferencesHelper.get("public_message");
                    if (!msg.trim().isEmpty()) {
                        try {
                            Carrier.getInstance().sendFriendMessage(friendId, msg);
                        } catch (ElastosException e) {
                            e.printStackTrace();
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = mainFragmentPagerAdapter.getItem(MainFragmentPagerAdapter.FRIENDS);
                        ((FriendsFragment) fragment).updateFriendStatus(friendId, status);
                    }
                });
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
            public void onFriendAdded(Carrier carrier, final FriendInfo info) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ((FriendsFragment) mainFragmentPagerAdapter.getItem(MainFragmentPagerAdapter.FRIENDS)).addFriend(info);
                    }
                });
            }

            @Override
            public void onFriendRemoved(Carrier carrier, final String friendId) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ((FriendsFragment) mainFragmentPagerAdapter.getItem(MainFragmentPagerAdapter.FRIENDS)).removeFriend(friendId);
                    }
                });
            }

            @Override
            public void onFriendInfoChanged(Carrier carrier, final String friendId, final FriendInfo info) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = mainFragmentPagerAdapter.getItem(MainFragmentPagerAdapter.FRIENDS);
                        ((FriendsFragment) fragment).updateFriendInfo(friendId, info);
                    }
                });
            }

            @Override
            //3.2 接受好友信息
            public void onFriendMessage(Carrier carrier, String fromId, String message) {
                try {
                    Log.i("call back", "userId:" + fromId + "message: " + message);
                    if (message.startsWith("{recommend}:")) {
                        String recommendAddr = message.substring(12);
                        Log.v("", recommendAddr);
                        carrier.addFriend(recommendAddr.replace("\b", ""), carrier.getAddress());
                        ToastUtils.shortT("自动添加推荐好友成功");
                    } else if (message.compareTo("MSG_REPLY_SESSION_REQUST_AND_START") == 0) {
                        CarrierHelper.replySessionRequestAndStart();
                    }
                    else {
                        BusProvider.getInstance().post(new FriendMessage(fromId, message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (message.startsWith("{recommend}:")) {
                        Log.e("", "recommendAddr=" + message.substring(12) + ", error=" + e.getMessage());
                        ToastUtils.shortT("自动添加推荐好友失败");
                    }
                }
            }
        });
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setupViewPager(savedInstanceState);
    }

    private void setupViewPager(Bundle savedInstanceState) {
        mainFragmentPagerAdapter.restoreInstanceState(savedInstanceState);
        viewPager.setOffscreenPageLimit(mainFragmentPagerAdapter.getCount());
        viewPager.setAdapter(mainFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CarrierHelper.destroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

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
