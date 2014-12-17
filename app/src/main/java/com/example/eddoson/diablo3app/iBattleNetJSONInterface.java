package com.example.eddoson.diablo3app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interfaces between activities who need Battle.net info
 * and the BattleNetAPIHandler. Produces a callback for those
 * activities
 */
public interface iBattleNetJSONInterface
{
    /**
     * This is a callback that supplies the root JSONObject
     * from BattleNetAPIHandler
     * @param root
     */
    public void onUpdateJSONObject(JSONObject root) throws JSONException;
}
