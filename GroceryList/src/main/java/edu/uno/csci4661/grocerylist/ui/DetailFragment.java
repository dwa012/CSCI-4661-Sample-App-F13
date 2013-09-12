package edu.uno.csci4661.grocerylist.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailFragment extends Fragment {
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


        // TODO get the item and then show the relevant info

        return view;
    }
}
