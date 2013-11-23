package edu.uno.csci4661.grocerylist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import edu.uno.csci4661.grocerylist.auth.AuthActivity;
import edu.uno.csci4661.grocerylist.auth.AuthPreferences;
import edu.uno.csci4661.grocerylist.ui.ItemDetailActivity;
import edu.uno.csci4661.grocerylist.ui.ItemDetailFragment;
import edu.uno.csci4661.grocerylist.ui.ItemListFragment;
import edu.uno.csci4661.grocerylist.ui.SettingsActivity;

public class MainActivity extends Activity implements ItemListFragment.ListFragmentListener {

    private int REGISTER_ACTIVITY_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if the fragment placeholder is in view then populate it
        if (findViewById(R.id.detail_fragment) != null) {
            Fragment fragment = new ItemDetailFragment();
            Bundle args = new Bundle();
            args.putInt(ItemDetailFragment.ITEM, 1);
            fragment.setArguments(args);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.detail_fragment, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        AuthPreferences authPreferences = new AuthPreferences(this);

        if (authPreferences.getUser() == null) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        } else {
            new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... objects) {
                    GCMIntentService.register(getApplicationContext());
                    return null;
                }
            }.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  Toast.makeText(this, requestCode + " " + resultCode + ";", Toast.LENGTH_LONG).show();
        if (requestCode == REGISTER_ACTIVITY_REQ_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Could not register for GCM", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onListItemSelected(Uri item) {

        Gson gson = new Gson();

        // the detail fragment is not in the view, most likely on a smaller device
        if (this.getFragmentManager().findFragmentById(R.id.detail_fragment) == null) {
            // launch a ItemDetail Activity
            // you can pass a data to an activity via Extras, then pass it along to the fragment
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra("item_uri", item);

            this.startActivity(intent);
        } else { // detail fragment is in view
            // update the existing detail fragment in the UI, usually by replacing it
            ItemDetailFragment fragment = new ItemDetailFragment();
            Bundle args = new Bundle();
            args.putParcelable("item_uri", item);
            fragment.setArguments(args);

            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.replace(R.id.detail_fragment, fragment);
            ft.commit();
        }
    }
}
