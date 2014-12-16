package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity
{
    Button btnFriends;
    Button btnCharacters;
    Button btnGame;
    Button btnLogout;
    TextView tvLoggedInAs;
    static String FRIEND_KEY = "friend";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check to see if there is a current user
        if (ParseUser.getCurrentUser() == null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        //connect logic to the UI components
        btnFriends = (Button) findViewById(R.id.buttonFriends);
        btnLogout = (Button) findViewById(R.id.buttonLogout);
        btnGame = (Button) findViewById(R.id.buttonGame);
        tvLoggedInAs = (TextView) findViewById(R.id.textViewLoggedInAs);


        //friend button logic
        btnFriends.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, FriendActivity.class));
            }
        });

        //set loggedinas to the current username
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null)
        {
            //if we are here then there is a current user
            tvLoggedInAs.setText("Logged in as: " + currentUser.getUsername());
        }
        else
        {
            //somehow the user got to the main menu without logging in
            tvLoggedInAs.setText("Not logged in!!");
        }

        //logout button logic
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        //game button logic
        btnGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //go to the game menu
                startActivity(new Intent(MainActivity.this, GameMenuActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
