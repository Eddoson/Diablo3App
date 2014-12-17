package com.example.eddoson.diablo3app;

import android.app.Application;

import com.parse.Parse;

/**
 * Controls initializing parse
 * @author Ed Sutton
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
