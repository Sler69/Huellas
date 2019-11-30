package com.example.huellas.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huellas.R;
import com.example.huellas.data.API;
import com.example.huellas.data.RetrofitClient;
import com.example.huellas.utils.ImageUtils;
import com.example.huellas.utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class ComparisonActivity extends AppCompatActivity {

    private TextView firstScanLabel;
    private TextView secondScanLabel;
    private TextView resultLabel;
    private Button scanFirstFingerprintButton;
    private Button scanSecondFingerprintButton;
    private Button analyzeFingerPrintsButton;
    private Button defaultAnalyzeButton;
    private ImageView firstFingerprintImage;
    private ImageView secondFingerprintImage;
    private Bitmap  firstImageBitmap = null;
    private Bitmap secondImageBitmap = null;
    private boolean isFirstFingerPrint = true, activatedButton1 = true, activatedButton2 = true;
    private ToggleButton individual, matching;
    private Bitmap individualFirstBitmap, matchingFirstBitmap;
    private Bitmap individualSecondBitmap, matchingSecondBitmap;
    private Canvas canvasA, canvasB;
    private JSONArray firstInd, secondInd, match;
    private Bitmap mutableBitmap, mutableBitmapB;
    private Paint paint;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;


    private Handler scanHandler;
    private Handler responseHandler;
    private Fingerprint scanFingerPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comparison);

        firstScanLabel = findViewById(R.id.firstLabelError);
        secondScanLabel = findViewById(R.id.secondLabelError);
        resultLabel = findViewById(R.id.resultText);

        individual = findViewById(R.id.individualToggle);
        matching = findViewById(R.id.matchingToggle);

        scanFirstFingerprintButton = findViewById(R.id.buttonScanFirstFingerprint);
        scanSecondFingerprintButton = findViewById(R.id.buttonScanSecondFingerprint);
        analyzeFingerPrintsButton = findViewById(R.id.buttonCompareFingerprints);
        defaultAnalyzeButton = findViewById(R.id.buttonDefaultFingerprints);

        progressBarHolder =  findViewById(R.id.progressBarComparison);

        scanFirstFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanFirstFingerPrint();
            }
        });

        scanSecondFingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanSecondFingerPrint();
            }
        });

        analyzeFingerPrintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyzeScannedFingerprints();
            }
        });



        defaultAnalyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyzeDefaultImages();
            }
        });

        firstFingerprintImage = findViewById(R.id.firstComparisonImageView);
        secondFingerprintImage = findViewById(R.id.secondComparisonImageView);

        scanFingerPrint = new Fingerprint();
        responseHandler = generateResponseHandler();
        scanHandler = generateScanHandler();

        individual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activatedButton1 = b;
                if(b&&activatedButton2){
                    paintBothMinutias();
                    return;
                }
                if(b){
                    paintIndividual();
                    return;

                }
                if(activatedButton2){
                    paintMatching();
                    return;
                }
                firstFingerprintImage.setImageBitmap(firstImageBitmap);
                secondFingerprintImage.setImageBitmap(secondImageBitmap);

            }
        });

        matching.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activatedButton2 = b;
                if(b&&activatedButton1){
                    paintBothMinutias();
                    return;

                }
                if(b){
                    paintMatching();
                    return;
                }
                if(activatedButton1){
                    paintIndividual();
                    return;
                }

                firstFingerprintImage.setImageBitmap(firstImageBitmap);
                secondFingerprintImage.setImageBitmap(secondImageBitmap);
            }
        });
    }

    private void analyzeDefaultImages(){
        Bitmap firstDefaultBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger1);
        Bitmap secondDefaultBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger2);
        firstFingerprintImage.setImageBitmap(firstDefaultBitmap);
        secondFingerprintImage.setImageBitmap(secondDefaultBitmap);
        firstImageBitmap = null;
        secondImageBitmap = null;
        UUID finger1Id = UUID.randomUUID();
        UUID finger2Id = UUID.randomUUID();

        // MultipartBody.Part firstDefaultImage = ImageUtils.bitmapToMultipart(finger1Id.toString(),firstDefaultBitmap,"fingerprintA",this,false);

        MultipartBody.Part firstDefaultImage = ImageUtils.defaultImage("fingerprintA",this,getAssets(),finger1Id.toString(),"finger1.jpg",false);
        if(firstDefaultImage == null){
            showAlert("There was an error on parsing the first image.");
            return;
        }
        // MultipartBody.Part secondDefaultImage = ImageUtils.bitmapToMultipart(finger2Id.toString(),secondDefaultBitmap,"fingerprintB",this,false);
        MultipartBody.Part secondDefaultImage = ImageUtils.defaultImage("fingerprintB",this,getAssets(),finger2Id.toString(),"finger2.jpg",false);
        if(secondDefaultImage == null ){
            showAlert("There was an error on parsing the default images to request format.");
            return;
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);


        Bitmap workingBitmap = Bitmap.createBitmap(firstDefaultBitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        canvasA = new Canvas(mutableBitmap);
        canvasA.drawCircle(100,100, 20, paint);

        firstFingerprintImage.setAdjustViewBounds(true);
        firstFingerprintImage.setImageBitmap(mutableBitmap);
        //sendComparisonToServer(firstDefaultImage, secondDefaultImage);
    }

    private void scanFirstFingerPrint(){
        isFirstFingerPrint = true;
        scanFingerPrint.scan(this, scanHandler , responseHandler);
    }

    private void scanSecondFingerPrint(){
        isFirstFingerPrint = false;
        scanFingerPrint.scan(this, scanHandler,responseHandler);
    }

    private void analyzeScannedFingerprints(){

        if(firstImageBitmap == null|| secondImageBitmap == null){
            showAlert("We need two Fingerprints to start the process.");
            return;
        }

        firstImageBitmap = ImageUtils.to1ByteBitmapOneCycle(firstImageBitmap);
        secondImageBitmap = ImageUtils.to1ByteBitmapOneCycle(secondImageBitmap);

        UUID randomId1 = UUID.randomUUID();
        String imageId1 = randomId1.toString();
        MultipartBody.Part imageFirstScan = ImageUtils.bitmapToMultipart(imageId1,firstImageBitmap,"fingerprintA",this,false);

        if(imageFirstScan == null){
            showAlert("There was an error on parsing the first image.");
            return;
        }

        UUID randomId2 = UUID.randomUUID();
        String imageId2 = randomId2.toString();

        MultipartBody.Part imageSecondScan = ImageUtils.bitmapToMultipart(imageId2,secondImageBitmap,"fingerprintB",this,false);
        if(imageSecondScan == null){
            showAlert("There was an error on parsing the second image.");
            return;
        }

        sendComparisonToServer(imageFirstScan, imageSecondScan);
    }

    private void sendComparisonToServer(MultipartBody.Part firstFingerPrint, MultipartBody.Part secondFingerPrint){
        showProgressbar();

        String token = PreferenceUtil.getUser(this);
        API service = RetrofitClient.getRetrofit().create(API.class);

        if(firstFingerPrint == null || secondFingerPrint == null){
            showAlert("There was an error transforming the fingerprint files" );
        }

        RequestBody type1 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "impression");
        RequestBody type2 =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "impression");
        RequestBody region =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "finger");

        RequestBody rotation =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "off");

        Call<ResponseBody> call = service.match_1v1("Bearer " + token,type1,type2,region ,rotation , firstFingerPrint, secondFingerPrint);
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
                String information = "";
                try {
                    JSONObject minutaRawObject = new JSONObject(s);
                    JSONArray minutaARawArray = minutaRawObject.getJSONArray("minutiaeA");
                    firstInd = minutaARawArray;
                    JSONArray minutaBRawArray = minutaRawObject.getJSONArray("minutiaeB");
                    secondInd = minutaBRawArray;
                    JSONArray minutaMatching = minutaRawObject.getJSONArray("matchingPairs");
                    match = minutaMatching;
                    String minutaSimilarity = minutaRawObject.getString("similarity");
                    information = "Similarity between fingerprints: " + minutaSimilarity.toString() ;

                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);

                    Paint paintMatch = new Paint();
                    paintMatch.setAntiAlias(true);
                    paintMatch.setColor(Color.GREEN);
                    paintMatch.setStyle(Paint.Style.STROKE);
                    paintMatch.setStrokeWidth(2);
                    Bitmap workingBitmap = Bitmap.createBitmap(firstImageBitmap);
                    mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    canvasA = new Canvas(mutableBitmap);
                    for (int i = 0; i<minutaARawArray.length();i++){

                        JSONObject jsonObject = minutaARawArray.getJSONObject(i);
                        int x = jsonObject.getInt("X");
                        int y = jsonObject.getInt("Y");
                        double angle = jsonObject.getDouble("Angle");
                        float a = Float.parseFloat(String.valueOf(angle));

                       // information += a +" " + Float.parseFloat(String.valueOf(Math.cos(a))) + " " + Float.parseFloat(String.valueOf(Math.sin(a))) + "\n";

                        canvasA.drawCircle(x, y, 5, paint);
                        canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

                    }

                    individualFirstBitmap = mutableBitmap;

                    for (int i = 0; i<minutaMatching.length();i++){

                        JSONObject jsonObject = minutaMatching.getJSONObject(i);
                        JSONObject minutia = jsonObject.getJSONObject("QueryMtia");
                        int x = minutia.getInt("X");
                        int y = minutia.getInt("Y");
                        double angle = minutia.getDouble("Angle");
                        float a = Float.parseFloat(String.valueOf(angle));

                        //information += a +" " + Float.parseFloat(String.valueOf(Math.cos(a))) + " " + Float.parseFloat(String.valueOf(Math.sin(a))) + "\n";

                        canvasA.drawCircle(x, y, 5, paintMatch);
                        canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

                    }


                    firstFingerprintImage.setAdjustViewBounds(true);
                    firstFingerprintImage.setImageBitmap(mutableBitmap);

                    Bitmap workingBitmapB = Bitmap.createBitmap(secondImageBitmap);
                    mutableBitmapB = workingBitmapB.copy(Bitmap.Config.ARGB_8888, true);

                    canvasB = new Canvas(mutableBitmapB);
                    for (int i = 0; i<minutaBRawArray.length();i++){

                        JSONObject jsonObject = minutaBRawArray.getJSONObject(i);
                        int x = jsonObject.getInt("X");
                        int y = jsonObject.getInt("Y");
                        double angle = jsonObject.getDouble("Angle");
                        float a = Float.parseFloat(String.valueOf(angle));

                        //information += a +" " + Float.parseFloat(String.valueOf(Math.cos(a))) + " " + Float.parseFloat(String.valueOf(Math.sin(a))) + "\n";

                        canvasB.drawCircle(x, y, 5, paint);
                        canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

                    }
                    for (int i = 0; i<minutaMatching.length();i++){

                        JSONObject jsonObject = minutaMatching.getJSONObject(i);
                        JSONObject minutia = jsonObject.getJSONObject("TemplateMtia");
                        int x = minutia.getInt("X");
                        int y = minutia.getInt("Y");
                        double angle = minutia.getDouble("Angle");
                        float a = Float.parseFloat(String.valueOf(angle));

                        //information += a +" " + Float.parseFloat(String.valueOf(Math.cos(a))) + " " + Float.parseFloat(String.valueOf(Math.sin(a))) + "\n";

                        canvasB.drawCircle(x, y, 5, paintMatch);
                        canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

                    }


                    secondFingerprintImage.setAdjustViewBounds(true);
                    secondFingerprintImage.setImageBitmap(mutableBitmapB);
                } catch (JSONException e) {
                    e.printStackTrace();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                        String message = jsonObject.getString("message");
                        showAlert(message);
                        return;
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
                resultLabel.setText(information);
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressbar();
                System.out.println("Getting cause:" + t.getCause());
                System.out.println("Fallo la conexion con el servidor " + t.getMessage());
                System.out.println("Something failed" + Arrays.toString(t.getStackTrace()));
                showAlert(t.getMessage());
                call.cancel();
            }
        });
    }


    private Handler generateResponseHandler(){
        return new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                int status = msg.getData().getInt("status");
                String message;
                if(isFirstFingerPrint){
                    firstScanLabel.setText("");
                }else{
                    secondScanLabel.setText("");
                }
                // TODO Refactor switch on util.
                switch (status) {
                    case Status.INITIALISED:
                        message = "Preparando lector de huellas";
                        break;
                    case Status.SCANNER_POWERED_ON:
                        message = "Lector de huellas prendido";
                        break;
                    case Status.READY_TO_SCAN:
                        message = "Lector de huellas listo para escanear";
                        break;
                    case Status.FINGER_DETECTED:
                        message = "Dedo detectado";
                        break;
                    case Status.RECEIVING_IMAGE:
                        message = "Recibiendo imagen";
                        break;
                    case Status.FINGER_LIFTED:
                        message = "El dedo se ha levantado del lector de huellas";
                        break;
                    case Status.SCANNER_POWERED_OFF:
                        message = "Lector de huellas apagado";
                        break;
                    case Status.SUCCESS:
                        message ="Huella capturada exitosamente";
                        break;
                    case Status.ERROR:
                        message = msg.getData().getString("errorMessage");
                        break;
                    default:
                        message = msg.getData().getString("errorMessage");
                        break;
                }

                if(isFirstFingerPrint){
                    firstScanLabel.setText(message);
                }else{
                    secondScanLabel.setText(message);
                }
            }
        };
    }

    private Handler generateScanHandler(){
        return new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                byte[] scannedImageBytes;
                String errorMessage;
                int status = msg.getData().getInt("status");
                Intent intent = new Intent();
                intent.putExtra("status", status);
                scannedImageBytes = msg.getData().getByteArray("img");
                if (status == Status.SUCCESS && scannedImageBytes != null) {
                    if(isFirstFingerPrint){
                        firstImageBitmap = BitmapFactory.decodeByteArray(scannedImageBytes, 0, scannedImageBytes.length);
                        firstFingerprintImage.setImageBitmap(firstImageBitmap);
                    }else{
                        secondImageBitmap = BitmapFactory.decodeByteArray(scannedImageBytes, 0, scannedImageBytes.length);
                        secondFingerprintImage.setImageBitmap(secondImageBitmap);
                    }

                    if(firstImageBitmap != null && secondImageBitmap != null){
                        analyzeFingerPrintsButton.setVisibility(View.VISIBLE);
                    }else{
                        analyzeFingerPrintsButton.setVisibility(View.INVISIBLE);
                    }

                } else {
                    errorMessage = msg.getData().getString("errorMessage");
                    intent.putExtra("errorMessage", errorMessage);
                }
            }
        };
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

    private void showProgressbar(){
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        scanFirstFingerprintButton.setEnabled(false);
        scanSecondFingerprintButton.setEnabled(false);
        analyzeFingerPrintsButton.setEnabled(false);
        defaultAnalyzeButton.setEnabled(false);
    }

    private void hideProgressbar(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        scanFirstFingerprintButton.setEnabled(true);
        scanSecondFingerprintButton.setEnabled(true);
        analyzeFingerPrintsButton.setEnabled(true);
        defaultAnalyzeButton.setEnabled(true);

    }

    private void paintMatching(){
        Paint paintMatch = new Paint();
        paintMatch.setAntiAlias(true);
        paintMatch.setColor(Color.GREEN);
        paintMatch.setStyle(Paint.Style.STROKE);
        paintMatch.setStrokeWidth(2);

        Bitmap workingBitmap = Bitmap.createBitmap(firstImageBitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasA = new Canvas(mutableBitmap);

        for (int i = 0; i<match.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = match.getJSONObject(i);
                JSONObject minutia = jsonObject.getJSONObject("QueryMtia");
                int x = minutia.getInt("X");
                int y = minutia.getInt("Y");
                double angle = minutia.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));
                canvasA.drawCircle(x, y, 5, paintMatch);
                canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        firstFingerprintImage.setAdjustViewBounds(true);
        firstFingerprintImage.setImageBitmap(mutableBitmap);

        Bitmap workingBitmapB = Bitmap.createBitmap(secondImageBitmap);
        mutableBitmapB = workingBitmapB.copy(Bitmap.Config.ARGB_8888, true);

        canvasB = new Canvas(mutableBitmapB);

        for (int i = 0; i<match.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = match.getJSONObject(i);
                JSONObject minutia = jsonObject.getJSONObject("TemplateMtia");
                int x = minutia.getInt("X");
                int y = minutia.getInt("Y");
                double angle = minutia.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));

                canvasB.drawCircle(x, y, 5, paintMatch);
                canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //resultLabel.setText(information);
        secondFingerprintImage.setAdjustViewBounds(true);
        secondFingerprintImage.setImageBitmap(mutableBitmapB);

    }

    private void paintIndividual(){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        Bitmap workingBitmap = Bitmap.createBitmap(firstImageBitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasA = new Canvas(mutableBitmap);
        for (int i = 0; i<firstInd.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = firstInd.getJSONObject(i);
                int x = jsonObject.getInt("X");
                int y = jsonObject.getInt("Y");
                double angle = jsonObject.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));
                canvasA.drawCircle(x, y, 5, paint);
                canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        firstFingerprintImage.setAdjustViewBounds(true);
        firstFingerprintImage.setImageBitmap(mutableBitmap);

        Bitmap workingBitmapB = Bitmap.createBitmap(secondImageBitmap);
        Bitmap mutableBitmapB = workingBitmapB.copy(Bitmap.Config.ARGB_8888, true);

        canvasB = new Canvas(mutableBitmapB);
        for (int i = 0; i<secondInd.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = secondInd.getJSONObject(i);
                int x = jsonObject.getInt("X");
                int y = jsonObject.getInt("Y");
                double angle = jsonObject.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));

                canvasB.drawCircle(x, y, 5, paint);
                canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        secondFingerprintImage.setAdjustViewBounds(true);
        secondFingerprintImage.setImageBitmap(mutableBitmapB);
    }

    private void paintBothMinutias(){

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        Bitmap workingBitmap = Bitmap.createBitmap(firstImageBitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasA = new Canvas(mutableBitmap);
        for (int i = 0; i<firstInd.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = firstInd.getJSONObject(i);
                int x = jsonObject.getInt("X");
                int y = jsonObject.getInt("Y");
                double angle = jsonObject.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));
                canvasA.drawCircle(x, y, 5, paint);
                canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Paint paintMatch = new Paint();
        paintMatch.setAntiAlias(true);
        paintMatch.setColor(Color.GREEN);
        paintMatch.setStyle(Paint.Style.STROKE);
        paintMatch.setStrokeWidth(2);


        for (int i = 0; i<match.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = match.getJSONObject(i);
                JSONObject minutia = jsonObject.getJSONObject("QueryMtia");
                int x = minutia.getInt("X");
                int y = minutia.getInt("Y");
                double angle = minutia.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));
                canvasA.drawCircle(x, y, 5, paintMatch);
                canvasA.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        firstFingerprintImage.setAdjustViewBounds(true);
        firstFingerprintImage.setImageBitmap(mutableBitmap);

        Bitmap workingBitmapB = Bitmap.createBitmap(secondImageBitmap);
        Bitmap mutableBitmapB = workingBitmapB.copy(Bitmap.Config.ARGB_8888, true);

        canvasB = new Canvas(mutableBitmapB);
        for (int i = 0; i<secondInd.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = secondInd.getJSONObject(i);
                int x = jsonObject.getInt("X");
                int y = jsonObject.getInt("Y");
                double angle = jsonObject.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));

                canvasB.drawCircle(x, y, 5, paint);
                canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paint);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i<match.length();i++){

            JSONObject jsonObject = null;
            try {
                jsonObject = match.getJSONObject(i);
                JSONObject minutia = jsonObject.getJSONObject("TemplateMtia");
                int x = minutia.getInt("X");
                int y = minutia.getInt("Y");
                double angle = minutia.getDouble("Angle");
                float a = Float.parseFloat(String.valueOf(angle));

                canvasB.drawCircle(x, y, 5, paintMatch);
                canvasB.drawLine(x, y, x+8*Float.parseFloat(String.valueOf(Math.cos(a))), y+8*Float.parseFloat(String.valueOf(Math.sin(a))), paintMatch);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        secondFingerprintImage.setAdjustViewBounds(true);
        secondFingerprintImage.setImageBitmap(mutableBitmapB);


    }

}
