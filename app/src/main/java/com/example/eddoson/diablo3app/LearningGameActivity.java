package com.example.eddoson.diablo3app;

import android.os.Bundle;
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


public class LearningGameActivity extends ActionBarActivity
{
    Button btnSubmit;
    RadioGroup rgSelection;
    Spinner spnrItemType;
    ImageView ivItem;
    RadioButton rbA, rbB, rbC, rbD;
    String[] spinnerItemList = {"All", "Bracers", "Legs", "Chest", "Helm", "Boots", "Shoulders", "Belt", "One-hand", "Two-hand", "Off-hand"};
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_game);

        //connect logic to UI components
        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        rgSelection = (RadioGroup) findViewById(R.id.radioGroupSelection);
        spnrItemType = (Spinner) findViewById(R.id.spinnerItemType);
        ivItem = (ImageView) findViewById(R.id.imageViewItem);
        rbA = (RadioButton) findViewById(R.id.radioButtonA);
        rbB = (RadioButton) findViewById(R.id.radioButtonB);
        rbC = (RadioButton) findViewById(R.id.radioButtonC);
        rbD = (RadioButton) findViewById(R.id.radioButtonD);

        //spinner logic for clicking on an item
        spnrItemType.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });

        //initialize
        adapter = new ArrayAdapter(LearningGameActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItemList);
        spnrItemType.setAdapter(adapter);
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
}
