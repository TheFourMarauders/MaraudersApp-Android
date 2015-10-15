package com.maraudersapp.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by brent on 10/8/15.
 */
public class SignUpActivity extends AppCompatActivity{

    @InjectView(R.id.input_username_signup) EditText usernameText;
    @InjectView(R.id.input_email_signup) EditText emailText;
    @InjectView(R.id.input_password_signup) EditText passwordText;
    @InjectView(R.id.signup_button) Button signUpButton;
    @InjectView(R.id.link_login) TextView login;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signUpButton.setEnabled(false);

        String name = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Implement signup to server

        onSignupSuccess(); //Put in handle success
  /*
        new HttpPostPutTask(new HttpCallback<String>() {
            @Override
            public void handleSuccess(String s) {
               // Log.i(LocationConstants.LOG_TAG, "Server sent successfully. " + s);
                onLoginSuccess();
            }

            @Override
            public void handleFailure() {
               // Log.i(LocationConstants.LOG_TAG, "Location send not successful. Code: " + responseCode
               //         + ". Message: " + errorMessage);
                onLoginFailed();
            }
        }).execute(new PutUserNamePass(username, password));

     */
    }


    public void onSignupSuccess() {
        putCredentials();
        Toast.makeText(getBaseContext(), "Signup Successful!", Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup Failed", Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            usernameText.setError("At least 3 characters");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void putCredentials() {
        SharedPreferences.Editor editor = getSharedPreferences(LoginActivity.LOGIN_PREFS_NAME, MODE_PRIVATE).edit();
        if (editor != null) {
            editor.putString("username", usernameText.getText().toString());
            editor.putString("password", passwordText.getText().toString());
            editor.commit();
        }
    }
}
