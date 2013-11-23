package edu.uno.csci4661.grocerylist.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by danielward on 10/31/13.
 */
public class GroceryProvider extends ContentProvider {

    public static final String AUTHORITY = "edu.uno.csci4661.grocerylist";

    // the primary content URI
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/items");

    // database columns
    public static final String KEY_ID = "_id";
    public static final String KEY_REMOTE_ID = "remote_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_QUANTITY = "quantity";

    //Create the constants used to differentiate between the different URI
    //requests.
    private static final int ITEMS = 1;
    private static final int ITEM_ID = 2;

    // the URI matcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("edu.uno.csci4661.grocerylist", "items", ITEMS);
        uriMatcher.addURI("edu.uno.csci4661.grocerylist", "items/#", ITEM_ID);
    }

    private GroceryDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();

        dbHelper = new GroceryDatabaseHelper(context);

        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sort) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(GroceryDatabaseHelper.ITEM_TABLE);

        // If this is a row query, limit the result set to the passed in row.
        switch (uriMatcher.match(uri)) {
            case ITEM_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }

        // If no sort order is specified, sort by date / time
        String orderBy;
        if (TextUtils.isEmpty(sort)) {
            orderBy = KEY_ID;
        } else {
            orderBy = sort;
        }

        // Apply the query to the underlying database.
        Cursor c = qb.query(database,
                projection,
                selection, selectionArgs,
                null, null,
                orderBy);

        // Register the contexts ContentResolver to be notified if
        // the cursor result set changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return a cursor to the query result.
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ITEMS:
                return "vnd.android.cursor.dir/edu.uno.csci4661.grocerylist";
            case ITEM_ID:
                return "vnd.android.cursor.item/edu.uno.csci4661.grocerylist";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(GroceryDatabaseHelper.ITEM_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        Uri result = null;
        if (id > 0) {
            result = Uri.withAppendedPath(CONTENT_URI, "" + id);
        }

        return result;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    //Helper class for opening, creating, and managing database version control
    private static class GroceryDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "earthquakes.db";
        private static final int DATABASE_VERSION = 1;
        private static final String ITEM_TABLE = "items";

        private static final String DATABASE_CREATE =
                "create table " + ITEM_TABLE + " ("
                        + KEY_ID + " integer primary key autoincrement, "
                        + KEY_REMOTE_ID + " TEXT UNIQUE, "
                        + KEY_NAME + " TEXT, "
                        + KEY_DESCRIPTION + " TEXT, "
                        + KEY_QUANTITY + " INTEGER);";

        public GroceryDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
            onCreate(db);
        }
    }
}
