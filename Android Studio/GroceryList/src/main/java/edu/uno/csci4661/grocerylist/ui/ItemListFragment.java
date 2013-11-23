package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.adapters.ItemListCursorAdapter;
import edu.uno.csci4661.grocerylist.database.GroceryProvider;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.sync.MySyncAdapter;

public class ItemListFragment extends Fragment {

    private CursorAdapter adapter;
    private ContentObserver observer;

    public interface ListFragmentListener {
        public void onListItemSelected(Uri itemUri);
    }

    private List<GroceryItem> items;
    private MenuItem item;


    private ListFragmentListener listener = new ListFragmentListener() {
        @Override
        public void onListItemSelected(Uri itemUri) {
            // left blank
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container);
        ListView list = (ListView) view.findViewById(R.id.listview);
        items = new ArrayList<GroceryItem>();
        Cursor c = getItems();

//        adapter = new ItemListAdapter(this.getActivity(), R.layout.item_list_layout, items);
        adapter = new ItemListCursorAdapter(this.getActivity(), c, -1);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getAdapter().getItem(position);

                int idIndex = cursor.getColumnIndexOrThrow(GroceryProvider.KEY_ID);
                int itemId = cursor.getInt(idIndex);

                Log.d("grocery_list", itemId + "");
                Uri uri = ContentUris.withAppendedId(GroceryProvider.CONTENT_URI, itemId);

                listener.onListItemSelected(uri);
            }
        });

        list.setAdapter(adapter);

        setHasOptionsMenu(true);

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

    @Override
    public void onResume() {
        super.onResume();
        adapter.changeCursor(getItems());

        getActivity().registerReceiver(syncFinishedReceiver, new IntentFilter(MySyncAdapter.SYNC_ADAPTER_FINISHED_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(syncFinishedReceiver);
    }

    private Cursor getItems() {
        return getActivity().getContentResolver().query(GroceryProvider.CONTENT_URI, null, null, null, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        item = menu.add("Refresh");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == this.item.getItemId()) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor cursor = ItemListFragment.this.getItems();
            ItemListFragment.this.adapter.changeCursor(cursor);
        }
    };
}
