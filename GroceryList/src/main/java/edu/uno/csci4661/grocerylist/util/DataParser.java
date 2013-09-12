package edu.uno.csci4661.grocerylist.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;

public class DataParser {

    public static List<GroceryItem> getData(Context context) throws IOException {
        Gson gson = new Gson();
        String jsonOutput = readJsonFile(context);
        Type listType = new TypeToken<ItemWrapper>() {
        }.getType();
        ItemWrapper items = gson.fromJson(jsonOutput, listType);

        return items.getItems();
    }

    private static String readJsonFile(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.data);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        String jsonString = writer.toString();

        return jsonString;
    }

    private class ItemWrapper {
        private List<GroceryItem> items;

        public List<GroceryItem> getItems() {
            return items;
        }

        public void setItems(List<GroceryItem> items) {
            this.items = items;
        }
    }
}
