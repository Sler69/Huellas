package com.example.huellas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static MultipartBody.Part bitmapToMultipart(String filename, Bitmap rawImage, Context context){
        File imageToUpload;
        MultipartBody.Part multiPartImage = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rawImage.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        byte[] bitmapdata = bos.toByteArray();

        try{
            imageToUpload = File.createTempFile(filename, ".jpg", context.getCacheDir());

            FileOutputStream fos = new FileOutputStream(imageToUpload);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageToUpload);

        multiPartImage = MultipartBody.Part.createFormData("fingerprint", imageToUpload.getName(), requestFile);

        return multiPartImage;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        float[] matrix = new float[]{
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0, 0, 0, 1, 0,};

        Bitmap dest = Bitmap.createBitmap(
                bmpOriginal.getWidth(),
                bmpOriginal.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);

        return dest;
    }
}
