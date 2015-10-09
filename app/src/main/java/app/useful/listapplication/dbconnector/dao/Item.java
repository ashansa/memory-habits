package app.useful.listapplication.dbconnector.dao;

import java.io.Serializable;

/**
 * Created by ashansa on 10/6/15.
 */
public class Item implements Serializable{

    private long id;
    private long rating;
    private String itemName;
    private String description;
    private String sectionName;

    public Item(long id, String itemName, String description, long rating, String sectionName) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.rating = rating;
        this.sectionName = sectionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return itemName;
    }

}
