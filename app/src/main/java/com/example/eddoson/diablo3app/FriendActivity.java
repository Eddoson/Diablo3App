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
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ed Sutton
 */
public class FriendActivity extends ActionBarActivity implements iBattleNetJSONInterface
{
    Button btnAdd;
    ListView lvFriends;
    final ArrayList<Friend> listFriends = new ArrayList<>();
    final ArrayList<Friend> listSearchFriends = new ArrayList<>();
    EditText etFriendText;
    FriendAdapter adapter, searchFriendAdapter;
    ParseUser currentUser;
    ListPopupWindow lpwFriendChoiceDropDown;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //connecting logic to UI components
        btnAdd = (Button) findViewById(R.id.buttonAdd);
        lvFriends = (ListView) findViewById(R.id.listViewFriends);
        etFriendText = (EditText) findViewById(R.id.editTextFriend);
        lpwFriendChoiceDropDown = new ListPopupWindow(FriendActivity.this);

        //initiate other things
        try
        {
            //get fresh copy of currentUser
            //thanks to jared314 for .fetch() idea
            currentUser = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        //if testArray is null, the user has no friends. :(
        if (currentUser.getJSONArray("friends") != null)
        {
            //root json array of friend json objects
            JSONArray friendsJSONArray = currentUser.getJSONArray("friends");

            //iterate through the friends json array
            for (int i = 0; i < friendsJSONArray.length(); i++)
            {
                try
                {
                    //convert each json object into a friend object then add to friends list
                    JSONObject jsonFriend = friendsJSONArray.getJSONObject(i);
                    Friend newFriend = Friend.fromJSONObject(jsonFriend);

                    listFriends.add(newFriend);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            //redraw the listview
            updateList();
        }

        //add button on click logic
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!etFriendText.getText().toString().isEmpty())
                {
                    new BattleNetAPIHandler(FriendActivity.this).execute(MainActivity.MAIN_API_URL + etFriendText.getText().toString().replaceAll("#", "-") + "/");
                }
            }
        });

        //listview on long click logic
        lvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                //alert dialog builder for confirming long click
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(FriendActivity.this);
                adBuilder.setTitle("Are you sure?");
                adBuilder.setMessage("You are about to delete a friend from your friends list. Are you sure you want to do this.");
                adBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //pull the jsonarray from the friends column
                        ExtendedJSONArray jsonArrayRemover = new ExtendedJSONArray();
                        JSONArray parseFriendArray = currentUser.getJSONArray("friends");

                        try
                        {
                            parseFriendArray = jsonArrayRemover.removeFromJSONArray(parseFriendArray, position);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        //update the new jsonarray to parse
                        currentUser.put("friends", parseFriendArray);
                        currentUser.saveInBackground(new SaveCallback()
                        {
                            @Override
                            public void done(ParseException e)
                            {
                                if (e != null)
                                {
                                    //there was a problem
                                    Toast.makeText(FriendActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                    return;
                                }

                                //remove the friend at the position we clicked
                                listFriends.remove(position);

                                //redraw the new list of friends to the screen
                                updateList();
                            }
                        });
                    }
                });
                adBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //dismiss the dialog, the user dun goofed
                        dialog.dismiss();
                    }
                });

                //builder is done, create the alertdialog
                AlertDialog alertDialog = adBuilder.create();
                //show the alertdialog
                alertDialog.show();

                return true;
            }
        });

        //item click logic
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //pull the friend we clicked on
                Friend thisFriend = listFriends.get(position);

                //create an intent and bundle the friend info
                Intent intent = new Intent(FriendActivity.this, CharacterListActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, thisFriend);

                //go
                startActivity(intent);
            }
        });
    }

    private void updateList()
    {
        //run adapter
        adapter = new FriendAdapter(FriendActivity.this, R.layout.friend_list_component, listFriends);

        //attach adapter to listview
        lvFriends.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend, menu);
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
        if (id == R.id.action_main_menu)
        {
            //if user wants to go to main menu, start the activity
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.action_logout)
        {
            //log the user out from parse
            ParseUser.logOut();

            //go to the login activity
            startActivity(new Intent(this, LoginActivity.class));

            //destroy this instance of thise activity
            finish();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFriendToParse(final Friend thisFriend) throws JSONException
    {
        //jsonify the friend object so that it melds with parse
        JSONObject jsonFriend = thisFriend.toJSONObject();

        //add the new username to the array in the friends column
        currentUser.add("friends", jsonFriend);
        currentUser.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null)
                {
                    //bad
                    Toast.makeText(FriendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //if we are here, everything parse-wise went well.
                //update the list of friends with the new friend
                listFriends.add(thisFriend);

                //redraw the listview
                updateList();
            }
        });
    }

    /**
     * Callback for the initial friend list loading/retrieving
     *
     * @param root
     * @throws JSONException
     */
    @Override
    public void onUpdateJSONObject(JSONObject root) throws JSONException
    {
        //check for bad username
        if (!root.isNull("code"))
        {
            //bad username, try again
            Toast.makeText(FriendActivity.this, "Username not found! Try again with username-number", Toast.LENGTH_SHORT).show();
            return;
        }

        //we will use these later to create our friend object
        final String bnetUsername = etFriendText.getText().toString().replaceAll("#", "-");
        final Friend[] chosenFriend = new Friend[1];

        //reset the search list in case user has searched this already
        listSearchFriends.clear();

        //to store paragon later
        final String paragon = root.getString("paragonLevel");

        //create a query to search for all rows containing this bnet username
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("bnetUsername", bnetUsername);
        query.findInBackground(new FindCallback<ParseUser>()
        {
            public void done(List<ParseUser> usersList, ParseException e)
            {
                if (e != null)
                {
                    //bad
                    Toast.makeText(FriendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (usersList.isEmpty())
                {
                    //the searched friend has not registered to our app
                    chosenFriend[0] = new Friend(bnetUsername);
                    chosenFriend[0].setParagon(paragon);

                    try
                    {
                        addFriendToParse(chosenFriend[0]);
                    } catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                //if userlist is 1 long, there is only 1 choice for parse usernames
                if (usersList.size() == 1)
                {
                    //this is the friend we want to add to parse
                    chosenFriend[0] = Friend.fromParseUser(usersList.get(0));
                    chosenFriend[0].setParagon(paragon);

                    try
                    {
                        //attempt to add friend to parse
                        addFriendToParse(chosenFriend[0]);
                    } catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }
                    return;
                }
                //create a dropdown with parseUsername choices for the user
                else
                {
                    for (ParseUser pUser : usersList)
                    {
                        //convert parseusers into friends then add to search friend list
                        listSearchFriends.add(Friend.fromParseUser(pUser));
                    }

                    //creating an adapter to populate a list of friends
                    searchFriendAdapter = new FriendAdapter(FriendActivity.this, R.layout.friend_list_component, listSearchFriends);

                    //create a drop down list view so that the user can choose which parse user is their friend
                    lpwFriendChoiceDropDown.setAnchorView(etFriendText);
                    lpwFriendChoiceDropDown.setAdapter(searchFriendAdapter);
                    lpwFriendChoiceDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            //the user has selected who their actual friend is
                            chosenFriend[0] = listSearchFriends.get(position);

                            if (listFriends.contains(chosenFriend[0]))
                            {
                                //this friend is already on the user's friends list!
                                Toast.makeText(FriendActivity.this, "This is already your friend!", Toast.LENGTH_SHORT).show();
                                lpwFriendChoiceDropDown.dismiss();

                                return;
                            }

                            //set the paragon for this friend object
                            chosenFriend[0].setParagon(paragon);

                            try
                            {
                                //attempt to upload this friend to parse
                                addFriendToParse(chosenFriend[0]);
                            } catch (JSONException e1)
                            {
                                e1.printStackTrace();
                            }
                            //a friend was chosen, dismiss the dropdown
                            lpwFriendChoiceDropDown.dismiss();
                        }
                    });
                    lpwFriendChoiceDropDown.show();
                }
            }
        });

    }
}
