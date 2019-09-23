package com.example.huellas.ui.views;

import androidx.appcompat.app.AppCompatActivity;
import com.example.huellas.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowFingerprintActivity extends AppCompatActivity {

    private TextView tvTextFingerprint, tvDescription;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fingerprint);

        tvTextFingerprint = (TextView) findViewById(R.id.texto_huella);
        tvDescription = (TextView) findViewById(R.id.texto_desc);
        img = (ImageView) findViewById(R.id.fingerprintThumbnail);

        Intent intent = getIntent();
        String huella = intent.getExtras().getString("Fingerprint");
        String desc = intent.getExtras().getString("Description");
        int image = intent.getExtras().getInt("Thumbnail");

        tvTextFingerprint.setText(huella);
        tvDescription.setText(desc);
        img.setImageResource(image);
    }
}
