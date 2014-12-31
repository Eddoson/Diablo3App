package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

/**
 * @author Ed Sutton
 */
public class GameMenuActivity extends ActionBarActivity
{
    Button btnLearning;
    Button btnRanked;
    Button btnLeaderboard;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        //connect logic to UI components
        btnLearning = (Button) findViewById(R.id.buttonLearningMode);
        btnRanked= (Button) findViewById(R.id.buttonRankedMode);
        btnLeaderboard = (Button) findViewById(R.id.buttonLeaderboard);

        //initialize
        currentUser = ParseUser.getCurrentUser();

        //learning mode button logic
        btnLearning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //go to learning mode
                Intent intent = new Intent(GameMenuActivity.this, GameActivity.class);
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
                Intent intent = new Intent(GameMenuActivity.this, GameActivity.class);
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
                Intent intent = new Intent(GameMenuActivity.this, LeaderboardActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, thisFriend);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_menu, menu);
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
