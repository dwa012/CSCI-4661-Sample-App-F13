package edu.uno.csci4661.grocerylist.adapters;

import android.app.Activity;
import android.content.Context;
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

public class ItemListAdapter extends ArrayAdapter<GroceryItem> {
    private Context context;
    private int resource;
    private List<GroceryItem> items;

    public interface OnItemClickListener {
        public void onItemClicked(int position, GroceryItem item);
    }

    private OnItemClickListener listener;

    public ItemListAdapter(Context context, int textViewResourceId, List<GroceryItem> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.resource = textViewResourceId;
        this.items = objects;

        listener = new OnItemClickListener() {
            @Override
            public void onItemClicked(int position, GroceryItem item) {
                // left blank
            }
        };
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

        final GroceryItem item = items.get(position);

        holder.quantity.setText(item.getQuantity() + ""); // doh! this was an int, not a String *facepalm*
        holder.name.setText(item.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // add the listener for the convertview
        final int itemPosition = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(itemPosition, item);
            }
        });

        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    static class Holder {
        TextView quantity;
        TextView name;
        Button button;
    }
}
