package com.example.huellas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huellas.data.SessionManager;
import com.example.huellas.ui.views.PrincipalActivity;
import com.example.huellas.ui.views.SignUpActivity;
import com.example.huellas.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    // public static final String USERNAME_MESSAGE = "com.example.huellas.USERNAME";
    // public static final String PASSWORD_MESSAGE = "com.example.huellas.PASSWORD";
    private EditText mailText;
    private EditText passwordText;
    private Button loginButton;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // session = new SessionManager(this);
        mailText =  findViewById(R.id.usernameText);
        passwordText =  findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(view);
            }
        });
       /* editTextPassword.addTextChangedListener(loginTextWatcher);
        editTextUsername.addTextChangedListener(loginTextWatcher);*/
    }

    public void attemptLogin(View view) {
        /*
         */
        String pass = passwordText.getText().toString();

        mailText.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        final String email = mailText.getText().toString();
        String password = passwordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordText.setError(getString(R.string.error_invalid_password));
            focusView = passwordText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mailText.setError(getString(R.string.error_field_required));
            focusView = mailText;
            cancel = true;

        } else if (!isEmailValid(email)) {
            mailText.setError(getString(R.string.error_invalid_email));
            focusView = mailText;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getString(R.string.error_field_required));
            focusView = passwordText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            API service = RetrofitClient.getRetrofit().create(API.class);

            Call<ResponseBody> call = service.login(mailText.getText().toString(), passwordText.getText().toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    String s = null;
                    try {

                        if(response.code() == 200){
                            s = response.body().string();
                            System.out.println("Response: " + s);
                        }else{
                            s = response.errorBody().string();
                            System.out.println("Response Error: " + s);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(s != null){
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String token = jsonObject.getString("token");

                            PreferenceUtil.saveUser(token,MainActivity.this);
                            Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
                            startActivity(intent);

                            System.out.println(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("Fallo la conexion con el servidor"+t.getMessage());

                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
/*
    public void sendMessage(View view) {
         // boolean isAuthenticated = checkStatus();
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
*/
    public void signUp(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
/*
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
*/
}
