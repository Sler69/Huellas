package com.example.huellas.ui.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huellas.data.API;
import com.example.huellas.data.RetrofitClient;
import com.example.huellas.R;
import com.example.huellas.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameText;
    private EditText mailText;
    private EditText passwordText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nameText = findViewById(R.id.newUser);
        passwordText = findViewById(R.id.newPass);
        mailText = findViewById(R.id.newEmail);
        signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(view);

            }
        });
    }


    public void attemptRegister(View view) {
        /*
         */
        mailText.setError(null);
        passwordText.setError(null);
        nameText.setError(null);

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            API service = RetrofitClient.getRetrofit().create(API.class);

            Call<ResponseBody> call = service.register(nameText.getText().toString(), mailText.getText().toString(), passwordText.getText().toString());
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
                            System.out.println("Response: " + s);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(s != null){
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String token = jsonObject.getString("token");

                            PreferenceUtil.saveUser(token,SignUpActivity.this);
                            Intent intent = new Intent(SignUpActivity.this, PrincipalActivity.class);
                            startActivity(intent);

                            System.out.println(token);

                        } catch (JSONException e) {
                            e.printStackTrace();

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(s);
                                String message = jsonObject.getString("message");

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
                                builder1.setMessage(message);
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });


                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

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
}
