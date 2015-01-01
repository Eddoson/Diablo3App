package com.example.eddoson.diablo3app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for the listview on the Friend Activity
 */
public class FriendAdapter extends ArrayAdapter
{
    Context context;
    int resource;
    List<Friend> objects;

    public FriendAdapter(Context context, int resource, List<Friend> objects)
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

        //this is the current friend being viewed
        Friend friend = objects.get(position);

        //connecting logic to the UI
        TextView tvUsername = (TextView) convertView.findViewById(R.id.textViewUsername);
        TextView tvBnetUsername = (TextView) convertView.findViewById(R.id.textViewBnetUsername);
        TextView tvParagon = (TextView) convertView.findViewById(R.id.textViewParagon);

        //set the friend info to the text views
        tvUsername.setText(friend.getUsername());
        tvBnetUsername.setText(friend.getBnetUsername());
        tvParagon.setText(friend.getParagon());

        return convertView;
    }
}
