package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.helper.CopyHelper;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/11.
 */
public class AddressActivity extends BaseActivity {

    @BindView(R.id.my_address)
    TextView addressView;

    private String address;

    public static void start(Context context, String address) {
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(Extra.MY_ADDRESS, address);
        context.startActivity(intent);
    }

    @Override
    protected boolean needSetupCarrier() {
        return false;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_address;
    }

    @Override
    protected void initVariables() {
        address = getIntent().getStringExtra(Extra.MY_ADDRESS);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        addressView.setText(address);
        addressView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CopyHelper.copyText(address);
                return true;
            }
        });
    }
}
