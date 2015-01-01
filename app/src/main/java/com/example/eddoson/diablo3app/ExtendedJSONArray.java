package com.example.eddoson.diablo3app;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Ed-Desktop on 12/31/2014.
 */
public class ExtendedJSONArray extends JSONArray
{
    /**
     * Custom remove method for JSONArray
     * Thanks to swiftyspiffy and jared314 for the idea!
     *
     * @param index
     * @return
     */
    public JSONArray removeFromJSONArray(int index) throws JSONException
    {
        //temporary array to return later
        JSONArray returnArray = new JSONArray();

        //loop through the the jsonarray
        for (int i = 0; i < this.length(); i++)
        {
            //if this isn't the index to remove from
            if (i != index)
            {
                //copy contents
                returnArray.put(this.getJSONObject(i));
            }

        }

        return returnArray;
    }
}
