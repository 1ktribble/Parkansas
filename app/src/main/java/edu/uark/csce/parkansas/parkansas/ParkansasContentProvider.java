package edu.uark.csce.parkansas.parkansas;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class ParkansasContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "edu.uark.csce.parkansas.parkansas.parkansasprovider";
    public static final String URL = "content://" + PROVIDER_NAME + "/parkansasdata";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String KEY_ID = "_id";
    public static final String KEY_ALARM_NAME = "_alarmName";
    public static final String KEY_ALARM_TYPE = "_alarmType";
//    public static final String KEY_ALARM_NAME = "_alarmName";
    public static final String KEY_ALARM_TIME = "_alarmTime";
    public static final String KEY_ALARM_TIME_HOUR = "_alarmTimeHour";
    public static final String KEY_ALARM_TIME_MINUTE = "_alarmTimeMinute";
    public static final String KEY_ALARM_TIME_DAY = "_alarmTimeDay";
    public static final String KEY_ALARM_ON = "_alarmOn";
    public static final String KEY_DATE = "_date";

    protected static HashMap<String, String> parkansasContentMap;

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private static final int ALLROWS = 1;
    private static final int SINGLEROW = 2;

    private static UriMatcher mURI_MATCHER;
    static{
        mURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        mURI_MATCHER.addURI(PROVIDER_NAME, "parkansasdata", ALLROWS);
        mURI_MATCHER.addURI(PROVIDER_NAME, "parkansasdata/#", SINGLEROW);
    }

    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//
    public static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "parkansasLocal.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "parkansasTable";

        /* 1.1 Boolean Datatype

        SQLite does not have a separate Boolean storage class. Instead, Boolean values are stored as
         integers 0 (false) and 1 (true).*/

        private static final String DATABASE_CREATE_CMD =
                "create table " + DATABASE_TABLE + "(" + KEY_ID +
                        " integer primary key, " +
                        KEY_ALARM_NAME + " text, " +
                        KEY_ALARM_TYPE + " text, " +
                        KEY_ALARM_TIME + " text not null, " +
                        KEY_ALARM_TIME_HOUR + " int, " +
                        KEY_DATE + " int, " +
                        KEY_ALARM_TIME_MINUTE + " int, " +
                        KEY_ALARM_ON + " int not null, " +
                        KEY_ALARM_TIME_DAY + " text not null);"
                ;
        private static final String DATABASE_DROP_CMD =
                "drop table if it exists " + DATABASE_TABLE;

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                  int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_CMD);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("TODOPROVIDER", "Upgrading from version " + oldVersion +
                            " to " + newVersion + ". All data will be deleted."
            );
            db.execSQL(DATABASE_DROP_CMD);
            db.execSQL(DATABASE_CREATE_CMD);
        }
    }
    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//

    public ParkansasContentProvider() {}

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        switch (mURI_MATCHER.match(uri)) {
            case SINGLEROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default:
                break;
        }

        if (selection == null) {
            selection = "1";
        }

        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE,
                selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch(mURI_MATCHER.match(uri)){
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.uark.parkansas";
            case SINGLEROW:
                return "vnd.android.cursor.item/vnd.uark.parkansas";
            default:
                throw new IllegalArgumentException("Unsupported URI: "+ uri);
        }    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();

        String nullColHack = null;

        @SuppressWarnings("static-access")
        long id = db.insert(mySQLiteOpenHelper.DATABASE_TABLE,
                nullColHack, values);

        if(id > -1)
        {
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mySQLiteOpenHelper = new MySQLiteOpenHelper(getContext(),
                MySQLiteOpenHelper.DATABASE_NAME,
                null,
                MySQLiteOpenHelper.DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db;
        try{
            db = mySQLiteOpenHelper.getWritableDatabase();
        } catch(SQLiteException ex) {
            db = mySQLiteOpenHelper.getReadableDatabase();
        }

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(mURI_MATCHER.match(uri)) {
            case SINGLEROW:
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowID);
                break;
            case ALLROWS:
                queryBuilder.setProjectionMap(parkansasContentMap);
            default:
                break;
        }

        queryBuilder.setTables(mySQLiteOpenHelper.DATABASE_TABLE);

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        switch (mURI_MATCHER.match(uri)) {
            case SINGLEROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID;
                if (!TextUtils.isEmpty(selection)) {
                    String appendString = " and (" + selection + ')';
                    selection += appendString;
                }
            default:
                break;
        }

        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}
