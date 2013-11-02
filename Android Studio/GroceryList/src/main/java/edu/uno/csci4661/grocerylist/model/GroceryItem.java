package edu.uno.csci4661.grocerylist.model;

import android.content.ContentValues;
import android.database.Cursor;

import edu.uno.csci4661.grocerylist.database.GroceryProvider;

public class GroceryItem {

    int id;
    String name;
    String description;
    int quantity;

    public GroceryItem(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(GroceryProvider.KEY_ID));
        this.quantity = cursor.getInt(cursor.getColumnIndex(GroceryProvider.KEY_QUANTITY));
        this.name = cursor.getString(cursor.getColumnIndex(GroceryProvider.KEY_NAME));
        this.description = cursor.getString(cursor.getColumnIndex(GroceryProvider.KEY_DESCRIPTION));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ContentValues getContentValues(){
        ContentValues result = new ContentValues();
        result.put(GroceryProvider.KEY_DESCRIPTION,this.getDescription());
        result.put(GroceryProvider.KEY_QUANTITY,this.getQuantity());
        result.put(GroceryProvider.KEY_NAME,this.getName());
        return result;
    }

    @Override
    public String toString() {
        return "GroceryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
