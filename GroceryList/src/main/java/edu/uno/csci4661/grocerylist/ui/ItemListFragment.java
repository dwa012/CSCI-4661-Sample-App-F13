package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.util.DataParser;

public class ItemListFragment extends Fragment {

    public interface ListFragmentListener {
        public void onListItemSelected(int id);
    }

    List<GroceryItem> items;


    private ListFragmentListener listener = new ListFragmentListener() {
        @Override
        public void onListItemSelected(int id) {
            // left blank
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container);
        ListView list = (ListView) view.findViewById(R.id.listview);

        try {
            items = DataParser.getData(this.getActivity());
        } catch (IOException e) {
            e.printStackTrace();
            items = new ArrayList<GroceryItem>();
        }

        final String[] names = new String[items.size()];

        for (int i = 0; i < items.size(); i++) {
            names[i] = items.get(i).getName();
        }


        list.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, names));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //the listener now takes an id of an item
                listener.onListItemSelected(items.get(position).getId());
            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (ListFragmentListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
