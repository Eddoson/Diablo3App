package com.example.eddoson.diablo3app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Display character's items
 *
 * @author Ed Sutton
 */
public class CharacterSheetActivity extends ActionBarActivity implements iBattleNetJSONInterface, View.OnClickListener
{
    ImageView ivShoulders, ivHead, ivAmulet, ivGloves, ivChest, ivBracers, ivRingL, ivBelt, ivRingR, ivWeaponL, ivBoots, ivWeaponR, ivLegs;
    TextView tvAccountName, tvParagon, tvCharacterName;
    Character currentCharacter;
    Friend currentFriend;
    String[] statTypes = {"primary", "secondary", "passive"};
    String[] itemTypeArray = {"shoulders", "head", "neck", "hands", "torso", "bracers", "leftFinger", "rightFinger", "waist", "mainHand", "offHand", "feet", "legs"};

    //number of characters in a line before a new line
    int charLineLimit = 45;

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
        setContentView(R.layout.activity_character_sheet);

        //check screen dimensions to determine character limit for a string
        Point sizePoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(sizePoint);
        if (sizePoint.x <= 1080 && sizePoint.y <= 1800)
        {
            charLineLimit = 25;
        }

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

        //load up an array of these imageviews
        ImageView[] imageViewArray = {ivShoulders, ivHead, ivAmulet, ivGloves, ivChest, ivBracers, ivRingL, ivRingR, ivBelt, ivWeaponL, ivWeaponR, ivBoots, ivLegs};

        //for each imageview in imageviewarray
        for (int i = 0; i < imageViewArray.length; i++)
        {
            //set onclick listener
            imageViewArray[i].setOnClickListener(this);

            //set tag to appropriate item type
            imageViewArray[i].setTag(itemTypeArray[i]);
        }

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
        tvCharacterName.setText("Character: " + currentCharacter.getName());

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
        String url = MainActivity.IMAGE_URL + icon + ".png";

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

        //load the images using their respective image URLs
        Picasso.with(CharacterSheetActivity.this).load(url).into(ivCurrent);
    }

    @Override
    public void onClick(View v)
    {
        //get the tag from the incoming view that was clicked
        String itemType = (String) v.getTag();

        //using the itemtype, pull tooltipParams for that item to use in the URL later
        String tooltipParams = currentCharacter.getItems().get(itemType).get("tooltipParams");

        //concat url for pulling detailed item info
        String url = MainActivity.DETAILED_ITEM_API_URL + tooltipParams;

        //start battlenetapihandler for the url
        DetailedItemInfoCallback callback = new DetailedItemInfoCallback();
        new BattleNetAPIHandler(CharacterSheetActivity.this, callback).execute(url);
    }

    class DetailedItemInfoCallback implements iBattleNetJSONInterface
    {
        @Override
        public void onUpdateJSONObject(JSONObject root) throws JSONException
        {
            //alert dialog and builder for alert dialog to display item info
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(CharacterSheetActivity.this);
            AlertDialog itemDisplayDialog;

            //we will append to these to create the message at the end
            StringBuilder itemName = new StringBuilder();
            StringBuilder armor = new StringBuilder();
            StringBuilder attributes = new StringBuilder();
            StringBuilder gems = new StringBuilder();
            StringBuilder setBonus = new StringBuilder();

            //initialize stringbuilders to display later
            //thanks to navastyles for tip on String.format and Stringbuilder better organizing code!!
            itemName.append(String.format("Name: %s \n", root.getString("name")));
            armor.append("");
            attributes.append("Attributes: \n");
            gems.append("");
            setBonus.append("");

            //!!ARMOR SECTION!!
            //if the armor attribute isn't missing, pull it.
            if (!root.isNull("armor"))
            {
                JSONObject jobArmor = root.getJSONObject("armor");
                armor.append(String.format("Armor: %s \n", jobArmor.getString("min")));
            }

            //!!ATTRIBUTES SECTION!!
            attributes = parseAttributesSection(root, attributes);

            //!!GEM SECTION!!
            gems = parseGemSection(root, gems);

            //!!SET BONUS SECTION!!
            if (!root.isNull("set"))
            {
                setBonus = parseSetBonusSection(root, setBonus);
            }

            //customizing builder
            adBuilder.setMessage(String.format("%s%s%s%s%s", itemName.toString(), armor.toString(), attributes.toString(), gems.toString(), setBonus.toString()));

            //creating alertdialog
            itemDisplayDialog = adBuilder.create();
            itemDisplayDialog.show();
        }

        /**
         * Parses the JSONObject received from Battle.net API to retrieve set bonus information
         * when an item is clicked on
         * @param root
         * @param setBonus
         * @return
         */
        private StringBuilder parseSetBonusSection(JSONObject root, StringBuilder setBonus) throws JSONException
        {
            //set bonus root
            JSONObject jobSetBonuses = root.getJSONObject("set");

            //initialize the set portion to display later with the name of the item
            setBonus.append(String.format("\n\nSet: \n%s", jobSetBonuses.getString("name")));

            //array of set bonuses per rank
            JSONArray jarrayRanks = jobSetBonuses.getJSONArray("ranks");

            //loop through each rank
            for (int i = 0; i < jarrayRanks.length(); i++)
            {
                //this is a rank object
                JSONObject thisRankObj = jarrayRanks.getJSONObject(i);

                //number of pieces to acquire this rank
                String requiredNum = thisRankObj.getString("required");

                //append to master stringbuilder
                setBonus.append(String.format("\n\tPieces: %s", requiredNum));

                for (String statType : statTypes)
                {
                    //json array that stores the bonuses for this rank
                    JSONArray statObject = thisRankObj.getJSONObject("attributes").getJSONArray(statType);

                    for (int j = 0; j < statObject.length(); j++)
                    {
                        JSONObject bonusesObject = statObject.getJSONObject(j);
                        String bonusesText = bonusesObject.getString("text");

                        //append text field to our master stringbuilder
                        setBonus.append(String.format("\n\t\t%s", addLinebreaks(bonusesText, charLineLimit, "\n\t\t")));
                    }

                }
            }

            return setBonus;
        }
        /**
         * Parses the JSONObject received from Battle.net API to retrieve attributes information
         * when an item is clicked on
         * @param root
         * @param attributes
         * @return
         * @throws JSONException
         */
        private StringBuilder parseAttributesSection(JSONObject root, StringBuilder attributes) throws JSONException
        {
            //attributes object,
            JSONObject jobAttributes = root.getJSONObject("attributes");


            //loop through attribute types and append info to attributes stringbuilder
            for (String thisStatType : statTypes)
            {
                //array that holds primary,secondary,passive stats as jsonobjects
                JSONArray specificAttributeArray = jobAttributes.getJSONArray(thisStatType);

                //loop through the attribute objects and pull strings from them
                for (int i = 0; i < specificAttributeArray.length(); i++)
                {
                    //label the primary, secondary, and passive stats
                    if (i == 0)
                    {
                        String displayStatType = "<" + thisStatType.substring(0, 1).toUpperCase() + thisStatType.substring(1) + ">";
                        attributes.append(String.format("\t %s \n", displayStatType));
                    }

                    //get the specific attribute object (primary, secondary, passive)
                    JSONObject attributeTextObject = specificAttributeArray.getJSONObject(i);
                    attributes.append(String.format("\t%s\n", addLinebreaks(attributeTextObject.getString("text"), charLineLimit, "\n\t")));

                    //add an extra newline for prettiness if we're about to move on to the next attribute type
                    if (i == specificAttributeArray.length() - 1)
                    {
                        attributes.append("\n");
                    }
                }
            }
            //remove the new line characters from the end of the string
            attributes.replace(attributes.length() - 2, attributes.length(), "");

            return attributes;
        }
        /**
         * Parses the JSONObject received from Battle.net API to retrieve gem information
         * when an item is clicked on
         * @param root
         * @param gems
         * @return
         * @throws JSONException
         */
        private StringBuilder parseGemSection(JSONObject root, StringBuilder gems) throws JSONException
        {
            //get json array of gems
            JSONArray jobGemArray = root.getJSONArray("gems");

            //if there are gems to display, get ready to display them
            if (jobGemArray.length() > 0)
            {
                gems.append("\n\nGems: \n");
            }

            //loop through each gem and get information
            for (int j = 0; j < jobGemArray.length(); j++)
            {
                //this represents all data of 1 gem
                JSONObject jsonGemObject = jobGemArray.getJSONObject(j);
                //append the name of this gem
                gems.append(String.format("\t%s\n", jsonGemObject.getJSONObject("item").getString("name")));

                //loop through the stat types (primary, secondary, passive) in this gem
                for (String statType : statTypes)
                {
                    //array of various stats for this stat type (primary: +20 str, +40 int, etc)
                    JSONArray statTypeArray = jsonGemObject.getJSONObject("attributes").getJSONArray(statType);
                    for (int i = 0; i < statTypeArray.length(); i++)
                    {
                        //one of the stats for this stat type (the +20 str, or the +40 int, etc)
                        JSONObject statTypeObject = statTypeArray.getJSONObject(i);
                        gems.append(String.format("\t\t%s\n", statTypeObject.getString("text")));

                    }
                }
                if (j == jobGemArray.length() - 1)
                {
                    //remove the new line characters from the end of the string
                    gems.replace(gems.length() - 1, gems.length(), "");
                }
            }
            return gems;
        }

        /**
         * Formatter to straighten text correctly
         * Thanks to EnjoyMikeHunt for the contribution!
         * @param input
         * @param maxLineLength
         * @return
         */
        public String addLinebreaks(String input, int maxLineLength, String formatBuffer) {
            StringTokenizer tok = new StringTokenizer(input, " ");
            StringBuilder output = new StringBuilder(input.length());
            int lineLen = 0;
            while (tok.hasMoreTokens()) {
                String word = tok.nextToken() + " ";

                if (lineLen + word.length() > maxLineLength) {
                    output.append(formatBuffer);
                    lineLen = 0;
                }
                output.append(word);
                lineLen += word.length();
            }
            return output.toString();
        }
    }
}

