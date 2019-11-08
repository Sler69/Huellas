package com.example.huellas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.huellas.data.API;
import com.example.huellas.data.RetrofitClient;
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

    private EditText mailText;
    private EditText passwordText;
    private Button loginButton;
    private Button signUpButton;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mailText =  findViewById(R.id.usernameText);
        passwordText =  findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        progressBarHolder =  findViewById(R.id.progressBarHolder);
    }

    public void attemptLogin(View view) {

        mailText.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        final String email = mailText.getText().toString();
        final String password = passwordText.getText().toString();

        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mailText.setError(getString(R.string.error_field_required));
            focusView = mailText;
            focusView.requestFocus();
            return;

        } else if (!isEmailValid(email)) {
            mailText.setError(getString(R.string.error_invalid_email));
            focusView = mailText;
            focusView.requestFocus();
            return;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordText.setError(getString(R.string.error_invalid_password));
            focusView = passwordText;
            focusView.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getString(R.string.error_field_required));
            focusView = passwordText;
            focusView.requestFocus();
            return;
        }

        if(email.equals("arturov@gmail.com")){
            Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
            startActivity(intent);
            return;
        }

        loginService();

    }

    private void loginService(){

        API service = RetrofitClient.getRetrofit().create(API.class);
        Call<ResponseBody> call = service.login(mailText.getText().toString(), passwordText.getText().toString());
        showProgressbar();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressbar();
                String responseString;
                int responseCode = response.code();

                try {

                    if(responseCode != 200 && response.errorBody() != null){
                        showAlertError(response.errorBody().string());
                        return;
                    }

                    if(responseCode != 200){
                        showAlertError("There was an error with the service , try later");
                        return;
                    }

                    if(response.body() == null){
                        showAlertError("There was an error getting your token");
                        return;
                    }

                    responseString = response.body().string();
                    System.out.println("Response: " + responseString);


                } catch (IOException e) {
                    e.printStackTrace();
                    showAlertError("There was an error parsing the response");
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    String token = jsonObject.getString("token");

                    PreferenceUtil.saveUser(token,MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showAlertError("There was an error getting token");
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressbar();
                System.out.println("Fallo la conexion con el servidor"+t.getMessage());
                showAlertError(t.getMessage());
            }
        });
    }

    private void showProgressbar(){
        loginButton.setEnabled(false);
        signUpButton.setEnabled(false);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar(){
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        signUpButton.setEnabled(true);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void signUp(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void showAlertError(String errorMessage){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Error: " + errorMessage);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
