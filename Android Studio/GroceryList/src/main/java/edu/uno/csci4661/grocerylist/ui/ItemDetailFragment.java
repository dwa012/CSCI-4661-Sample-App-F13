package edu.uno.csci4661.grocerylist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.receivers.ItemReceiver;
import edu.uno.csci4661.grocerylist.util.DataParser;

public class ItemDetailFragment extends Fragment {
    public static final String ITEM = "item";
    private GroceryItem item;

    public interface OnBroadcastClickListener {
        public void onClick(GroceryItem item);
    }

    private OnBroadcastClickListener listener = new OnBroadcastClickListener() {
        @Override
        public void onClick(GroceryItem item) {
            // left blank;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        if (this.getArguments().size() == 0) {
            throw new IllegalArgumentException("Needs to have an item id as an argument");
        }

        // this will throw an exeption if no id was given
        String json = this.getArguments().getString(ITEM);
        Gson gson = new Gson();
        item = gson.fromJson(json, GroceryItem.class);

        view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(item.getName());

        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        quantity.setText(item.getQuantity() + "");

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(item.getDescription());

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(getDrawable(item));

        Button shareButton = (Button) view.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareItem();
            }
        });

        Button broadcastButton = (Button) view.findViewById(R.id.broadcast);
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onClick(ItemDetailFragment.this.item);
                Intent intent = new Intent(ItemReceiver.BROADCAST_ACTION);
                intent.putExtra(Intent.EXTRA_TEXT, "Broadcasting: " + item.getName());
                ItemDetailFragment.this.getActivity().sendBroadcast(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (OnBroadcastClickListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private void shareItem() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String body = "Item: " + item.getName() + ", Quantity: " + item.getQuantity();

        // For a file in shared storage.  For data in private storage, use a ContentProvider.
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Please pick this up at the grocery for me");
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(shareIntent);
    }

    private void sendItemBroadcast() {

    }

    private int getDrawable(GroceryItem item) {
        switch (item.getId()) {
            case 1:
                return R.drawable.milk;
            case 2:
                return R.drawable.bread;
            case 3:
                return R.drawable.sugar;
            case 4:
                return R.drawable.peanut_butter;
            default:
                return 0;
        }
    }
}
