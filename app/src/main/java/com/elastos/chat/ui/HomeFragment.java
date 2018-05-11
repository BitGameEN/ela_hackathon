package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.activity.MainActivity;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

/**
 * @author rczhang on 2018/05/08.
 */
public class HomeFragment extends BaseFragment {

    private MainActivity activity;
    private Button butAddFriend;
    private Button butPublishMsg;
    private EditText etFriendAddress;
    private EditText etPublicMsg;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) this.getActivity();
        etFriendAddress = view.findViewById(R.id.home_et_friend_address);
        butAddFriend = view.findViewById(R.id.home_but_add_friend);
        butAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String friendadd = etFriendAddress.getText().toString();
                if (!friendadd.equals("")) {
                    try {
                        Log.v("", "向" + friendadd + "发送好友申请");
                        String selfAddr = Carrier.getInstance().getAddress();
                        Carrier.getInstance().addFriend(friendadd, selfAddr);
                        SharedPreferencesHelper.put(Carrier.getIdFromAddress(friendadd), friendadd);
                        ToastUtils.shortT("添加好友成功");
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        ToastUtils.shortT("添加好友失败");
                    }
                }
            }
        });

        etPublicMsg = view.findViewById(R.id.home_et_public_msg);
        butPublishMsg = view.findViewById(R.id.home_but_publish_msg);
        butPublishMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferencesHelper.put("public_message", etPublicMsg.getText().toString());
                String msg = SharedPreferencesHelper.get("public_message");
                Log.v("", msg);
                if (!msg.trim().isEmpty()) {
                    try {
                        List<FriendInfo> friendInfos = Carrier.getInstance().getFriends();
                        if (friendInfos.size() > 0) {
                            for (FriendInfo fi : friendInfos) {
                                Log.v("", fi.getUserId());
                                Carrier.getInstance().sendFriendMessage(fi.getUserId(), msg);
                            }
                        }
                        ToastUtils.shortT("发布消息成功");
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        ToastUtils.shortT("发布消息失败");
                    }
                }
            }
        });
    }
}
