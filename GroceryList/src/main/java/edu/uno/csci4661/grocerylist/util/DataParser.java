package edu.uno.csci4661.grocerylist.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.model.GroceryItem;
import edu.uno.csci4661.grocerylist.model.ItemWrapper;

public class DataParser {

    public static List<GroceryItem> getData(Context context) throws IOException {
        Gson gson = new Gson();
        String jsonOutput = readJsonFile(context);
        ItemWrapper items = gson.fromJson(jsonOutput, ItemWrapper.class);

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

}
