package com.example.eddoson.diablo3app;

import java.io.Serializable;

/**
 * Wrapper class for friend information
 */
public class Friend implements Serializable
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
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Friend friend = (Friend) o;

        if (paragon != null ? !paragon.equals(friend.paragon) : friend.paragon != null)
        {
            return false;
        }
        if (username != null ? !username.equals(friend.username) : friend.username != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (paragon != null ? paragon.hashCode() : 0);
        return result;
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
