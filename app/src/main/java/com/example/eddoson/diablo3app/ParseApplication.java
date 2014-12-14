package com.example.eddoson.diablo3app;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Ed-Desktop on 12/12/2014.
 */
public class ParseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Parse.initialize(this, "n9bfmaSXweeTz4CmKsQWoiFn8KoMn7c4opPO0sRh", "Txl4b42XMZvWj30yTcoJwUtxHWA4onVTAzPmydR2");
    }
}
