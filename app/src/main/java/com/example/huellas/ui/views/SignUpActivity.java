package com.example.huellas.ui.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huellas.MainActivity;
import com.example.huellas.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText passwordText1;
    private EditText passwordText2;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent = getIntent();
        passwordText1 = findViewById(R.id.newPass1);
        passwordText2 = findViewById(R.id.newPass2);
    }

    public void verify(View view){

        passwordText2.setError("Passwords Don't Match");
    }
}
