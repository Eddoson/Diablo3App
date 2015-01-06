package com.example.eddoson.diablo3app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    EditText etSearchBar;
    Button btnConfirmSearch;
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
        etSearchBar = (EditText) findViewById(R.id.editTextSearch);
        btnConfirmSearch = (Button) findViewById(R.id.buttonConfirmSearch);

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
                String username, bnetUsername;
                int highscore;

                //loop through the incoming list of info
                for (ParseUser parseFriend : parseObjects)
                {
                    //create a friend object out of the info
                    Friend newFriend = Friend.fromParseUser(parseFriend);

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

        //confirm button logic
        btnConfirmSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String searchedName = etSearchBar.getText().toString();
                Friend searchFriend = Friend.fromParseUsername(searchedName);

                if (friendList.indexOf(searchFriend) == -1)
                {
                    //friend not found
                    Toast.makeText(LeaderboardActivity.this, String.format("%s not found!", searchedName), Toast.LENGTH_SHORT).show();
                    return;
                }

                //get the index for the searched friend in the master friend list
                int foundFriendIndex = friendList.indexOf(searchFriend);

                //pull the friend object from the master friend list
                searchFriend = friendList.get(foundFriendIndex);

                //create an alert dialog to display the friend's information
                AlertDialog.Builder adSearchInfoBuilder = new AlertDialog.Builder(LeaderboardActivity.this);
                adSearchInfoBuilder.setTitle("Search");
                adSearchInfoBuilder.setMessage(String.format("Username: %s \nBattlenet Tag: %s \nHighscore: %s \nRank: %s", searchFriend.getUsername(), searchFriend.getBnetUsername(), Integer.toString(searchFriend.getHighscore()), Integer.toString(foundFriendIndex + 1)));
                adSearchInfoBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                //show the dialog
                adSearchInfoBuilder.create().show();
            }
        });

        //on item click logic for listview
        lvLeaderboard.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //pull the friend that was clicked on
                Friend clickedFriend = friendList.get(position);

                //package friend into intent
                Intent intent = new Intent(LeaderboardActivity.this, CharacterListActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, clickedFriend);

                //go to character list for that friend
                startActivity(intent);
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
