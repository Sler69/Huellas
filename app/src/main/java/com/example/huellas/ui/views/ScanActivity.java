package com.example.huellas.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huellas.data.API;
import com.example.huellas.R;
import com.example.huellas.data.RetrofitClient;
import com.example.huellas.utils.ImageUtils;
import com.example.huellas.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {
    private TextView tvStatus;
    private TextView tvError;
    private TextView resultLabel;
    private Fingerprint fingerprint;
    private ImageView imageView;
    private Button scanNewFingerprint;
    private Button analyzeDefaultFingerprint;
    private Button extractMinutae;
    private Bitmap scannerBitmap;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_scan);

        tvStatus =  findViewById(R.id.tvStatus);
        tvError = findViewById(R.id.tvError);
        resultLabel = findViewById(R.id.scanResultText);

        imageView = findViewById(R.id.imageView);
        scanNewFingerprint = findViewById(R.id.saveButton);

        progressBarHolder =  findViewById(R.id.progressBarScan);

        analyzeDefaultFingerprint = findViewById(R.id.defaultFingerprint);
        extractMinutae = findViewById(R.id.extractMinutiae);
        
        extractMinutae.setVisibility(View.INVISIBLE);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        scanNewFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startScanner();
            }
        });

        analyzeDefaultFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyzeDefault();
            }
        });

        extractMinutae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractMinutiaeScanner();
            }
        });

        fingerprint = new Fingerprint();

    }

    public void startScanner(){
        fingerprint.scan(this, printHandler, updateHandler);
    }

    public void extractMinutiaeScanner(){

        UUID randomId = UUID.randomUUID();
        String idImage = randomId.toString();
        scannerBitmap = ImageUtils.to1ByteBitmapOneCycle(scannerBitmap);

        MultipartBody.Part scannerImageMultipart = ImageUtils.bitmapToMultipart(idImage,scannerBitmap,"fingerprint",this,false );
        if(scannerImageMultipart == null){
            showAlert("There was an error parsing the image, Try Again");
            return;
        }

        extractMinutiaeService(scannerImageMultipart);
    }

    public void analyzeDefault(){

        extractMinutae.setVisibility(View.INVISIBLE);
        Bitmap defaultImageBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger2);
        int width = defaultImageBitmap.getWidth();
        int height = defaultImageBitmap.getHeight();
        int byteCount = defaultImageBitmap.getByteCount();
        String informationOfCode = "Bytes for the image: "
                + byteCount + "\n"
                + " Width: " + width + "\n"
                + " Height: " + height + "\n"
                + " Byte per Pixel" + byteCount / (width * height);
        System.out.println(informationOfCode);
        imageView.setImageBitmap(defaultImageBitmap);
       // defaultImageBitmap = ImageUtils.to1ByteBitmapOneCycle(defaultImageBitmap);
        UUID randomId = UUID.randomUUID();
        String imageId = randomId.toString();
        //MultipartBody.Part imageToSend = ImageUtils.bitmapToMultipart(imageId,defaultImageBitmap,"fingerprint",this,false);
        MultipartBody.Part imageToSend = ImageUtils.defaultImage("fingerprint",this,getAssets(),imageId,"finger1.jpg",false);
        if(imageToSend == null){
            showAlert("There was an error on converting default image to request format.");
            return;
        }
        System.out.println(imageToSend);

        extractMinutiaeService(imageToSend);

    }

    public void showAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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

    final Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] image;
            String errorMessage;
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status",
                    status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                Bitmap informationFromScan = BitmapFactory.decodeByteArray(image, 0, image.length);
                int width = informationFromScan.getWidth();
                int height = informationFromScan.getHeight();
                int byteCount = informationFromScan.getByteCount();
                String informationOfCode = "Bytes for the image: "
                        + byteCount + "\n"
                        + " Width: " + width + "\n"
                        + " Height: " + height + "\n"
                        + " Byte per Pixel" + byteCount / (width * height);
                tvStatus.setText(informationOfCode);
                scannerBitmap = informationFromScan;
                imageView.setImageBitmap(scannerBitmap);
                extractMinutae.setVisibility(View.VISIBLE);
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
        }
    };

    private void extractMinutiaeService(final MultipartBody.Part imageToAnalyze){
        String token = PreferenceUtil.getUser(this);
        API service = RetrofitClient.getRetrofit().create(API.class);

        RequestBody type1 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "impression");
        RequestBody type2 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "finger");

        Call<ResponseBody> call = service.extract_minutiae("Bearer " + token,type1,type2, imageToAnalyze);
        showProgressbar();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressbar();
                String s = null;
                int responseCode = response.code();

                try {
                    if(responseCode != 200 && response.errorBody() != null){
                        showAlert(response.errorBody().string());
                        return;
                    }

                    if(responseCode != 200){
                        showAlert("There was an error from the server try later");
                        return;
                    }

                    if(response.body() == null){
                        showAlert("There was an error getting Minutae for the fingerprint");
                        return;
                    }

                    s = response.body().string();
                }catch (IOException e){
                    e.printStackTrace();
                    showAlert("There was an error parsing the response body");
                }

                try {
                    JSONArray minutaRawArray = new JSONArray(s);
                    String information = "";


                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    Bitmap workingBitmap = Bitmap.createBitmap(scannerBitmap);
                    Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    Canvas canvas = new Canvas(mutableBitmap);
                    for (int i = 0; i<minutaRawArray.length();i++){

                        JSONObject jsonObject = minutaRawArray.getJSONObject(i);
                        int x = jsonObject.getInt("X");
                        int y = jsonObject.getInt("Y");
                        double angle = jsonObject.getDouble("Angle");
                        float a = Float.parseFloat(String.valueOf(angle));

                        information += a +" " + Float.parseFloat(String.valueOf(Math.cos(a))) + " " + Float.parseFloat(String.valueOf(Math.sin(a))) + "\n";

                        canvas.drawCircle(x, y, 5, paint);
                        canvas.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

                    }

                    //resultLabel.setText(information);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageBitmap(mutableBitmap);
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


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressbar();
                System.out.println("Getting cause:" + t.getCause());
                System.out.println("Fallo la conexion con el servidor " + t.getMessage());
                System.out.println("Something failed" + Arrays.toString(t.getStackTrace()));
                tvError.setText(t.getMessage());
                call.cancel();
            }
        });
    }

    private void showProgressbar(){
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        scanNewFingerprint.setEnabled(false);
        analyzeDefaultFingerprint.setEnabled(false);
        extractMinutae.setEnabled(false);
    }

    private void hideProgressbar(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        scanNewFingerprint.setEnabled(true);
        analyzeDefaultFingerprint.setEnabled(true);
        extractMinutae.setEnabled(true);

    }
}
