package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles all registering related activities
 * @author Ed Sutton
 */
public class RegisterActivity extends ActionBarActivity implements iBattleNetJSONInterface
{
    Button btnDone;
    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    EditText etBnetUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //connect logic to views on UI
        btnDone = (Button) findViewById(R.id.buttonDone);
        etUsername = (EditText) findViewById(R.id.editTextRegisterUsername);
        etPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        etConfirmPassword = (EditText) findViewById(R.id.editTextRegisterConfirmPassword);
        etBnetUsername = (EditText) findViewById(R.id.editTextBnetUsername);

        //done button logic
        btnDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty() || etConfirmPassword.getText().toString().isEmpty() || etBnetUsername.getText().toString().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please fill in all boxes before continuing!", Toast.LENGTH_LONG).show();
                    return;
                }

                String bnetUsername = etBnetUsername.getText().toString();
                new BattleNetAPIHandler(RegisterActivity.this).execute(MainActivity.MAIN_API_URL + bnetUsername + "/");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateJSONObject(JSONObject root) throws JSONException
    {
        //check for bad username
        if (!root.isNull("code"))
        {
            //bad username, try again
            Toast.makeText(RegisterActivity.this, "Battlenet username not found! Try again with username-number", Toast.LENGTH_SHORT).show();
            return;
        }

        //pull out edit text info, changing usernames to initial caps
        //thanks to Trystan34!!
        String username = etUsername.getText().toString();
        username = username.substring(0, 1).toUpperCase() + username.substring(1);
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        //this will be our new user
        ParseUser user = new ParseUser();
        user.setUsername(username);

        if (password.equals(confirmPassword))
        {
            //only if both passwords match
            user.setPassword(password);
        }
        else
        {
            //passwords didn't match
            Toast.makeText(RegisterActivity.this, "The passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.put("bnetUsername", etBnetUsername.getText().toString());
        user.signUpInBackground(new SignUpCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null)
                {
                    //bad
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //everything worked! yay!
                Toast.makeText(RegisterActivity.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
