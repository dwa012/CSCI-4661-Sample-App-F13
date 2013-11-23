package edu.uno.csci4661.grocerylist.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import edu.uno.csci4661.grocerylist.database.GroceryProvider;

/**
 * http://blog.tomtasche.at/2013/05/google-oauth-on-android-using.html
 */
public class AuthActivity extends Activity {

    public static final String ACCOUNT_TYPE = "com.google";
    private static final int AUTHORIZATION_CODE = 1993;
    private static final int ACCOUNT_CODE = 1601;

    private AuthPreferences authPreferences;
    private AccountManager accountManager;

    /**
     * Match this to the scope define in the AppEngine server
     */
    public static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    /**
     * Replace the AUDIENCE_ID in the string with the value defined in the server
     */
    public static final String AUDIENCE = "server:client_id:AUDIENCE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);

        authPreferences = new AuthPreferences(this);
        if (authPreferences.getUser() != null && authPreferences.getToken() != null) {
            // left blank
        } else {
            chooseAccount();
        }
    }

    private void chooseAccount() {
        // use https://github.com/frakbot/Android-AccountChooser for
        // compatibility with older devices
        Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[] { "com.google" }, false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    private void requestToken() {
        Account userAccount = null;
        String user = authPreferences.getUser();
        for (Account account : accountManager.getAccountsByType("com.google")) {
            if (account.name.equals(user)) {
                userAccount = account;
                break;
            }
        }

        accountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, this, new OnTokenAcquired(), null);
    }

    /**
     * call this method if your token expired, or you want to request a new
     * token for whatever reason. call requestToken() again afterwards in order
     * to get a new token.
     */
    private void invalidateToken() {
        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken("com.google", authPreferences.getToken());

        authPreferences.setToken(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AUTHORIZATION_CODE) {
                requestToken();
            } else if (requestCode == ACCOUNT_CODE) {
                String accountName = data
                        .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                authPreferences.setUser(accountName);

                // invalidate old tokens which might be cached. we want a fresh
                // one, which is guaranteed to work
                invalidateToken();

                requestToken();
            }
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                    final Account account = new Account(bundle.getString(AccountManager.KEY_ACCOUNT_NAME), bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));

                    authPreferences.setToken(token);

                    ContentResolver.setIsSyncable(account, GroceryProvider.AUTHORITY, 1);

                    // Inform the system that this account is eligible for auto sync when the network is up
                    ContentResolver.setSyncAutomatically(account, GroceryProvider.AUTHORITY, true);

                    AuthActivity.this.finish();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
