package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class QRCodeContentActivity extends BaseActivity {

    @BindView(R.id.content)
    TextView content;

    private String qrCodeContent;

    public static void start(Context context, String qrCodeContent) {
        Intent intent = new Intent(context, QRCodeContentActivity.class);
        intent.putExtra(Extra.QR_CODE_CONTENT, qrCodeContent);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables() {
        qrCodeContent = getIntent().getStringExtra(Extra.QR_CODE_CONTENT);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        setContentView(R.layout.activity_qr_code_content);
        content.setText(qrCodeContent);
    }
}
