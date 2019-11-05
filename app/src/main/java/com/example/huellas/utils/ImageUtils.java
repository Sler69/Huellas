package com.example.huellas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
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

    public static MultipartBody.Part bitmapToMultipart(String filename, Bitmap rawImage, String formDataName , Context context){
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

        multiPartImage = MultipartBody.Part.createFormData(formDataName, imageToUpload.getName(), requestFile);

        return multiPartImage;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {

        Bitmap dest = Bitmap.createBitmap(
                bmpOriginal.getWidth(),
                bmpOriginal.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);

        return dest;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
