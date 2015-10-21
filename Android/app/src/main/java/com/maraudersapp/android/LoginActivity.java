package com.maraudersapp.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.net.HttpCallback;
import com.maraudersapp.android.net.HttpPostPutTask;
import com.maraudersapp.android.net.methods.post_put.PutUserNamePass;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommFactory;

import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by brent on 10/8/15.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_PREFS_NAME = "LoginPrefsFile";
    private static final int REQUEST_SIGNUP = 0;
    boolean mustNullify = false;
    boolean isInitial = true;
    boolean isPreferencesNull = false;
    ServerComm remote;

    @InjectView(R.id.input_username_login) EditText usernameText;
    @InjectView(R.id.input_password_login) EditText passwordText;
    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.link_signup) TextView signUp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        Intent i = getIntent();
        mustNullify = i.getBooleanExtra("nullify", false);
        if(mustNullify) {
            nullifyPreferences();
            isInitial = false;
        }
        isPreferencesNull = checkIfNullPrefValues();
        if(isInitial && !isPreferencesNull) {
            checkLogin();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        remote = new ServerCommFactory().build(getApplicationContext());
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        putCredentials();

        // TODO: Authentication login

        Log.d("LOGIN", "Checking login");
        remote.getFriendsFor(username, new RemoteCallback<Set<UserInfo>>() {
            @Override
            public void onSuccess(Set<UserInfo> response) {
                Log.d("LOGIN_SUCCESS", response.toString());
                onLoginSuccess();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                Log.d("LOGIN_FAILURE", errorCode + message);
                onLoginFailed();
            }
        });

        //onLoginSuccess(); //Put in handle success
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Signup
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        if(!mustNullify || !isInitial) {
            Toast.makeText(getBaseContext(), "Login Successful!", Toast.LENGTH_LONG).show();
        }
        loginButton.setEnabled(true);
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void onLoginFailed() {
        nullifyPreferences();
        if(!mustNullify || !isInitial) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        }
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            if(!mustNullify || !isInitial) {
                usernameText.setError("At least 3 characters");
            }
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            if(!mustNullify || !isInitial) {
                passwordText.setError("between 4 and 10 alphanumeric characters");
            }
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void putCredentials() {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("username", usernameText.getText().toString());
        editor.putString("password", passwordText.getText().toString());
        editor.commit();
    }

    public void checkLogin() {
        System.out.println("Checking");
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE);
        if (prefs != null) {
            String username = prefs.getString("username", null);
            String password = prefs.getString("password", null);
            usernameText.setText(username, TextView.BufferType.EDITABLE);
            passwordText.setText(password, TextView.BufferType.EDITABLE);
            login();
        }
    }

    public void nullifyPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("username", null);
        editor.putString("password", null);
        editor.commit();
    }

    public boolean checkIfNullPrefValues() {
        return getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE).getString("username",null) == null;
    }
}
