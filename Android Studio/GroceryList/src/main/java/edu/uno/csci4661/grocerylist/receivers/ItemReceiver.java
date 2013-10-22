package edu.uno.csci4661.grocerylist.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ItemReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION = "edu.uno.csci4661.grocerylist.ITEM_BROADCAST";

    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            String text = intent.getExtras().getString(Intent.EXTRA_TEXT, "");
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }
}
