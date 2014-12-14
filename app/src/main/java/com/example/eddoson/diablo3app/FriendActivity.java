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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FriendActivity extends ActionBarActivity
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
        currentUser = ParseUser.getCurrentUser();

        //pull friends array from this user's friends column
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>()
        {
            public void done(List<ParseUser> objects, ParseException e)
            {
                if (e == null)
                {
                    // The query was successful.
                    //pull the first parseuser from objects list
                    ParseUser newUser = objects.get(0);

                    //pull list of friends from parse
                    ArrayList<Object> testArray = (ArrayList<Object>) newUser.getList("friends");
                    for (Object obj : testArray)
                    {
                        //make a new friend object for each incoming friend name
                        Friend newFriend = new Friend((String) obj);

                        //populate the list of friends with newly created friend objects
                        listFriends.add(newFriend);
                    }
                    //redraw the listview
                    updateList();

                }
                else
                {
                    // Something went wrong.
                    Toast.makeText(FriendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //add button on click logic
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!etFriendText.getText().toString().isEmpty())
                {
                    //pull username from edittext then wrap in Friend object
                    String newUsername = etFriendText.getText().toString();
                    Friend newFriend = new Friend(newUsername);

                    //update the list of friends with the new friend
                    listFriends.add(newFriend);

                    //redraw the listview
                    updateList();
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
                        //reflect changes onto parse
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("username", currentUser.getUsername());
                        query.findInBackground(new FindCallback<ParseUser>()
                        {
                            public void done(List<ParseUser> objects, ParseException e)
                            {
                                if (e != null)
                                {
                                    //Something went wrong.
                                    Toast.makeText(FriendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //thanks to jared314 for error checking strategy
                                    return;
                                }

                                //pull the parseuser from the returning list
                                ParseUser user = objects.get(0);

                                //pull the arraylist of friends from parse, update the list, upload back to parse
                                ArrayList<Object> parseFriendArray = (ArrayList<Object>) user.getList("friends");

                                //remove the clicked friend from the arraylist returned from parse
                                int index = parseFriendArray.indexOf(listFriends.get(position).getUsername());
                                //remove the friend we clicked on
                                parseFriendArray.remove(index);

                                //add the new arraylist to the parseuser and save
                                //thanks to Hgoale for idea to use put instead of add
                                user.put("friends", parseFriendArray);
                                user.saveInBackground(new SaveCallback()
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
