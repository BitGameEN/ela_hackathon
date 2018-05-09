package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;

import com.elastos.chat.R;
import com.elastos.chat.common.Extra;
import com.elastos.helper.QRCodeHelper;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class MyQRCodeActivity extends BaseActivity {

    @BindView(R.id.qr_code)
    ImageView qrCode;

    public String qrCodeContent;

    public static void start(Context context,String qrCodeContent) {
        Intent intent = new Intent(context, MyQRCodeActivity.class);
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
        setContentView(R.layout.activity_my_qr_code);
        // TODO: 2018/5/9 移到线程中
        Bitmap qrCodeBitmap = QRCodeHelper.createQRCodeBitmap(this, qrCodeContent);
        qrCode.setImageBitmap(qrCodeBitmap);
    }
}
