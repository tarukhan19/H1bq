package com.dbvertex.company.h1bq.Util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by shree on 4/20/2018.
 */

public class BitmapUtil {
    public static String getStringFromBitmap(Bitmap bm)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
    }
}
