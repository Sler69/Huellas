package com.example.huellas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huellas.data.SessionManager;
import com.example.huellas.ui.views.MainViewActivity;
import com.example.huellas.ui.views.PrincipalActivity;


public class MainActivity extends AppCompatActivity {
    public static final String USERNAME_MESSAGE = "com.example.huellas.USERNAME";
    public static final String PASSWORD_MESSAGE = "com.example.huellas.PASSWORD";
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button loginButton;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(this);
        editTextUsername =  findViewById(R.id.usernameText);
        editTextPassword =  findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        editTextPassword.addTextChangedListener(loginTextWatcher);
        editTextUsername.addTextChangedListener(loginTextWatcher);
    }

    public void sendMessage(View view) {
        boolean isAuthenticated = checkStatus();
        if(isAuthenticated) {
            Intent intent = new Intent(this, PrincipalActivity.class);
            String strUsername = editTextUsername.getText().toString();
            String strPassword = editTextPassword.getText().toString();
            intent.putExtra(USERNAME_MESSAGE, strUsername);
            intent.putExtra(PASSWORD_MESSAGE, strPassword);
            session.setUsername(strUsername);
            startActivity(intent);
            return;
        }
        editTextUsername.setError("Wrong Username or password");

    }

    private boolean checkStatus(){
        String strUserName = getUsernameString();
        String strPassword = getPasswordString();
        if(strUserName.equals("test@test.com") && strPassword.equals("welcome1")){
            return true;
        }
        return false;
    }

    private String getUsernameString(){
        return editTextUsername.getText().toString();
    }

    private String getPasswordString(){
        return editTextPassword.getText().toString();
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String usernameInput = getUsernameString();
            String passwordInput = getPasswordString();

            loginButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
