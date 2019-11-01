package com.example.huellas.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private Fingerprint fingerprint;
    private ImageView imageView;
    private Button scanNewFingerprint;
    private Button analyzeDefaultFingerprint;
    private Button extractMinutae;
    private Bitmap scannerBitmap;
    private Boolean shiftImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_scan);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);
        imageView = findViewById(R.id.imageView);
        scanNewFingerprint = findViewById(R.id.saveButton);

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
        extractMinutiaeService(scannerBitmap);
    }

    public void analyzeDefault(){
        extractMinutae.setVisibility(View.INVISIBLE);
        Bitmap defaultImageBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger2);
        imageView.setImageBitmap(defaultImageBitmap);
        extractMinutiaeService(defaultImageBitmap);
    }

    public void showAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
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
                scannerBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                scannerBitmap = ImageUtils.toGrayscale(scannerBitmap);
                imageView.setImageBitmap(scannerBitmap);
                extractMinutae.setVisibility(View.VISIBLE);
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
        }
    };

    private void extractMinutiaeService(Bitmap imageBitmap){
        UUID randomId = UUID.randomUUID();
        MultipartBody.Part imageMultiPart = ImageUtils.bitmapToMultipart(randomId.toString(),imageBitmap, this);
        String token = PreferenceUtil.getUser(this);
        API service = RetrofitClient.getRetrofit().create(API.class);

        RequestBody type1 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "impression");
        RequestBody type2 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "finger");

        Call<ResponseBody> call = service.extract_minutiae("Bearer " + token,type1,type2, imageMultiPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
}
