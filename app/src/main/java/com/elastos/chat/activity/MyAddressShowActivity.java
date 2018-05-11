package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.chat.ui.view.AppBar;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class MyAddressShowActivity extends BaseActivity {

    @BindView(R.id.appbar)
    AppBar appBar;
    @BindView(R.id.input)
    EditText input;

    public String my_address;

    public static Intent getStartIntent(Context context, String my_address) {
        Intent intent = new Intent(context, MyAddressShowActivity.class);
        intent.putExtra(Extra.MY_ADDRESS, my_address);
        return intent;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_show_my_adrress;
    }

    @Override
    protected void initVariables() {
        my_address = getIntent().getStringExtra(Extra.MY_ADDRESS);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        appBar.setRightTextEnable(false);
        input.setText(my_address);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        appBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
