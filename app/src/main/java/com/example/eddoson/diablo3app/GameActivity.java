package com.example.eddoson.diablo3app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Ed Sutton
 */
public class GameActivity extends ActionBarActivity
{
    Button btnSubmit;
    RadioGroup rgSelection;
    Spinner spnrItemType;
    ImageView ivItem;
    TextView tvTitle, tvNumCorrect, tvTimer;
    RadioButton correctRadioButton;
    String[] spinnerItemList = {"All", "Bracers", "Legs", "Chest", "Helm", "Boots", "Shoulders", "Belt", "One-hand", "Two-hand", "Off-hand"};
    ArrayAdapter adapter;
    List<ItemPiece> itemPieceList;
    ParseUser currentUser;
    int currentItemIndex, numCorrect, attempedGuesses;
    CountDownTimer timer;
    boolean isRankedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //connect logic to UI components
        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        tvTitle = (TextView) findViewById(R.id.textViewGameTitle);
        tvNumCorrect = (TextView) findViewById(R.id.textViewNumCorrect);
        tvTimer = (TextView) findViewById(R.id.textViewTimer);
        rgSelection = (RadioGroup) findViewById(R.id.radioGroupSelection);
        spnrItemType = (Spinner) findViewById(R.id.spinnerItemType);
        ivItem = (ImageView) findViewById(R.id.imageViewItem);

        //initialize
        itemPieceList = new ArrayList<>();
        numCorrect = 0;
        attempedGuesses = 0;
        currentUser = ParseUser.getCurrentUser();

        //check if intent extras are empty
        if (getIntent().getExtras() != null)
        {
            //set the mode for game activity
            isRankedMode = getIntent().getExtras().getBoolean(MainActivity.IS_RANKED_MODE_KEY);
            setupMode();
        }

        //setup dropdown box to display item types
        adapter = new ArrayAdapter(GameActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItemList);
        spnrItemType.setAdapter(adapter);

        //setup action to perform when an item is clicked
        spnrItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //start loading bar while pulling info
                final SimpleProgressDialog progressDialogHandler = new SimpleProgressDialog();
                progressDialogHandler.onPreExecute();

                //this is the item the user selected
                String selectedItem = spinnerItemList[position].toLowerCase();

                //create a query and pull info from parse
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Items");

                //if user selected all, do NOT filter query results
                if (!selectedItem.equals("all"))
                {
                    query.whereEqualTo("itemType", selectedItem.toLowerCase());
                }

                //go
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    public void done(List<ParseObject> parseItemList, ParseException e)
                    {
                        if (e != null)
                        {
                            //bad, something went wrong
                            Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //empty previous list
                        itemPieceList = new ArrayList<ItemPiece>();

                        //temp strings for creating itempieces
                        String name;
                        String imageUrl;
                        String itemType;

                        //loop through the parseobjects and retrieve items
                        for (ParseObject object : parseItemList)
                        {
                            //pull item piece information from parse
                            name = object.getString("name");
                            imageUrl = object.getString("imageURL");
                            itemType = object.getString("itemType");

                            //create a new item piece wrapper and place it in the list
                            ItemPiece thisItemPiece = new ItemPiece(imageUrl, name, itemType);
                            itemPieceList.add(thisItemPiece);
                        }
                        //randomize the list, then load stuff from it.
                        long seed = System.nanoTime();
                        Collections.shuffle(itemPieceList, new Random(seed));

                        //populate radio buttons, load image, etc
                        currentItemIndex = 0;
                        loadNewItem(currentItemIndex);

                        //stop loading bar
                        progressDialogHandler.onPostExecute(null);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //TODO: query for all
                //for now, do nothing
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_learning_game, menu);
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

    private void loadNewItem(int listIndex)
    {
        List<Integer> usedIndices = new ArrayList<>();
        //select the first item piece from the randomized list
        ItemPiece selectedItemPiece = itemPieceList.get(listIndex);

        //load image from first in list
        Picasso.with(GameActivity.this).load(selectedItemPiece.getImageUrl()).into(ivItem);

        //create a random index between 0 and 3
        long seed = System.nanoTime();
        Random rand = new Random(seed);
        int correctIndex = rand.nextInt(4);

        RadioButton wrongButton;
        for (int i = 0; i < rgSelection.getChildCount(); i++)
        {
            if (i == correctIndex)
            {
                //use the random int to select a radio button then set text to correct name
                correctRadioButton = (RadioButton) rgSelection.getChildAt(correctIndex);
                correctRadioButton.setText(selectedItemPiece.getName());

                //set this index as a used index, we don't want to select it again
                usedIndices.add(Integer.valueOf(correctIndex));
            }
            else
            {
                //create a wrong answer for a radio button
                wrongButton = (RadioButton) rgSelection.getChildAt(i);

                //find a random index for wrong name that is not the correct index
                int randomSelection;
                do
                {
                    //create a random index within the size of the item piece list
                    randomSelection = rand.nextInt(itemPieceList.size());
                } while (randomSelection == listIndex || usedIndices.contains(randomSelection));

                //set text to that random item
                wrongButton.setText(itemPieceList.get(randomSelection).getName());

                //set this index as used
                usedIndices.add(Integer.valueOf(randomSelection));
            }
        }
    }

    /**
     * Handles incrementing currentItemIndex through the itemPieceList
     */
    private void incrementItemListIndex()
    {
        //check to see if we're about to go out of bounds
        if ((currentItemIndex + 1) <= (itemPieceList.size() - 1))
        {
            currentItemIndex++;
        }
        //we've reached the end of the list
        else
        {
            //start index back at 0
            currentItemIndex = 0;

            //randomize list again
            long seed = System.nanoTime();
            Collections.shuffle(itemPieceList, new Random(seed));
        }
    }

    private void resetRankedGame()
    {
        //reset variables
        currentItemIndex = 0;
        numCorrect = 0;
        attempedGuesses = 0;

        //randomize list again
        long seed = System.nanoTime();
        Collections.shuffle(itemPieceList, new Random(seed));

        setupMode();
    }
    /**
     * Sets up correct perspectives for ranked or learning mode
     */
    private void setupMode()
    {
        //if its ranked mode
        if (isRankedMode)
        {
            //set title to ranked mode specific text
            tvTitle.setText("Ranked Mode");
            tvNumCorrect.setText("Correct: 0");

            //setup timer for ranked mode
            timer = new CountDownTimer(60000, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    //recalculate new time
                    int countSeconds = (int) (millisUntilFinished / 1000);
                    int countMinutes = countSeconds / 60;
                    countSeconds = countSeconds % 60;

                    //display new time
                    String timerDisplay = String.format("%02d:%02d", countMinutes, countSeconds);
                    tvTimer.setText(timerDisplay);
                }

                @Override
                public void onFinish()
                {
                    //set timer to 0, thanks jared314 for the idea
                    String timerDisplay = String.format("%02d:%02d", 0, 0);
                    tvTimer.setText(timerDisplay);

                    //check if this is a new high score
                    int currentHighscore = currentUser.getInt("highscore");

                    //if this score is better than their highscore, update highscore
                    if (numCorrect > currentHighscore)
                    {
                        currentUser.put("highscore", numCorrect);
                        currentUser.saveInBackground();
                    }

                    //TODO: stop user from playing, upload new scores to parse, update leaderboard, etc
                    //create a dialog to tell the user the game is done
                    AlertDialog.Builder stopDialog = new AlertDialog.Builder(GameActivity.this);

                    //calculate and display statistic for this round
                    float percentCorrect = ((float)numCorrect/(float)attempedGuesses) * 100;
                    stopDialog.setMessage(String.format("Attempted: %s \nCorrect: %s \nIncorrect: %s \nPercentage Correct: %%%.2f", attempedGuesses, numCorrect, (attempedGuesses - numCorrect), percentCorrect));
                    stopDialog.setTitle("Game Over");

                    //retry button logic
                    stopDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //dismiss
                            dialog.dismiss();

                            //reset the ranked game
                            resetRankedGame();
                        }
                    });

                    //quit button logic
                    stopDialog.setNegativeButton("Quit", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();

                            //go to the game menu
                            startActivity(new Intent(GameActivity.this, GameMenuActivity.class));

                            //destroy this instance of ranked mode
                            finish();
                        }
                    });
                    //show the dialog that we created
                    stopDialog.create().show();
                }
            };

            //submit button on click logic
            btnSubmit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //correct answer was selected!
                    if (rgSelection.getCheckedRadioButtonId() == correctRadioButton.getId())
                    {
                        //increase the amount of correct choices
                        numCorrect++;

                        //set the new number of correct answers to the respective textview
                        tvNumCorrect.setText("Correct: " + Integer.toString(numCorrect));
                    }

                    //increment currentItemIndex to move through the list of items
                    incrementItemListIndex();

                    //load new item using the new index
                    loadNewItem(currentItemIndex);

                    //increment number of guesses
                    attempedGuesses++;

                    //uncheck all radio buttons
                    rgSelection.clearCheck();
                }
            });

            //create an alert dialog to pop up asking the user if they're ready
            AlertDialog.Builder startDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            startDialogBuilder.setMessage("Press 'OK' when you're ready to begin!");
            startDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    timer.start();
                }
            });

            //present the dialog at activity start
            startDialogBuilder.create().show();
        }
        //is learning mode
        else
        {
            //changed title to learning mode
            tvTitle.setText("Learning Mode");

            //submit button on click logic
            btnSubmit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //initialize alert dialog builder that will tell the user if they are correct or not
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(GameActivity.this);

                    //correct answer was selected!
                    if (rgSelection.getCheckedRadioButtonId() == correctRadioButton.getId())
                    {
                        adBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //increment currentItemIndex to move through the list of items
                                incrementItemListIndex();

                                //load new item using the new index
                                loadNewItem(currentItemIndex);

                                //dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                        adBuilder.setMessage("Correct! It was " + correctRadioButton.getText());
                    }
                    //wrong answer was selected!
                    else
                    {
                        adBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                        adBuilder.setMessage("Incorrect! Try again!");
                    }

                    //uncheck all radio buttons
                    rgSelection.clearCheck();

                    //show the built alert dialog
                    adBuilder.create().show();
                }
            });
        }

    }

    /**
     * Simple class to perform a loading bar on a separate thread
     */
    class SimpleProgressDialog extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog loadingBar;

        @Override
        protected void onPreExecute()
        {
            loadingBar = new ProgressDialog(GameActivity.this);
            loadingBar.setMessage("Loading...");
            loadingBar.setIndeterminate(true);
            loadingBar.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            loadingBar.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            return null;
        }
    }
}
