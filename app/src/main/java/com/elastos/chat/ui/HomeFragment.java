package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.activity.MainActivity;

/**
 * @author rczhang on 2018/05/08.
 */
public class HomeFragment extends BaseFragment {

    private TextView txtAddress;
    private TextView txtUserId;
    private MainActivity activity;

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
        return view;
    }

    public void updateInfo(String carrierAddr, String carrierUserId) {
        txtAddress.setText(carrierAddr);
        txtUserId.setText(carrierUserId);
    }
}
