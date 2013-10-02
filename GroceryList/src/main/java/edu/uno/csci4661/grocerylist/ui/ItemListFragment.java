package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.adapters.ItemListAdapter;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.util.DataParser;

public class ItemListFragment extends Fragment {

    public interface ListFragmentListener {
        public void onListItemSelected(int id);
    }

    List<GroceryItem> items;
    MenuItem item;


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

        ItemListAdapter adapter = new ItemListAdapter(this.getActivity(), R.layout.item_list_layout, items);

        // have to use a custom listener since the button in the layout causes the listview listener
        // to not work
        adapter.setOnItemClickListener(new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, GroceryItem item) {
                listener.onListItemSelected(item.getId());
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

    private class FetchTask extends AsyncTask<Void, Integer, String> {

        private ProgressDialog progressDialog;
        private Context context;

        private FetchTask() {
            // left blank
        }

        public FetchTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMax(100);
            progressDialog.setTitle("Refreshing Data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < 100; i++) {
                publishProgress(i);

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "Finished";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog
                    .setMessage(s)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            alertDialog.create().show();
        }
    }
}
