package com.example.huellas.ui.views;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.huellas.R;
import com.example.huellas.ui.fragments.ComparisonFragment;
import com.example.huellas.ui.fragments.FingerprintsFragment;
import com.example.huellas.ui.fragments.NewComparisonFragment;
import com.example.huellas.ui.fragments.NewFingerPrintFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_activity);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ComparisonFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.nav_comparisons:
                        selectedFragment = new ComparisonFragment();
                        break;
                    case R.id.nav_fingerprints:
                        selectedFragment = new FingerprintsFragment();
                        break;
                    case R.id.nav_new_comparison:
                        selectedFragment = new NewComparisonFragment();
                        break;
                    case R.id.nav_new_fingerprint:
                        selectedFragment = new NewFingerPrintFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        };
}
