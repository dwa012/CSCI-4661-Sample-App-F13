package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.adapters.ItemListAdapter;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.model.ItemWrapper;

public class ItemListFragment extends Fragment {

    private ItemListAdapter adapter;

    public interface ListFragmentListener {
        public void onListItemSelected(GroceryItem item);
    }

    private List<GroceryItem> items;
    private MenuItem item;


    private ListFragmentListener listener = new ListFragmentListener() {
        @Override
        public void onListItemSelected(GroceryItem item) {
            // left blank
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container);
        ListView list = (ListView) view.findViewById(R.id.listview);
        items = new ArrayList<GroceryItem>();


        adapter = new ItemListAdapter(this.getActivity(), R.layout.item_list_layout, items);

        // have to use a custom listener since the button in the layout causes the listview listener
        // to not work
        adapter.setOnItemClickListener(new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, GroceryItem item) {
                listener.onListItemSelected(item);
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

        new FetchTask(this.getActivity()).execute();
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
            new FetchTask(this.getActivity()).execute();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class FetchTask extends AsyncTask<Void, Integer, List<GroceryItem>> {

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
        protected List<GroceryItem> doInBackground(Void... voids) {
            List<GroceryItem> results = new ArrayList<GroceryItem>();

            HttpGet request = new HttpGet("http://csci4661-api.appspot.com/api/items");

            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpResponse response = httpClient.execute(request);
                String json = EntityUtils.toString(response.getEntity());

                Log.d("grocerylist", json);

                Gson gson = new Gson();
                ItemWrapper items = gson.fromJson(json, ItemWrapper.class);
                results = items.getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<GroceryItem> groceryItems) {
            ItemListFragment.this.items.clear();
            ItemListFragment.this.items.addAll(groceryItems);
            ItemListFragment.this.adapter.notifyDataSetChanged();

            progressDialog.dismiss();

        }
    }
}
