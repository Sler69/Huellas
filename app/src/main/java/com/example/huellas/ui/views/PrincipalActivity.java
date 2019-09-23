package com.example.huellas.ui.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huellas.Huella;
import com.example.huellas.Huella_adapter;
import com.example.huellas.R;
import com.example.huellas.ui.fragments.ComparisonFragment;
import com.example.huellas.ui.fragments.FingerprintsFragment;
import com.example.huellas.ui.fragments.NewComparisonFragment;
import com.example.huellas.ui.fragments.NewFingerPrintFragment;
import com.example.huellas.ui.fragments.RecyclerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import asia.kanopi.fingerscan.Status;

public class PrincipalActivity extends AppCompatActivity {

    ImageView ivFinger;
    TextView tvMessage;
    byte[] img;
    Bitmap bm;
    private static final int SCAN_FINGER = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_activity);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ComparisonFragment()).commit();
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.nav_comparisons:
                        selectedFragment = new RecyclerFragment();
                        break;
                    case R.id.nav_fingerprints:
                        selectedFragment = new FingerprintsFragment();
                        break;
                    case R.id.nav_new_comparison:
                        selectedFragment = new NewComparisonFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        };

    public void startScan(View v) {
        switch (v.getId()) {
            case R.id.scan:
                // for ex: your package name can be "com.example"
                // your activity name will be "com.example.Contact_Developer"
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent, SCAN_FINGER);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int status;
        String errorMesssage;
        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        tvMessage.setText("Fingerprint captured");
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ivFinger.setImageBitmap(bm);
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " + errorMesssage + " --");
                    }
                }
                break;
            }
        }
    }
}
