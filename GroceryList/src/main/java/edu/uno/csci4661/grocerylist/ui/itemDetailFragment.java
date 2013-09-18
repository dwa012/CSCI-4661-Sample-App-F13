package edu.uno.csci4661.grocerylist.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.util.DataParser;

public class ItemDetailFragment extends Fragment {
    public static final String ITEM_ID = "item_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        // TODO add the some code to inflate from a layout

        if (this.getArguments().size() == 0) {
            throw new IllegalArgumentException("Needs to have an item id as an argument");
        }

        // this will throw an exeption if no id was given
        int id = this.getArguments().getInt(ITEM_ID);

        GroceryItem item = null;


        try {
            List<GroceryItem> items = DataParser.getData(this.getActivity());

            for (GroceryItem groceryItem : items) {
                if (groceryItem.getId() == id) {
                    item = groceryItem;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(item.getName());

        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        quantity.setText(item.getQuantity() + "");

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(item.getDescription());


        return view;
    }
}
