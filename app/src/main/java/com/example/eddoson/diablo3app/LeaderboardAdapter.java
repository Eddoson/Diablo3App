package com.example.eddoson.diablo3app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author Ed Sutton
 */
public class LeaderboardAdapter extends ArrayAdapter<Friend>
{
    Context context;
    int resource;
    List<Friend> friendList;

    public LeaderboardAdapter(Context context, int resource, List<Friend> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.friendList = objects;
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

        //store this current friend
        Friend currentFriend = friendList.get(position);
        Log.d("friend", "inside adapter: " + currentFriend.toString());
        //connect UI components to logic
        TextView tvPlayerNumber = (TextView) convertView.findViewById(R.id.textViewPlayerNumber);
        TextView tvPlayerName = (TextView) convertView.findViewById(R.id.textViewPlayerName);
        TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.textViewPlayerScore);

        //print information to the textviews for this row
        tvPlayerNumber.setText("#" + (position + 1));
        tvPlayerName.setText(currentFriend.getUsername());
        tvPlayerScore.setText(Integer.toString(currentFriend.getHighscore()));

        return convertView;
    }
}
