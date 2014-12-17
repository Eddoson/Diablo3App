package com.example.eddoson.diablo3app;

import java.io.Serializable;

/**
 * Wrapper class for friend information
 */
public class Friend implements Serializable
{
    String bnetUsername, paragon;

    public Friend(String bnetUsername, String paragon)
    {
        this.bnetUsername = bnetUsername;
        this.paragon = paragon;
    }

    public Friend(String bnetUsername)
    {
        this.bnetUsername = bnetUsername;
    }

    public String getBnetUsername()
    {
        return bnetUsername;
    }

    public void setBnetUsername(String bnetUsername)
    {
        this.bnetUsername = bnetUsername;
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
                "bnetUsername='" + bnetUsername + '\'' +
                ", paragon='" + paragon + '\'' +
                '}';
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

        if (bnetUsername != null ? !bnetUsername.equals(friend.bnetUsername) : friend.bnetUsername != null)
        {
            return false;
        }
        if (paragon != null ? !paragon.equals(friend.paragon) : friend.paragon != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = bnetUsername != null ? bnetUsername.hashCode() : 0;
        result = 31 * result + (paragon != null ? paragon.hashCode() : 0);
        return result;
    }
}
