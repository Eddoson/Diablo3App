package com.example.eddoson.diablo3app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Display character's items
 *
 * @author Ed Sutton
 */
public class CharacterSheetActivity extends ActionBarActivity implements iBattleNetJSONInterface
{
    ImageView ivShoulders, ivHead, ivAmulet, ivGloves, ivChest, ivBracers, ivRingL, ivBelt, ivRingR, ivWeaponL, ivBoots, ivWeaponR, ivLegs;
    TextView tvAccountName, tvParagon, tvCharacterName;
    Character currentCharacter;
    Friend currentFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);

        if (getIntent().getExtras() == null)
        {
            //bad, eject! eject!
            Toast.makeText(CharacterSheetActivity.this, "Could not load character information", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CharacterSheetActivity.this, CharacterListActivity.class));
        }

        //connect logic to UI components for imageviews
        ivShoulders = (ImageView) findViewById(R.id.imageViewShoulders);
        ivHead = (ImageView) findViewById(R.id.imageViewHead);
        ivAmulet = (ImageView) findViewById(R.id.imageViewAmulet);
        ivGloves = (ImageView) findViewById(R.id.imageViewGloves);
        ivChest = (ImageView) findViewById(R.id.imageViewChest);
        ivBracers = (ImageView) findViewById(R.id.imageViewBracers);
        ivRingL = (ImageView) findViewById(R.id.imageViewRingL);
        ivRingR = (ImageView) findViewById(R.id.imageViewRingR);
        ivBelt = (ImageView) findViewById(R.id.imageViewBelt);
        ivWeaponL = (ImageView) findViewById(R.id.imageViewWeaponL);
        ivWeaponR = (ImageView) findViewById(R.id.imageViewWeaponR);
        ivBoots = (ImageView) findViewById(R.id.imageViewBoots);
        ivLegs = (ImageView) findViewById(R.id.imageViewLegs);

        //connect logic to UI components for textviews
        tvAccountName = (TextView) findViewById(R.id.textViewCharacterSheetAccount);
        tvCharacterName = (TextView) findViewById(R.id.textViewCharacterSheetCharacterName);
        tvParagon = (TextView) findViewById(R.id.textViewCharacterSheetParagon);

        //pull friend and character info from the intent
        currentFriend = (Friend) getIntent().getExtras().get(MainActivity.FRIEND_KEY);
        currentCharacter = (Character) getIntent().getExtras().get(MainActivity.CHARACTER_KEY);

        //populate the textviews with information from currentFriend and currentCharacter
        tvAccountName.setText("Account: " + currentFriend.getBnetUsername());
        tvParagon.setText("Paragon: " + currentFriend.getParagon());
        tvCharacterName.setText("Character: " + currentCharacter.getName());;

        //save the URL to load this character's JSON info using BattleNetAPIHandler
        String characterURL = MainActivity.MAIN_API_URL + currentFriend.getBnetUsername() + "/hero/" + currentCharacter.getId();
        new BattleNetAPIHandler(CharacterSheetActivity.this).execute(characterURL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_character_sheet, menu);
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

    @Override
    public void onUpdateJSONObject(JSONObject root) throws JSONException
    {
        //pull the items JSON Object from root, pull iterator from that object
        JSONObject itemTypes = root.getJSONObject("items");
        Iterator<String> it = itemTypes.keys();
        HashMap<String, HashMap<String, String>> itemBroadMap = new HashMap<>();

        while (it.hasNext())
        {
            //current key
            String itemTypeLabel = it.next();

            //use current key to pull JSON Object
            JSONObject item = itemTypes.getJSONObject(itemTypeLabel);

            //hashmaps to be saved to our Character object
            HashMap<String, String> itemDetailsMap = new HashMap<>();

            //load the most nested hashmap
            itemDetailsMap.put("name", item.getString("name"));
            itemDetailsMap.put("icon", item.getString("icon"));
            itemDetailsMap.put("tooltipParams", item.getString("tooltipParams"));

            //now that we have the icon, go ahead and load the imageview with it
            loadImage(item.getString("icon"), itemTypeLabel);

            //save detailed hashmap to broad one
            itemBroadMap.put(itemTypeLabel, itemDetailsMap);
        }

        //now load broadmap to the character's object
        currentCharacter.setItems(itemBroadMap);

    }

    private void loadImage(String icon, String itemTypeLabel)
    {
        //store url for later
        String url = "http://media.blizzard.com/d3/icons/items/large/" + icon + ".png";

        //we will replace this imageview with the correct one during switch
        ImageView ivCurrent = null;

        switch (itemTypeLabel)
        {
            case "head":
                ivCurrent = ivHead;
                break;
            case "torso":
                ivCurrent = ivChest;
                break;
            case "feet":
                ivCurrent = ivBoots;
                break;
            case "hands":
                ivCurrent = ivGloves;
                break;
            case "shoulders":
                ivCurrent = ivShoulders;
                break;
            case "legs":
                ivCurrent = ivLegs;
                break;
            case "bracers":
                ivCurrent = ivBracers;
                break;
            case "mainHand":
                ivCurrent = ivWeaponL;
                break;
            case "offHand":
                ivCurrent = ivWeaponR;
                break;
            case "waist":
                ivCurrent = ivBelt;
                break;
            case "rightFinger":
                ivCurrent = ivRingR;
                break;
            case "leftFinger":
                ivCurrent = ivRingL;
                break;
            case "neck":
                ivCurrent = ivAmulet;
                break;
            default:
                Toast.makeText(CharacterSheetActivity.this, "Failed to find item type " + itemTypeLabel, Toast.LENGTH_SHORT).show();
        }

        Picasso.with(CharacterSheetActivity.this).load(url).into(ivCurrent);
    }
}
