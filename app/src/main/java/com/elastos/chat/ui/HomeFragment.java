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
import android.widget.Toast;

import com.elastos.chat.R;
import com.elastos.chat.activity.MainActivity;
import com.elastos.chat.activity.ScanQRCodeActivity;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

/**
 * @author rczhang on 2018/05/08.
 */
public class HomeFragment extends BaseFragment {

    private TextView txtAddress;
    private TextView txtUserId;
    private TextView txtFriendinfo;
    private MainActivity activity;
    private Button butAddFriend;
    private Button butGetFriend;
    private Button butDelFriend;
    private Button butPublishMsg;
    private EditText etFriendAddress;
    private EditText etDelFriendUid;
    private EditText etPublicMsg;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtAddress = view.findViewById(R.id.home_address);
        txtUserId = view.findViewById(R.id.home_user_id);
        activity = (MainActivity)this.getActivity();
        etFriendAddress = view.findViewById(R.id.home_et_friend_address);
        butAddFriend = view.findViewById(R.id.home_but_add_friend);
        butAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String friendadd = etFriendAddress.getText().toString();
                if(!friendadd.equals("")) {
                    try {
                        Log.v("","向"+friendadd+"发送好友申请");
                        String selfAddr = Carrier.getInstance().getAddress();
                        Carrier.getInstance().addFriend(friendadd, selfAddr);
                        activity.put(Carrier.getIdFromAddress(friendadd), friendadd);
                        Toast.makeText(activity, "添加好友成功", Toast.LENGTH_SHORT).show();
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "添加好友失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        txtFriendinfo = view.findViewById(R.id.home_friend_user_id);
        butGetFriend = view.findViewById(R.id.home_but_get_friend);
        butGetFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    List<FriendInfo> friendInfos = Carrier.getInstance().getFriends();
                    if(friendInfos.size()>0) {
                        for (FriendInfo fi:friendInfos) {
                            Log.v("", fi.getUserId());
                            txtFriendinfo.setText(txtFriendinfo.getText()+"|"+fi.getUserId());
                        }
                    }
                } catch (ElastosException e) {
                    e.printStackTrace();
                }
            }
        });
        etDelFriendUid = view.findViewById(R.id.home_et_friend_del_address);
        butDelFriend = view.findViewById(R.id.home_but_friend_del_uid);
        butDelFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    String toDelFriendUid = etDelFriendUid.getText().toString().trim();
                    if(! toDelFriendUid.isEmpty()){
                        Carrier.getInstance().removeFriend(etDelFriendUid.getText().toString());
                        Toast.makeText(activity, "删除好友成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (ElastosException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "删除好友失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        etPublicMsg = view.findViewById(R.id.home_et_public_msg);
        butPublishMsg = view.findViewById(R.id.home_but_publish_msg);
        butPublishMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                activity.put("public_message", etPublicMsg.getText().toString());
                String msg = activity.get("public_message");
                Log.v("", msg);
                if (! msg.trim().isEmpty()){
                    try {
                        List<FriendInfo> friendInfos = Carrier.getInstance().getFriends();
                        if(friendInfos.size()>0) {
                            for (FriendInfo fi:friendInfos) {
                                Log.v("", fi.getUserId());
                                Carrier.getInstance().sendFriendMessage(fi.getUserId(), msg);
                            }
                        }
                        Toast.makeText(activity, "发布消息成功", Toast.LENGTH_SHORT).show();
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "发布消息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    public void updateInfo(String carrierAddr, String carrierUserId) {
        txtAddress.setText(carrierAddr);
        txtUserId.setText(carrierUserId);
    }
}
