package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ParseUser.getCurrentUser()!=null) {
            goMainActivity();
        }
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Logging in");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username,password);
            }
        });
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Logging in");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username,password);
            }
        });
    }

    private void signupUser(final String username, String password) {
        Log.i(TAG,"Attempt to sign up");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error signing up",e);
                    ParseUser.logOut();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this,"Successful Signup "+username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(final String username, String password) {
        Log.i(TAG,"Attempt to login");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG,"Error logging in",e);
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this,"Successful Login "+username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}