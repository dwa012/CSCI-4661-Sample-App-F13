package edu.uno.csci4661.grocerylist;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import edu.uno.csci4661.grocerylist.ui.ItemDetailActivity;
import edu.uno.csci4661.grocerylist.ui.ItemDetailFragment;
import edu.uno.csci4661.grocerylist.ui.ItemListFragment;

public class MainActivity extends Activity implements ItemListFragment.ListFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    public void onListItemSelected(int id) {

        // the detail fragment is not in the view, most likely on a smaller device
        if (this.getFragmentManager().findFragmentById(R.id.detail_fragment) == null) {
            // launch a ItemDetail Activity
            // you can pass a data to an activity via Extras, then pass it along to the fragment
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ITEM_ID, id);

            this.startActivity(intent);
        } else { // detail fragment is in view
            // update the existing detail fragment in the UI, usually by replacing it
            ItemDetailFragment fragment = new ItemDetailFragment();
            Bundle args = new Bundle();
            args.putInt(ItemDetailFragment.ITEM_ID, id);
            fragment.setArguments(args);

            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.replace(R.id.detail_fragment, fragment);
            ft.commit();
        }

//        Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
    }
}
