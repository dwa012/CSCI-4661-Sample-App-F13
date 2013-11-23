package edu.uno.csci4661.grocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.androidquery.AQuery;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.database.GroceryProvider;

public class ItemListCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    AQuery aQuery;

    public ItemListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.item_list_layout, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        aQuery = new AQuery(view);

        int nameIndex = cursor.getColumnIndex(GroceryProvider.KEY_NAME);
        int quantityIndex = cursor.getColumnIndex(GroceryProvider.KEY_QUANTITY);

        aQuery.id(R.id.item_name).text(cursor.getString(nameIndex));
    }
}
