package com.example.huellas.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huellas.API;
import com.example.huellas.R;
import com.example.huellas.RetrofitClient;
import com.example.huellas.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {
    private TextView tvStatus;
    private TextView tvError;
    private Fingerprint fingerprint;
    private ImageView imageView;
    private Button button;
    private Button grayScaleButton;
    private Button extractMinutae;
    private Bitmap bm;
    private Bitmap bm2;
    private Boolean shiftImage = false;
    private File imageToUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_scan);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.saveButton);
        grayScaleButton = findViewById(R.id.grayscale);
        extractMinutae = findViewById(R.id.extractMinutiae);
        button.setVisibility(View.INVISIBLE);
        grayScaleButton.setVisibility(View.INVISIBLE);


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAlert();
            }
        });

        grayScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchGrayScale();
            }
        });

        extractMinutae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractMinutiae();
            }
        });

        fingerprint = new Fingerprint();

    }

    public void extractMinutiae(){
        API service = RetrofitClient.getRetrofit().create(API.class);
        UUID randomId = UUID.randomUUID();
        String encoded;
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger1);
        imageView.setImageBitmap(bm);
        Bitmap bitmap = bm2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        byte[] bitmapdata = bos.toByteArray();
        encoded = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        /*
        try{
            imageToUpload = File.createTempFile(randomId.toString(), ".jpg", this.getCacheDir());

            FileOutputStream fos = new FileOutputStream(imageToUpload);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            */
        /*}catch (IOException e){
            e.printStackTrace();
            return;
        }
        */
        /*
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/*"), imageToUpload);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", imageToUpload.getName(), requestFile);
        */
        String token = PreferenceUtil.getUser(this);
        Call<ResponseBody> call = service.extract_minutiae("Bearer " + token,"impression","finger", encoded);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String s = null;
                imageToUpload.delete();
                if(response.code() == 200 && response.body() != null){
                    System.out.println("EVERYTHING WENT OKAY!!!!");
                    System.out.println(response.body());
                    s = response.body().toString();
                    System.out.println("Response: " + s);
                    tvError.setText(s);
                }else{
                    System.out.println("THERE WAS AN ERROR");
                    System.out.println(response);
                    if(response.errorBody() != null){
                        s = "There is no error message but the response code is not 200.";
                    }
                    System.out.println("Response: " + s);
                }
                if(s != null){
                    try {
                        JSONArray minutaRawArray = new JSONArray(s);
                        String information = minutaRawArray.toString();
                        tvError.setText(information);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(s);
                            String message = jsonObject.getString("message");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Getting cause:" + t.getCause());
                System.out.println("Fallo la conexion con el servidor " + t.getMessage());
                System.out.println("Something failed" + Arrays.toString(t.getStackTrace()));
                tvError.setText(t.getMessage());
                call.cancel();
            }
        });
    }

    public void switchGrayScale(){
        if(shiftImage){
            imageView.setImageBitmap(bm);
            shiftImage = false;
            tvError.setText("Normal");
        }else{
            imageView.setImageBitmap(bm2);
            shiftImage = true;
            tvError.setText("GrayScale");
        }
    }

    public void showAlert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Huella Registrada");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                fingerprint.turnOffReader();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        fingerprint.scan(this, printHandler, updateHandler);
        super.onStart();
    }

    @Override
    protected void onStop() {
        fingerprint.turnOffReader();
        super.onStop();
    }

    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");
            tvError.setText("");
            switch (status) {
                case Status.INITIALISED:
                    tvStatus.setText("Preparando lector de huellas");
                    break;
                case Status.SCANNER_POWERED_ON:
                    tvStatus.setText("Lector de huellas prendido");
                    break;
                case Status.READY_TO_SCAN:
                    tvStatus.setText("Lector de huellas listo para escanear");
                    break;
                case Status.FINGER_DETECTED:
                    tvStatus.setText("Dedo detectado");
                    break;
                case Status.RECEIVING_IMAGE:
                    tvStatus.setText("Recibiendo imagen");
                    break;
                case Status.FINGER_LIFTED:
                    tvStatus.setText("El dedo se ha levantado del lector de huellas");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    tvStatus.setText("Lector de huellas apagado");
                    break;
                case Status.SUCCESS:
                    tvStatus.setText("Huella capturada exitosamente");
                    break;
                case Status.ERROR:
                    tvStatus.setText("Error");
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    tvStatus.setText(String.valueOf(status));
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;

            }
        }
    };

    Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] image;
            String errorMessage = "empty";
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status",
                    status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView.setImageBitmap(bm);
                bm2 = toGrayscale(bm);
                button.setVisibility(View.VISIBLE);
                grayScaleButton.setVisibility(View.VISIBLE);
                extractMinutae.setVisibility(View.VISIBLE);
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
        }
    };


    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        //Custom color matrix to convert to GrayScale
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
   /* private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
//        tv.append("\n\nExternal Media: readable="
//                +mExternalStorageAvailable+" writable="+mExternalStorageWriteable);
    }

    *//** Method to write ascii text characters to file on SD card. Note that you must add a
     WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     a FileNotFound Exception because you won't have write permission. *//*

    private void writeToSDFile(){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //tv.append("\nExternal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Hi , How are you");
            pw.println("Hello");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
       // tv.append("\n\nFile written to "+file);
    }

    *//** Method to read in a text file placed in the res/raw directory of the application. The
     method reads in all lines of the file sequentially. *//*

    private void readRaw() {
        // tv.append("\nData read from res/raw/textfile.txt:");
        InputStream is = this.getResources().openRawResource(R.textfile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

        // More efficient (less readable) implementation of above is the composite expression
    *//*BufferedReader br = new BufferedReader(new InputStreamReader(
            this.getResources().openRawResource(R.raw.textfile)), 8192);*//*

        try {
            String test;
            while (true) {
                test = br.readLine();
                // readLine() returns null if no more lines in the file
                if (test == null) break;
                // tv.append("\n"+"    "+test);
            }
            isr.close();
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // tv.append("\n\nThat is all");
    }*/
}
