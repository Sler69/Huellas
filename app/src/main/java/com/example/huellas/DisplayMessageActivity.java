package com.example.huellas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String strUsername = intent.getStringExtra(MainActivity.USERNAME_MESSAGE);
        String strPassword = intent.getStringExtra(MainActivity.PASSWORD_MESSAGE);

        TextView textViewUsername = findViewById(R.id.username);
        TextView textViewPassword = findViewById(R.id.password);

        textViewUsername.setText(strUsername);
        textViewPassword.setText(strPassword);

    }
}
