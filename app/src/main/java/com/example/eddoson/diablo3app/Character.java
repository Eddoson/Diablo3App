package com.example.eddoson.diablo3app;

/**
 * Wrapper class to store character information
 */
public class Character
{
    String name, characterClass;

    public Character(String name, String characterClass)
    {
        this.name = name;
        this.characterClass = characterClass;
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

    @Override
    public String toString()
    {
        return "Character{" +
                "name='" + name + '\'' +
                ", characterClass='" + characterClass + '\'' +
                '}';
    }
}
