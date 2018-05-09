package com.elastos.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.elastos.chat.R;
import com.elastos.chat.ui.view.AppBar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import butterknife.BindView;

/**
 * @author rczhang on 2018/05/09.
 */
public class ScanQRCodeActivity extends BaseActivity {

    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;

    @BindView(R.id.appbar) AppBar appBar;

    public static void start(Context context) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeScannerView = initializeContent();
        capture = new CaptureManager(this, barcodeScannerView);
        barcodeScannerView.setStatusText("扫一扫，添加好友");
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                barcodeScannerView.pause();
                if (result.getResult().getBarcodeFormat() != BarcodeFormat.QR_CODE) {
                    barcodeScannerView.resume();
                    Toast.makeText(ScanQRCodeActivity.this, "没有发现二维码", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Carrier.getInstance().addFriend(result.toString(),"hello");
                        Toast.makeText(ScanQRCodeActivity.this, "添加好友成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (ElastosException e) {
                        e.printStackTrace();
                        QRCodeContentActivity.start(ScanQRCodeActivity.this,result.toString());
                        finish();
                    }
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    /**
     * Override to use a different layout.
     *
     * @return the CompoundBarcodeView
     */
    protected CompoundBarcodeView initializeContent() {
        setContentView(R.layout.activity_scan_qr_code);
        return (CompoundBarcodeView) findViewById(R.id.zxing_barcode_scanner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}