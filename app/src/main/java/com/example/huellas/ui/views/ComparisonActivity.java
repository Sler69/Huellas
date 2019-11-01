package com.example.huellas.ui.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huellas.R;
import com.example.huellas.utils.ImageUtils;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;

public class ComparisonActivity extends AppCompatActivity {

    private TextView firstScanLabel;
    private TextView secondScanLabel;
    private Button scanFirstFingerprintButton;
    private Button scanSecondFingerprintButton;
    private Button analyzeFingerPrintsButton;
    private Button defaultAnalyzeButton;
    private ImageView firstFingerprintImage;
    private ImageView secondFingerprintImage;
    private Bitmap  firstImageBitmap = null;
    private Bitmap secondImageBitmap = null;
    private boolean isFirstFingerPrint = true;

    private Handler scanHandler;
    private Handler responseHandler;
    private Fingerprint scanFingerPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comparison);

        firstScanLabel = findViewById(R.id.firstLabelError);
        secondScanLabel = findViewById(R.id.secondLabelError);

        scanFirstFingerprintButton = findViewById(R.id.buttonScanFirstFingerprint);
        scanSecondFingerprintButton = findViewById(R.id.buttonScanSecondFingerprint);
        analyzeFingerPrintsButton = findViewById(R.id.buttonCompareFingerprints);
        defaultAnalyzeButton = findViewById(R.id.buttonDefaultFingerprints);

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
    }

    private void analyzeDefaultImages(){
        Bitmap firstDefaultBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger1);
        Bitmap secondDefaultBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.finger2);
        firstFingerprintImage.setImageBitmap(firstDefaultBitmap);
        secondFingerprintImage.setImageBitmap(secondDefaultBitmap);
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
        // TODO: Implement Retrofit
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
                        firstImageBitmap = ImageUtils.toGrayscale(firstImageBitmap);
                        firstFingerprintImage.setImageBitmap(firstImageBitmap);
                    }else{
                        secondImageBitmap = BitmapFactory.decodeByteArray(scannedImageBytes, 0, scannedImageBytes.length);
                        secondImageBitmap = ImageUtils.toGrayscale(firstImageBitmap);
                        secondFingerprintImage.setImageBitmap(secondImageBitmap);
                    }

                    if(firstImageBitmap != null && secondImageBitmap != null){
                        analyzeFingerPrintsButton.setVisibility(View.VISIBLE);
                    }

                } else {
                    errorMessage = msg.getData().getString("errorMessage");
                    intent.putExtra("errorMessage", errorMessage);
                }
            }
        };
    }



}
