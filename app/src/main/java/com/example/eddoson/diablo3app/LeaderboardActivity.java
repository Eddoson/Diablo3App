package com.example.eddoson.diablo3app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardActivity extends ActionBarActivity
{
    TextView tvUsernameandRank;
    ListView lvLeaderboard;
    List<Friend> friendList;
    LeaderboardAdapter adapter;
    Friend currentFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        //connect UI components with logic
        tvUsernameandRank = (TextView) findViewById(R.id.textViewUsernameAndRank);
        lvLeaderboard = (ListView) findViewById(R.id.listViewLeaderboard);

        //initialize
        friendList = new ArrayList<>();

        if (getIntent().getExtras() != null)
        {
            //receive incoming friend
            currentFriend = (Friend) getIntent().getExtras().get(MainActivity.FRIEND_KEY);
        }

        //create a new query in ascending order by highscore
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByDescending("highscore");
        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> parseObjects, ParseException e)
            {
                if (e != null)
                {
                    //there's a problem
                    Toast.makeText(LeaderboardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //initialize temporary strings
                String username;
                int highscore;

                Log.d("friend", "about to enter for loop: " + parseObjects.toString());
                //loop through the incoming list of info
                for (ParseUser object : parseObjects)
                {

                    //create a friend object out of the info
                    username = object.getUsername();
                    highscore = object.getInt("highscore");
                    Friend newFriend = new Friend(username, highscore);
                    Log.d("friend", newFriend.toString());

                    //add that friend object to a list
                    friendList.add(newFriend);
                }

                //find this user inside the friendlist
                int rank = friendList.indexOf(currentFriend) + 1;
                tvUsernameandRank.setText(String.format("User: %s|Rank: %s", currentFriend.getUsername(), Integer.toString(rank)));

                //now the friendlist is populated, use the adapter and draw to listview
                adapter = new LeaderboardAdapter(LeaderboardActivity.this, R.layout.leaderboard_list_component, friendList);
                lvLeaderboard.setAdapter(adapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_leaderboard, menu);
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
