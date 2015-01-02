package com.example.eddoson.diablo3app;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Wrapper class for friend information
 * @author Ed Sutton
 */
public class Friend implements Serializable
{
    String username, bnetUsername, paragon;
    int highscore;

    public Friend()
    {
        username = null;
        bnetUsername = null;
        paragon = null;
        highscore = 0;
    }

    public Friend(String bnetUsername, String paragon)
    {
        this.bnetUsername = bnetUsername;
        this.paragon = paragon;
        this.username = "User Not Registered";
        this.highscore = 0;
    }

    public Friend(String bnetUsername)
    {
        this.bnetUsername = bnetUsername;
        this.username = "User Not Registered";
        this.highscore = 0;
        this.paragon = null;
    }

    public Friend(String bnetUsername, String paragon, int highscore)
    {
        this.bnetUsername = bnetUsername;
        this.paragon = paragon;
        this.highscore = highscore;
        this.username = "User Not Registered";
    }

    public Friend(String username, int highscore)
    {
        this.username = username;
        this.highscore = highscore;
        this.bnetUsername = null;
        this.paragon = null;
    }

    public Friend(String username, String bnetUsername, String paragon, int highscore)
    {
        this.username = username;
        this.bnetUsername = bnetUsername;
        this.paragon = paragon;
        this.highscore = highscore;
    }

    /**
     * Factory method that produces a friend from a ParseUser object
     * Thanks to jared314 for the idea
     * @param user
     * @return
     */
    static Friend fromParseUser(ParseUser user)
    {
        Friend newFriend = new Friend();
        newFriend.setUsername(user.getUsername());
        newFriend.setBnetUsername(user.getString("bnetUsername"));
        newFriend.setHighscore(user.getInt("highscore"));

        return newFriend;
    }
    /**
     * Factory method that produces a friend from a parse username
     * Thanks to jared314 for the idea
     * @param username
     * @return
     */
    static Friend fromParseUsername(String username)
    {
        Friend newFriend = new Friend();
        newFriend.setUsername(username);

        return newFriend;
    }

    /**
     * Factory method that produces a friend from a battle net username
     * Thanks to jared314 for the idea
     * @param bnetUsername
     * @return
     */
    static Friend fromBattleNetUsername(String bnetUsername)
    {
        Friend newFriend = new Friend();
        newFriend.setBnetUsername(bnetUsername);

        return newFriend;
    }

    /**
     * Takes in a JSONObject and converts its information into a Friend object
     * @param jsonFriendRoot
     * @return
     * @throws JSONException
     */
    static Friend fromJSONObject(JSONObject jsonFriendRoot) throws JSONException
    {
        Friend newFriend = new Friend();

        newFriend.setUsername(jsonFriendRoot.getString("username"));
        newFriend.setBnetUsername(jsonFriendRoot.getString("bnetUsername"));
        newFriend.setParagon(jsonFriendRoot.getString("paragon"));
        newFriend.setHighscore(jsonFriendRoot.getInt("highscore"));

        return newFriend;
    }

    /**
     *
     * @return JSONObject with friend information packaged
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException
    {
        JSONObject jsonFriendRoot = new JSONObject();

        jsonFriendRoot.put("username", username);
        jsonFriendRoot.put("bnetUsername", bnetUsername);
        jsonFriendRoot.put("paragon", paragon);
        jsonFriendRoot.put("highscore", highscore);

        return jsonFriendRoot;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public int getHighscore()
    {
        return highscore;
    }

    public void setHighscore(int highscore)
    {
        this.highscore = highscore;
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
                "username='" + username + '\'' +
                ", bnetUsername='" + bnetUsername + '\'' +
                ", paragon='" + paragon + '\'' +
                ", highscore=" + highscore +
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
        result = 31 * result + (bnetUsername != null ? bnetUsername.hashCode() : 0);
        result = 31 * result + (paragon != null ? paragon.hashCode() : 0);
        result = 31 * result + highscore;
        return result;
    }
}
