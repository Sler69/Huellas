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

import com.example.huellas.R;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;

public class ScanActivity extends AppCompatActivity {
    private TextView tvStatus;
    private TextView tvError;
    private Fingerprint fingerprint;
    private ImageView imageView;
    private Button button;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_scan);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);
        imageView = findViewById(R.id.imageView);
        button =findViewById(R.id.saveButton);
        button.setVisibility(View.INVISIBLE);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAlert();
            }
        });
        fingerprint = new Fingerprint();

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
            intent.putExtra("status", status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView.setImageBitmap(bm);
                button.setVisibility(View.VISIBLE);
                intent.putExtra("img", image);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
            /*setResult(RESULT_OK, intent);
            finish();*/
        }
    };

}
