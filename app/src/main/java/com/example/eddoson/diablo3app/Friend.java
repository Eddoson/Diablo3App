package com.example.eddoson.diablo3app;

/**
 * Wrapper class for friend information
 */
public class Friend
{
    String username, paragon;

    public Friend(String username)
    {
        this.username = username;
        this.paragon = "0";
    }

    public Friend(String username, String paragon)
    {
        this.username = username;
        this.paragon = paragon;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getParagon()
    {
        return paragon;
    }

    public void setParagon(String paragon)
    {
        this.paragon = paragon;
    }

    @Override
    public String toString()
    {
        return "Friend{" +
                "username='" + username + '\'' +
                ", paragon='" + paragon + '\'' +
                '}';
    }
}
