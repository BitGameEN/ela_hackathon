package com.elastos.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elastos.chat.R;
import com.elastos.chat.activity.MainActivity;
import com.elastos.chat.activity.MyQRCodeActivity;
import com.elastos.chat.ui.view.ProfileItemView;

import org.elastos.carrier.exceptions.ElastosException;

import butterknife.BindInt;
import butterknife.BindView;

/**
 * @author rczhang on 2018/05/08.
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.qr_code)
    ProfileItemView qrCodeItem;

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
        qrCodeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address = ((MainActivity) getContext()).carrierInst.getAddress();
                    MyQRCodeActivity.start(getContext(),address);
                } catch (ElastosException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
