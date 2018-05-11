package com.elastos.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.elastos.chat.R;
import com.elastos.chat.SharedPreferencesHelper;
import com.elastos.chat.activity.MyAddressShowActivity;
import com.elastos.chat.activity.MyQRCodeActivity;
import com.elastos.chat.activity.NicknameSetActivity;
import com.elastos.chat.activity.ScanQRCodeActivity;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.ProfileItemView;
import com.elastos.chat.util.ToastUtils;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/08.
 */
public class MeFragment extends BaseFragment {

    public static int REQUEST_CODE_SET_NAME = 1;

    @BindView(R.id.my_address)
    ProfileItemView myAddress;
    @BindView(R.id.my_user_id)
    ProfileItemView myUserId;
    @BindView(R.id.qr_code)
    ProfileItemView qrCodeItem;
    @BindView(R.id.nickname)
    ProfileItemView nickname;
    @BindView(R.id.scan)
    ProfileItemView scan;

    private Button butPublishMsg;
    private EditText etPublicMsg;

    public static MeFragment newInstance() {
        MeFragment meFragment = new MeFragment();
        return meFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    @Override
    public void onPageFirstStart() {
        super.onPageFirstStart();
        try {
            nickname.setDesc(Carrier.getInstance().getSelfInfo().getName());
            nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(NicknameSetActivity.getStartIntent(getContext(), nickname.getDesc()), REQUEST_CODE_SET_NAME);
                }
            });
        } catch (ElastosException e) {
            e.printStackTrace();
        }

        qrCodeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address = Carrier.getInstance().getAddress();
                    MyQRCodeActivity.start(getContext(), address);
                } catch (ElastosException e) {
                    e.printStackTrace();
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQRCodeActivity.start(getContext());
            }
        });

        try {
            String address = Carrier.getInstance().getAddress();
            String userId = Carrier.getInstance().getUserId();
            myAddress.setDesc(address);
            myUserId.setDesc(userId);
            myAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(MyAddressShowActivity.getStartIntent(getContext(), myAddress.getDesc()));
                }
            });
            myUserId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(MyAddressShowActivity.getStartIntent(getContext(), myUserId.getDesc()));
                }
            });
        } catch (ElastosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SET_NAME) {
            nickname.setDesc(data.getStringExtra(Extra.NICKNAME));
        }
    }
}
