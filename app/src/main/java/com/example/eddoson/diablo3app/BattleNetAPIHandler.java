package com.example.eddoson.diablo3app;

import android.app.Activity;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Handle all API requests and produce useful objects such as
 * Character, Friend, etc. The object of this type will store
 * a JSON Object to reference to after pulling information from
 * the Battle.net API.
 */
public class BattleNetAPIHandler extends AsyncTask<Void, Void, JSONObject>
{
    Activity activity;
    iBattleNetJSONInterface iJsonInterfaceObject;

    public BattleNetAPIHandler(Activity activity)
    {
        this.activity = activity;
        this.iJsonInterfaceObject = (iBattleNetJSONInterface) activity;
    }

    /* public static List<Character> getCharacters(Activity activity) throws JSONException
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
    }*/

    @Override
    protected JSONObject doInBackground(Void... params)
    {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://us.battle.net/api/d3/profile/Eddoson-1118/";
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        queue.add(request);
        JSONObject root = new JSONObject();

        try
        {
           root = future.get(); // this will block
        } catch (InterruptedException e)
        {
            // exception handling
        } catch (ExecutionException e)
        {
            // exception handling
        }
        return root;
    }

    @Override
    protected void onPostExecute(JSONObject root)
    {
        //now we have the JSONObject from doInBackground()
        //send it to the respective activity callback
        try
        {
            iJsonInterfaceObject.onUpdateJSONObject(root);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        super.onPostExecute(root);
    }
}
