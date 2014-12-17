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
 * Handle all API requests to Battle.net
 * @author Ed Sutton
 */
public class BattleNetAPIHandler extends AsyncTask<String, Void, JSONObject>
{
    Activity activity;
    iBattleNetJSONInterface iJsonInterfaceObject;

    public BattleNetAPIHandler(Activity activity)
    {
        this.activity = activity;
        this.iJsonInterfaceObject = (iBattleNetJSONInterface) activity;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = params[0] + "/";
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
