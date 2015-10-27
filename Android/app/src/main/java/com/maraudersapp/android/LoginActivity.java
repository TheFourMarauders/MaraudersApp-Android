package com.maraudersapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;

import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by brent on 10/8/15.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    boolean mustNullify = false;
    boolean isInitial = true;
    boolean isPreferencesNull = false;
    private ServerComm remote;
    private SharedPrefsUserAccessor storage;

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
        storage = new SharedPrefsUserAccessor(getApplicationContext());
        if(mustNullify) {
            storage.clearCredentials();
            isInitial = false;
        }
        isPreferencesNull = storage.isCredentialsNull();
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

        remote = ServerCommManager.getCommForContext(getApplicationContext());
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        storage.putCredentials(username, password);

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
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onLoginFailed() {
        storage.clearCredentials();
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

    public void checkLogin() {
        System.out.println("Checking");
        String username = storage.getUsername();
        String password = storage.getPassword();
        usernameText.setText(username, TextView.BufferType.EDITABLE);
        passwordText.setText(password, TextView.BufferType.EDITABLE);
        login();
    }
}
