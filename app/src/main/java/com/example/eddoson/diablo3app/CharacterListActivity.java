package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CharacterListActivity extends ActionBarActivity
{
    TextView tvUsername;
    TextView tvParagon;
    ListView lvCharacters;
    Friend currentFriend;
    CharacterAdapter adapter;
    List<Character> characterList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        if (getIntent().getExtras() == null)
        {
            //bad, bundle was empty
            Toast.makeText(CharacterListActivity.this, "Could not load bundle", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CharacterListActivity.this, MainActivity.class));
        }

        //initialize the incoming Friend
        currentFriend = (Friend) getIntent().getExtras().get(MainActivity.FRIEND_KEY);

        //initialize some variables
        characterList = new ArrayList<>();
        characterList.add(new Character("Odell", "Wizard"));
        characterList.add(new Character("Tim", "Barbarian"));
        characterList.add(new Character("Ray", "Demon Hunter"));

        //connect logic to UI components
        tvUsername = (TextView) findViewById(R.id.textViewUsername);
        tvParagon = (TextView) findViewById(R.id.textViewParagon);
        lvCharacters = (ListView) findViewById(R.id.listViewCharacterList);

        //set text for our text views
        tvUsername.setText("User: " + currentFriend.getUsername());
        tvParagon.setText("Paragon: " + currentFriend.getParagon());

        adapter = new CharacterAdapter(CharacterListActivity.this, R.layout.character_list_component, characterList);
        lvCharacters.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_character_list, menu);
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
