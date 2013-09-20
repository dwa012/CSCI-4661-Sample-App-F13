package edu.uno.csci4661.grocerylist.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.util.DataParser;

public class ItemDetailFragment extends Fragment {
    public static final String ITEM_ID = "item_id";
    private static final int ACTION_CODE = 1234;
    private ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        if (this.getArguments().size() == 0) {
            throw new IllegalArgumentException("Needs to have an item id as an argument");
        }

        // this will throw an exeption if no id was given
        int id = this.getArguments().getInt(ITEM_ID);

        GroceryItem item = null;


        try {
            List<GroceryItem> items = DataParser.getData(this.getActivity());

            for (GroceryItem groceryItem : items) {
                if (groceryItem.getId() == id) {
                    item = groceryItem;
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(item.getName());

        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        quantity.setText(item.getQuantity() + "");

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(item.getDescription());

        image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(getDrawable(item));

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, ACTION_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_CODE) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(bitmap);
        }
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
