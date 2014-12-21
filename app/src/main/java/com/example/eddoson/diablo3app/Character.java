package com.example.eddoson.diablo3app;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Wrapper class to store character information
 *
 * @author Ed Sutton
 */
public class Character implements Serializable
{
    String name, characterClass, id;
    HashMap<String, HashMap<String, String>> items;

    public Character(String name, String characterClass, String id)
    {
        this.name = name;
        this.characterClass = characterClass;
        this.id = id;
    }

    public Character(HashMap<String, HashMap<String, String>> items)
    {
        this.items = items;
    }

    public HashMap<String, HashMap<String, String>> getItems()
    {
        return items;
    }

    public void setItems(HashMap<String, HashMap<String, String>> items)
    {
        this.items = items;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCharacterClass()
    {
        return characterClass;
    }

    public void setCharacterClass(String characterClass)
    {
        this.characterClass = characterClass;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Character{" +
                "name='" + name + '\'' +
                ", characterClass='" + characterClass + '\'' +
                ", id='" + id + '\'' +
                ", items=" + items +
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

        Character character = (Character) o;

        if (characterClass != null ? !characterClass.equals(character.characterClass) : character.characterClass != null)
        {
            return false;
        }
        if (id != null ? !id.equals(character.id) : character.id != null)
        {
            return false;
        }
        if (items != null ? !items.equals(character.items) : character.items != null)
        {
            return false;
        }
        if (name != null ? !name.equals(character.name) : character.name != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (characterClass != null ? characterClass.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
