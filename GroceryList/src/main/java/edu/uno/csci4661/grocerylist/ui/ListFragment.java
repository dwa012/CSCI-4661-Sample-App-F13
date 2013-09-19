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

/**
 * Created by danielward on 9/8/13.
 */
public class ListFragment extends Fragment {

    public interface OnListItemSelectedListener {
        public void onListItemSelected(int index);
    }

    private OnListItemSelectedListener listener;

    public ListFragment() {
        // left blank
    }

    /**
     * Add the listener at construction if needed.
     *
     * @param listener The item click listener that can be used to signal the parent Activity.
     */
    public ListFragment(OnListItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<String>(this.getActivity(),R.array.items));

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onListItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // left blank
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (OnListItemSelectedListener) activity;
        } catch (ClassCastException e) {
            if (this.listener == null) {
                this.listener = new OnListItemSelectedListener() {
                    @Override
                    public void onListItemSelected(int index) {
                        // left blank
                    }
                };
            }
        }
    }
}
