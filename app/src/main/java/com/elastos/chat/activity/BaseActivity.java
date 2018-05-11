package com.elastos.chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elastos.chat.R;
import com.elastos.chat.util.AndroidUtilities;
import com.elastos.helper.CarrierHelper;

import org.elastos.carrier.Carrier;

import butterknife.ButterKnife;

/**
 * @author rczhang on 2018/05/08.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private MaterialDialog mProgressDialog;

    protected abstract int getContentViewResId();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(getContentViewResId());
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        if (CarrierHelper.isReady) {
            initVariables();
            initViews(savedInstanceState);
        } else {
            showProgressDialog(R.string.loading, false);
            CarrierHelper.init();
            CarrierHelper.setCallback(new CarrierHelper.CallbackHandler() {
                @Override
                public void onReady(Carrier carrier) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isFinishing()) {
                                return;
                            }
                            dismissProgressDialog();
                            initVariables();
                            initViews(savedInstanceState);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    protected void initVariables() {
    }

    protected void initViews(Bundle savedInstanceState) {
    }

    public void showProgressDialog() {
        showProgressDialog(R.string.loading);
    }

    public void showProgressDialog(int contentRes) {
        showProgressDialog(contentRes, true);
    }

    public void showProgressDialog(int contentRes, boolean cancelable) {
        showProgressDialog(contentRes, cancelable, cancelable);
    }

    public void showProgressDialog(int contentRes, boolean cancelable, boolean canceledOnTouchOutside) {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .content(contentRes)
                    .cancelable(cancelable)
                    .canceledOnTouchOutside(canceledOnTouchOutside)
                    .progress(true, 0)
                    .show();
        } else {
            mProgressDialog.setContent(contentRes);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
