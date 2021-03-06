package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity
{
    Button btnLogin;
    Button btnRegister;
    TextView tvTitle;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //connecting UI components to backend logic
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnRegister = (Button) findViewById(R.id.buttonRegister);
        tvTitle = (TextView) findViewById(R.id.textViewTitle);
        etUsername = (EditText) findViewById(R.id.editTextLoginUsername);
        etPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        //login button onclick logic
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //pull username/password info and initial caps
                String username = etUsername.getText().toString();
                username = username.substring(0, 1).toUpperCase() + username.substring(1);
                String password = etPassword.getText().toString();

                //login using the info provided in our edit texts
                ParseUser.logInInBackground(username, password, new LogInCallback()
                {
                    @Override
                    public void done(ParseUser parseUser, ParseException e)
                    {
                        if (e == null)
                        {
                            //if we're here, everything worked! yay!
                            Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                        {
                            //boo, something broke!
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //go to register activity
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }
}
