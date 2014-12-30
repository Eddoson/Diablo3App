package com.example.eddoson.diablo3app;

/**
 * Created by Ed-Desktop on 12/29/2014.
 */
public class ItemPiece
{
    String imageUrl, name, itemType;

    public ItemPiece(String imageUrl, String name, String itemType)
    {
        this.imageUrl = imageUrl;
        this.name = name;
        this.itemType = itemType;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    @Override
    public String toString()
    {
        return "ItemPiece{" +
                "imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", itemType='" + itemType + '\'' +
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

        ItemPiece itemPiece = (ItemPiece) o;

        if (imageUrl != null ? !imageUrl.equals(itemPiece.imageUrl) : itemPiece.imageUrl != null)
        {
            return false;
        }
        if (itemType != null ? !itemType.equals(itemPiece.itemType) : itemPiece.itemType != null)
        {
            return false;
        }
        if (name != null ? !name.equals(itemPiece.name) : itemPiece.name != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = imageUrl != null ? imageUrl.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (itemType != null ? itemType.hashCode() : 0);
        return result;
    }
}
