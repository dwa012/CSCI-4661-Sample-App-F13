package edu.uno.csci4661.grocerylist.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;

/**
 * Created by danielward on 9/19/13.
 */
public class ItemListAdapter extends ArrayAdapter<GroceryItem> {
    private Context context;
    private int resource;
    private List<GroceryItem> items;

    public ItemListAdapter(Context context, int textViewResourceId, List<GroceryItem> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.resource = textViewResourceId;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

            holder = new Holder();
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.quantity = (TextView) convertView.findViewById(R.id.item_quantity);
            holder.button = (Button) convertView.findViewById(R.id.item_button);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Log.d("grocery", items.get(position) + "'");

        holder.quantity.setText(items.get(position).getQuantity());
        holder.name.setText(items.get(position).getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "button pressed", Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }


    static class Holder {
        TextView quantity;
        TextView name;
        Button button;
    }
}
