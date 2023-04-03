package com.chindle.sharelibrary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class AppUtil {

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bitmap depthCompressBitmap(Bitmap tmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(isBm, null, options);
        options.inJustDecodeBounds = false;
        float be = options.outWidth / 480;
        float scale = 0.55f;
        if (be >= 2) {
            scale = 0.35f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        tmp = Bitmap.createBitmap(tmp, 0, 0, options.outWidth, options.outHeight, matrix, true);
        baos.reset();
        tmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int op = 40;
        while (baos.toByteArray().length > 32 * 1024) {
            baos.reset();
            tmp.compress(Bitmap.CompressFormat.JPEG, op, baos);
            op -= 10;
            if (op <= 0) {
                break;
            }
        }
        if (baos.toByteArray().length > 32 * 1024) {
            isBm = new ByteArrayInputStream(baos.toByteArray());
            tmp = BitmapFactory.decodeStream(isBm, null, options);
            matrix.setScale(0.8f, 0.8f);
            tmp = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, true);
            baos.reset();
            tmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        }
        return tmp;
    }
}
