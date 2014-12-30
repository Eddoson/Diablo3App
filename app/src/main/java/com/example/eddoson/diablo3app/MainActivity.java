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

/**
 * Main thread/activity for the app. Handles the main menu interactions
 * @author Ed Sutton
 */
public class MainActivity extends ActionBarActivity
{
    Button btnFriends;
    Button btnCharacters;
    Button btnGame;
    Button btnLogout;
    TextView tvLoggedInAs;
    ParseUser currentUser;
    static String FRIEND_KEY = "friend";
    static String CHARACTER_KEY = "character";
    static String MAIN_API_URL = "http://us.battle.net/api/d3/profile/";
    static String DETAILED_ITEM_API_URL = "http://us.battle.net/api/d3/data/";
    static String IMAGE_URL = "http://media.blizzard.com/d3/icons/items/large/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect logic to the UI components
        btnFriends = (Button) findViewById(R.id.buttonFriends);
        btnLogout = (Button) findViewById(R.id.buttonLogout);
        btnGame = (Button) findViewById(R.id.buttonGame);
        btnCharacters = (Button) findViewById(R.id.buttonCharacters);
        tvLoggedInAs = (TextView) findViewById(R.id.textViewLoggedInAs);

        //initialize
        currentUser = ParseUser.getCurrentUser();

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
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null)
        {
            //if we are here then there is a current user
            String username = currentUser.getUsername();
            tvLoggedInAs.setText("Logged in as: " + username);
        }
        else
        {
            //somehow the user got to the main menu without logging in
            tvLoggedInAs.setText("Not logged in!!");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

        //character list button logic
        btnCharacters.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //put ourselves into a Friend object
                Friend newFriend = new Friend((String) currentUser.get("bnetUsername"));

                //create intent, put Friend object into it, start character list activity
                Intent intent = new Intent(MainActivity.this, CharacterListActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, newFriend);
                startActivity(intent);
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
