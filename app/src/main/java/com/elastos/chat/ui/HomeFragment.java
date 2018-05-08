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
import com.elastos.chat.activity.MainActivity;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

/**
 * @author rczhang on 2018/05/08.
 */
public class HomeFragment extends BaseFragment {

    private TextView txtAddress;
    private TextView txtUserId;
    private MainActivity activity;
    private Button butAddFriend;
    private EditText etFriendAddress;
    Carrier carrierInst = null;

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
                        carrierInst.addFriend(friendadd, "auto-accepted");
                    } catch (ElastosException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    public void updateInfo(String carrierAddr, String carrierUserId,Carrier carrier) {
        txtAddress.setText(carrierAddr);
        txtUserId.setText(carrierUserId);
        carrierInst = carrier;
    }
}
