package edu.uno.csci4661.grocerylist.receivers;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.auth.AuthActivity;
import edu.uno.csci4661.grocerylist.auth.AuthPreferences;
import edu.uno.csci4661.grocerylist.database.GroceryProvider;

/**
 * Created by danielward on 11/18/13.
 */
public class ItemAddedReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat)
                        .setContentTitle("New Grocery Item Added")
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(1234567, mBuilder.build());

        AuthPreferences authPreferences = new AuthPreferences(context);
        ContentResolver.requestSync(new Account(authPreferences.getUser(), AuthActivity.ACCOUNT_TYPE), GroceryProvider.AUTHORITY, new Bundle());
    }
}
