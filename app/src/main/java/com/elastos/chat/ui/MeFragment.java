package com.elastos.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elastos.chat.R;
import com.elastos.chat.activity.AddressActivity;
import com.elastos.chat.activity.UserIdActivity;
import com.elastos.chat.activity.MyQRCodeActivity;
import com.elastos.chat.activity.NicknameSetActivity;
import com.elastos.chat.activity.PublishMessageActivity;
import com.elastos.chat.activity.ScanQRCodeActivity;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.ProfileItemView;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

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
    @BindView(R.id.publish_message)
    ProfileItemView publishMessage;

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
        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishMessageActivity.start(getContext());
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
                    AddressActivity.start(getContext(), myAddress.getDesc());
                }
            });
            myUserId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(UserIdActivity.getStartIntent(getContext(), myUserId.getDesc()));
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
