package edu.uno.csci4661.grocerylist.model;

import java.util.List;

/**
 * Created by danielward on 10/3/13.
 */
public class ItemWrapper {
    private List<GroceryItem> items;

    public List<GroceryItem> getItems() {
        return items;
    }

    public void setItems(List<GroceryItem> items) {
        this.items = items;
    }
}
