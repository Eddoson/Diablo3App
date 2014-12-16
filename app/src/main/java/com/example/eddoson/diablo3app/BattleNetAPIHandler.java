package com.example.eddoson.diablo3app;

import android.app.Activity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Handle all API requests and produce useful objects such as
 * Character, Friend, etc. The object of this type will store
 * a JSON Object to reference to after pulling information from
 * the Battle.net API.
 */
public class BattleNetAPIHandler
{
    Date LastPulltimestamp;
    static JSONObject root;

    public static List<Character> getCharacters(Activity activity) throws JSONException
    {
        //this will be the list of characters
        final List<Character> characterList = new ArrayList<>();
        String name = null;
        String characterClass = null;

        //this should wait until response comes back, then set to root
        updateJSONObject(activity);

        //grab the JSONArray of heroes from root
        JSONArray heroesArray = root.getJSONArray("heroes");
        for (int i = 0; i < heroesArray.length(); i++)
        {
            //pull the hero object from array
            JSONObject heroObject = (JSONObject) heroesArray.get(i);

            //pull name and class from object, create Character object
            name = heroObject.getString("name");
            characterClass = heroObject.getString("class");
            Character newCharacter = new Character(name, characterClass);

            //add the new character to the list
            characterList.add(newCharacter);
        }
        Log.d("volley", characterList.toString());
        return characterList;
    }

    public static void updateJSONObject(Activity activity)
    {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://us.battle.net/api/d3/profile/Eddoson-1118/";
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        queue.add(request);

        try {
            root = future.get(); // this will block
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        }
        Log.d("volley", root.toString());







        /*
        String url = "http://us.battle.net/api/d3/profile/Eddoson-1118/";
        RequestQueue queue = Volley.newRequestQueue(activity);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        //yay everything worked
                        root = response;
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //boo, something broke
                        Log.d("volley", error.getMessage());
                    }
                });

            queue.add(jsObjRequest);*/
    }

}
