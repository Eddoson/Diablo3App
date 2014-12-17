Diablo3App
==========

A Diablo 3 assistant app for Android where users can keep track of their characters as well as their friend's. Item identification game will also be available to test your skills identifying weapon and armor icons.

NOTE: This app needs an Application file that is connected to your own Parse.com app. Create an app on Parse.com and follow their guide using your own hashed keys that they give you. The java class will extend Application and inside of the onCreate() method you'll pase your info like this:  @Override
     public void onCreate()
     {
         super.onCreate();
         Parse.initialize(this, "privatekey", "publickey");
     }
