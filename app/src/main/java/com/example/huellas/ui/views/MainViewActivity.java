package com.example.huellas.ui.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.huellas.MainActivity;
import com.example.huellas.R;
import com.example.huellas.data.SessionManager;


public class MainViewActivity extends AppCompatActivity {
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        session = new SessionManager(this);
        Intent intent = getIntent();
        String strUsername = intent.getStringExtra(MainActivity.USERNAME_MESSAGE);
        String strPassword = intent.getStringExtra(MainActivity.PASSWORD_MESSAGE);

        TextView textViewUsername = findViewById(R.id.username);
        TextView textViewPassword = findViewById(R.id.password);

        textViewUsername.setText(strUsername);
        textViewPassword.setText(strPassword);

    }
}
