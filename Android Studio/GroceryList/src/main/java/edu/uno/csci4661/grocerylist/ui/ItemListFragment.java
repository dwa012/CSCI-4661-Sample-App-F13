package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.adapters.ItemListAdapter;
import edu.uno.csci4661.grocerylist.adapters.ItemListCursorAdapter;
import edu.uno.csci4661.grocerylist.database.GroceryProvider;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.model.ItemWrapper;

public class ItemListFragment extends Fragment {

    private CursorAdapter adapter;

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
                Cursor cursor = (Cursor)adapterView.getAdapter().getItem(position);

                int idIndex = cursor.getColumnIndexOrThrow(GroceryProvider.KEY_ID);
                int itemId = cursor.getInt(idIndex);

                Log.d("grocery_list", itemId + "");
                Uri uri = ContentUris.withAppendedId(GroceryProvider.CONTENT_URI,itemId);

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

        Cursor c = getItems();
        if(c.getCount() == 0){
//            new FetchTask(this.getActivity()).execute();
        }
    }

    private Cursor getItems() {
        return getActivity().getContentResolver().query(GroceryProvider.CONTENT_URI,null,null,null,null,null);
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
//            new FetchTask(this.getActivity()).execute();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class FetchTask extends AsyncTask<Void, Integer, Cursor> {

        private ProgressDialog progressDialog;
        private Context context;

        private FetchTask() {
            // left blank
        }

        public FetchTask(Context context) {
            this();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Fetching Items");
            progressDialog.show();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            HttpGet request = new HttpGet("http://csci4661-api.appspot.com/api/items");

            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpResponse response = httpClient.execute(request);
                String json = EntityUtils.toString(response.getEntity());

                Log.d("grocerylist", json);

                Gson gson = new Gson();
                ItemWrapper items = gson.fromJson(json, ItemWrapper.class);

                ContentValues[] values = new ContentValues[items.getItems().size()];

                for (int i = 0; i < items.getItems().size(); i++) {
                    values[i] = items.getItems().get(i).getContentValues();
                }

                getActivity().getContentResolver().bulkInsert(GroceryProvider.CONTENT_URI,values);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return getItems();
        }

        @Override
        protected void onPostExecute(Cursor groceryItems) {
            adapter.swapCursor(groceryItems);
            progressDialog.dismiss();
        }
    }
}
