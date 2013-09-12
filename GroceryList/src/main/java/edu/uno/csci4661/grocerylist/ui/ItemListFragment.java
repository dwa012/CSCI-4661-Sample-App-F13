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

import edu.uno.csci4661.grocerylist.R;

public class ItemListFragment extends Fragment {

    public interface ListFragmentListener {
        public void onListItemSelected(int id);
    }

    String[] items = {"first", "second", "third"};


    private ListFragmentListener listener = new ListFragmentListener() {
        @Override
        public void onListItemSelected(int id) {
            // left blank
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container);
        ListView list = (ListView) view.findViewById(R.id.listview);
        list.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, items));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //the listener now takes an id of an item
//                listener.onListItemSelected(items[position].getId());
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
