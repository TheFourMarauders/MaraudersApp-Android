package com.maraudersapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by brent on 10/8/15.
 */
public class SignUpActivity extends AppCompatActivity{

    @InjectView(R.id.input_firstName_signup) EditText firstNameText;
    @InjectView(R.id.input_lastName_signup) EditText lastNameText;
    @InjectView(R.id.input_username_signup) EditText usernameText;
    @InjectView(R.id.input_password_signup) EditText passwordText;
    @InjectView(R.id.signup_button) Button signUpButton;
    @InjectView(R.id.link_login) TextView login;

    private SharedPrefsUserAccessor storage;
    private ServerComm remote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        storage = new SharedPrefsUserAccessor(getApplicationContext());
        remote = ServerCommManager.getCommForContext(getApplicationContext());

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

    @Override
    public void onResume() {
        super.onResume();
        remote = ServerCommManager.getCommForContext(getApplicationContext());
        storage = new SharedPrefsUserAccessor(getApplicationContext());
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed("Invalid");
            return;
        }

        signUpButton.setEnabled(false);

        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        UserCreationInfo creationReq = new UserCreationInfo(username, password, firstName, lastName);

        remote.createUser(creationReq, new RemoteCallback<String>() {
            @Override
            public void onSuccess(String response) {
                storage.putCredentials(username, password);
                onSignupSuccess();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                onSignupFailed(errorCode + ": " + message);
            }
        });
    }


    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Signup Successful!", Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        if (firstName.isEmpty() || firstName.length() <= 0) {
            firstNameText.setError("Enter your name so friends can find you!");
            valid = false;
        } else {
            firstNameText.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() <= 0) {
            lastNameText.setError("Enter your name so friends can find you!");
            valid = false;
        } else {
            lastNameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 3) {
            usernameText.setError("At least 3 characters");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
