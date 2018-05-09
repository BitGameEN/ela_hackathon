package com.elastos.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.elastos.chat.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rczhang on 2018/05/09.
 */
public class QRCodeHelper {

    public static Bitmap createQRCodeBitmap(Context context,String strContent) {
        BitMatrix bitMatrix = null;// 生成矩阵
        Bitmap bitmap = null;
        int paintColor = ContextCompat.getColor(context, R.color.black);
        int backgroundColor =ContextCompat.getColor(context,R.color.white);
        try {
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            bitMatrix = new MultiFormatWriter().encode(strContent,
                    BarcodeFormat.QR_CODE, 300, 300, hints);
            int matrixHeight = bitMatrix.getHeight();
            int matrixWidth = bitMatrix.getWidth();
            bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < matrixWidth; x++) {
                for (int y = 0; y < matrixHeight; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? paintColor : backgroundColor);
                }
            }

        } catch (WriterException e) {
        }
        return bitmap;
    }
}
