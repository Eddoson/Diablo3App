package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles logic backend for the character list activity
 * @author Ed Sutton
 */
public class CharacterListActivity extends ActionBarActivity implements iBattleNetJSONInterface
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

        //trigger api handler to start
        new BattleNetAPIHandler(CharacterListActivity.this).execute(MainActivity.MAIN_API_URL + currentFriend.getBnetUsername() + "/");

        //initialize some variables
        characterList = new ArrayList<>();

        //connect logic to UI components
        tvUsername = (TextView) findViewById(R.id.textViewUsername);
        tvParagon = (TextView) findViewById(R.id.textViewParagon);
        lvCharacters = (ListView) findViewById(R.id.listViewCharacterList);

        //on item click logic for listview
        lvCharacters.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //pull the character clicked on from the list
                Character transferCharacter = characterList.get(position);

                //load intent with friend and character information in it
                Intent intent = new Intent(CharacterListActivity.this, CharacterSheetActivity.class);
                intent.putExtra(MainActivity.FRIEND_KEY, currentFriend);
                intent.putExtra(MainActivity.CHARACTER_KEY, transferCharacter);

                //go
                startActivity(intent);
            }
        });

        //set text for our text views
        tvUsername.setText("Account: " + currentFriend.getBnetUsername());
        tvParagon.setText("Paragon: " + currentFriend.getParagon());
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

    /**
     * Callback from the BattleNetAPIHandler with JSONObject data
     * @param root
     * @throws JSONException
     */
    @Override
    public void onUpdateJSONObject(JSONObject root) throws JSONException
    {
        //these will be used to store temporary information later
        String name = "Empty";
        String characterClass = "Empty";
        String id = "Empty";

        //this is the array of characters/heroes from the api
        JSONArray heroesArray = root.getJSONArray("heroes");

        //iterate through the array of heroes and make a list of characters
        for (int i = 0; i < heroesArray.length(); i++)
        {
            //pull the hero object from array
            JSONObject heroObject = (JSONObject) heroesArray.get(i);

            //pull name and class and ID from object, create Character object
            name = heroObject.getString("name");
            characterClass = heroObject.getString("class");
            id = heroObject.getString("id");

            //initial caps the class
            characterClass = characterClass.substring(0, 1).toUpperCase() + characterClass.substring(1);
            Character newCharacter = new Character(name, characterClass, id);

            //add the new character to the list
            characterList.add(newCharacter);
        }

        //redraw the UI with the new list
        updateList();

        //update paragon level at the top of the screen
        currentFriend.setParagon(root.getString("paragonLevel"));
        tvParagon.setText("Paragon: " + currentFriend.getParagon());


    }

    /**
     * Updates UI with new character list
     */
    private void updateList()
    {
        //update the UI using the new character list
        adapter = new CharacterAdapter(CharacterListActivity.this, R.layout.character_list_component, characterList);
        lvCharacters.setAdapter(adapter);
    }
}
