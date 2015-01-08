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
    Button btnLearning;
    Button btnRanked;
    Button btnLeaderboard;
    TextView tvLoggedInAs;
    ParseUser currentUser;
    static String FRIEND_KEY = "friend";
    static String CHARACTER_KEY = "character";
    static String IS_RANKED_MODE_KEY = "isRankedMode";
    static String MAIN_API_URL = "http://us.battle.net/api/d3/profile/";
    static String DETAILED_ITEM_API_URL = "http://us.battle.net/api/d3/data/";
    static String IMAGE_URL = "http://media.blizzard.com/d3/icons/items/large/";

    @Override
    protected void onResume()
    {
        super.onResume();

        //checking to see if user is still logged in
        if (ParseUser.getCurrentUser() == null)
        {
            //no one should be here...
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //connect logic to the UI components
        btnFriends = (Button) findViewById(R.id.buttonFriends);
        btnCharacters = (Button) findViewById(R.id.buttonCharacters);
        btnLeaderboard = (Button) findViewById(R.id.buttonLeaderboard);
        btnLearning = (Button) findViewById(R.id.buttonLearningMode);
        btnRanked = (Button) findViewById(R.id.buttonRankedMode);
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

        //learning mode button logic
        btnLearning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //go to learning mode
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(MainActivity.IS_RANKED_MODE_KEY, false);
                startActivity(intent);
            }
        });

        //ranked mode button logic
        btnRanked.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //go to learning mode
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(MainActivity.IS_RANKED_MODE_KEY, true);
                startActivity(intent);
            }
        });

        //leaderboard button logic
        btnLeaderboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //make a friend object from this user's information
                String username = currentUser.getUsername();
                int highscore = currentUser.getInt("highscore");
                Friend thisFriend = new Friend(username, highscore);

                //package friend object into intent before starting leaderboard activity
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, thisFriend);
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

        if (id == R.id.action_logout)
        {
            //log the user out from parse
            ParseUser.logOut();

            //go to the login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            //destroy this instance of thise activity
            finish();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
