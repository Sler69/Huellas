package com.example.huellas.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static MultipartBody.Part bitmapToMultipart(String filename, Bitmap rawImage, String formDataName , Context context, boolean useCache){
        File imageToUpload;
        MultipartBody.Part multiPartImage = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rawImage.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        byte[] bitmapdata = bos.toByteArray();

        try{
            if(useCache){
                imageToUpload = File.createTempFile(filename, ".jpg", context.getCacheDir());
            }else{
                imageToUpload = new File(context.getFilesDir(),filename + ".jpg");
            }

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

        if(!useCache){
            boolean deletedImage = imageToUpload.delete();
            System.out.println("Deleted file from internal storage:" + deletedImage );
        }


        return multiPartImage;
    }

    public static MultipartBody.Part defaultImage( String formDataName,Context context,AssetManager assetManager, String fileName, String fileAssetName, boolean useCache  ){

        File imageToUpload;
        MultipartBody.Part multiPartImage = null;

        try{
            if(useCache){
                imageToUpload = File.createTempFile(fileName, ".jpg", context.getCacheDir());
            }else{
                imageToUpload = new File(context.getFilesDir(),fileName + ".jpg");
            }
            InputStream is = assetManager.open(fileAssetName);
            FileOutputStream fos = new FileOutputStream(imageToUpload);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            fos.flush();
            fos.close();

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }


        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageToUpload);

        multiPartImage = MultipartBody.Part.createFormData(formDataName, imageToUpload.getName(), requestFile);

        if(!useCache){
            boolean deletedImage = imageToUpload.delete();
            System.out.println("Deleted file from internal storage:" + deletedImage);
        }

        return multiPartImage;
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

    public static Bitmap to1ByteBitmapOneCycle(Bitmap originalBitmap){
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int[] newColors = new int[width*height];
        originalBitmap.getPixels(newColors,0,width,0,0,width, height);
        for(int x = 0; x < newColors.length; x++){
            int pixel = newColors[x];
            int redValue = Color.red(pixel);
            int blueValue = Color.blue(pixel);
            int greenValue = Color.green(pixel);
            int greyscale = 255 - (int)(0.2126 * redValue + 0.7152 * greenValue + 0.0722 * blueValue);
            int newPixel =  Color.argb(greyscale,redValue,greenValue,blueValue);
            newColors[x] = newPixel;
        }

        return Bitmap.createBitmap(newColors,width,height,Bitmap.Config.ARGB_8888);
    }
}
