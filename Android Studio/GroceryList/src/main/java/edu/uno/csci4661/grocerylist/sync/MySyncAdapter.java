package edu.uno.csci4661.grocerylist.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by danielward on 11/17/13.
 */
public class MySyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver resolver;

    public MySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        init(context);
    }

    public MySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        init(context);
    }

    private void init(Context context) {
        resolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d("grocery_list", "tried to sync");

    }
}
