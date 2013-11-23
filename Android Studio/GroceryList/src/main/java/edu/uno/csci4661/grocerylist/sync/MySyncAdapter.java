package edu.uno.csci4661.grocerylist.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.util.List;

import edu.uno.csci4661.grocerylist.CloudEndpointUtils;
import edu.uno.csci4661.grocerylist.R;
import edu.uno.csci4661.grocerylist.auth.AuthActivity;
import edu.uno.csci4661.grocerylist.database.GroceryProvider;
import edu.uno.csci4661.grocerylist.groceryitemendpoint.Groceryitemendpoint;
import edu.uno.csci4661.grocerylist.groceryitemendpoint.model.CollectionResponseGroceryItem;
import edu.uno.csci4661.grocerylist.groceryitemendpoint.model.GroceryItem;

/**
 * Created by danielward on 11/17/13.
 */
public class MySyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver resolver;
    private AccountManager mAccountManager;

    public static final String SYNC_ADAPTER_FINISHED_INTENT = "edu.uno.csci4661.sync.syncadapter.sync_finished";

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
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d("grocery_list", "tried to sync");
        try {

            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingAudience(
                            getContext(),
                            AuthActivity.AUDIENCE
                    );

            credential.setSelectedAccountName(account.name);
            credential.getToken();


            Groceryitemendpoint mGroceryitemendpoint = CloudEndpointUtils.updateBuilder(
                    new Groceryitemendpoint.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),
                            credential
                    )
            ).setApplicationName(getContext().getResources().getString(R.string.app_name)).build();


            CollectionResponseGroceryItem collection = mGroceryitemendpoint.listGroceryItem().execute();

            List<GroceryItem> items = collection.getItems();
            if (items != null) {
                ContentValues[] bulkItems = new ContentValues[items.size()];

                for (int i = 0; i < items.size(); i++) {
                    GroceryItem item = items.get(i);

                    ContentValues values = new ContentValues();
                    values.put(GroceryProvider.KEY_REMOTE_ID, item.getId());
                    values.put(GroceryProvider.KEY_NAME, item.getName());
                    values.put(GroceryProvider.KEY_DESCRIPTION, item.getDescription());
                    values.put(GroceryProvider.KEY_QUANTITY, item.getQuantity());

                    bulkItems[i] = values;
                }

                contentProviderClient.bulkInsert(GroceryProvider.CONTENT_URI, bulkItems);
            }
            getContext().sendBroadcast(new Intent(MySyncAdapter.SYNC_ADAPTER_FINISHED_INTENT));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
}
