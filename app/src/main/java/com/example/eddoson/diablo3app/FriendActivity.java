package com.example.eddoson.diablo3app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Ed Sutton
 */
public class FriendActivity extends ActionBarActivity implements iBattleNetJSONInterface
{
    Button btnAdd;
    ListView lvFriends;
    final ArrayList<Friend> listFriends = new ArrayList<>();
    EditText etFriendText;
    FriendAdapter adapter;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //connecting logic to UI components
        btnAdd = (Button) findViewById(R.id.buttonAdd);
        lvFriends = (ListView) findViewById(R.id.listViewFriends);
        etFriendText = (EditText) findViewById(R.id.editTextFriend);

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
            Log.d("friendsList", "friends column was NOT null");
            //root json array of friend json objects
            JSONArray friendsJSONArray = currentUser.getJSONArray("friends");
            Log.d("friendsList", friendsJSONArray.toString());
            //iterate through the friends json array
            for (int i = 0; i < friendsJSONArray.length(); i++)
            {
                try
                {
                    //convert each json object into a friend object then add to friends list
                    JSONObject jsonFriend = friendsJSONArray.getJSONObject(i);
                    Friend newFriend = Friend.fromJSONObject(jsonFriend);
                    Log.d("friendsList", newFriend.toString());
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
                    new BattleNetAPIHandler(FriendActivity.this).execute(MainActivity.MAIN_API_URL + etFriendText.getText().toString() + "/");
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
                        ExtendedJSONArray parseFriendArray = (ExtendedJSONArray) currentUser.getJSONArray("friends");

                        //remove the friend object in question from this local array
                        try
                        {
                            parseFriendArray.removeFromJSONArray(position);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            return;
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
        if (!listFriends.isEmpty())
        {
            //run adapter
            adapter = new FriendAdapter(FriendActivity.this, R.layout.friend_list_component, listFriends);

            //attach adapter to listview
            lvFriends.setAdapter(adapter);
        }
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        //pull username from edittext then wrap in Friend object
        String newUsername = etFriendText.getText().toString();
        final Friend newFriend = new Friend(newUsername);

        //add the new username to the array in the friends column
        currentUser.add("friends", newUsername);
        currentUser.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                //if we are here, everything parse-wise went well.
                //update the list of friends with the new friend
                listFriends.add(newFriend);

                //redraw the listview
                updateList();
            }
        });
    }
}
