package com.example.eddoson.diablo3app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This is the adapter for populating the list view on the character list activity
 * @author Ed Sutton
 */
public class CharacterAdapter extends ArrayAdapter<Character>
{
    Context context;
    int resource;
    List<Character> objects;
    public CharacterAdapter(Context context, int resource, List<Character> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //do convert view inflation stuff
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }

        //pull the current iterated character from the list of characters
        Character thisCharacter = objects.get(position);

        //connect the logic to the UI components
        TextView tvCharacterName = (TextView) convertView.findViewById(R.id.textViewCharacterName);
        TextView tvClass = (TextView) convertView.findViewById(R.id.textViewCharacterClass);

        //set the UI components to display the information from Character class
        tvCharacterName.setText(thisCharacter.getName());
        tvClass.setText(thisCharacter.getCharacterClass());

        return convertView;

    }
}
