package com.nero.videoshuffle.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Switch;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    private final UriMatcher mUriMatcher;
    public static final String AUTHORITIES = "com.nero.videoshuffle.myauthorities";
    private static final int APPLE_COLLECTION = 100;
    private static final int APPLE_SINGLE = 101;

    private static final String APPLE = "apple";
    private MyDBOpenHelper mDBHelper;
    private HashMap<Uri, String> mTableDic = new HashMap<>();

    {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String path = String.format("/%s", APPLE);
        mUriMatcher.addURI(AUTHORITIES, APPLE, APPLE_COLLECTION);
        mUriMatcher.addURI(AUTHORITIES, APPLE + "/#", APPLE_SINGLE);

        mTableDic.put(FruitColumn.Apple.CONTENT_URI, FruitColumn.Apple.TABLE_NAME);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public MyContentProvider() {

    }

    private SQLiteDatabase getDB() {
        if (mDBHelper == null) {
            mDBHelper = new MyDBOpenHelper(this.getContext());
        }
        return mDBHelper.getWritableDatabase();
    }

    private String getTableName(Uri uri) {
        String table = mTableDic.get(uri);
        return table;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final String GROUP_BASE = "vnd.android.cursor.dir";
    private final String SINGLE_BASE = "vnd.android.cursor.item";

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case APPLE_COLLECTION:
                return String.format("%s/%s", GROUP_BASE, APPLE);
            case APPLE_SINGLE:
                return String.format("%s/%s", SINGLE_BASE, APPLE);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = this.getDB();
        Uri itemUri;
        String table = getTableName(uri);
        long id = db.insert(table, null, values);

        switch (mUriMatcher.match(uri)) {
            case APPLE_COLLECTION:
                itemUri = ContentUris.withAppendedId(uri, id);
                break;
            case APPLE_SINGLE:
                String path = uri.toString();
                path = path.substring(0, path.lastIndexOf("/") + 1);
                itemUri = Uri.parse(path + id);
                break;
            default:
                throw new UnsupportedOperationException();

        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(itemUri, null);
        }
        return itemUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case APPLE_COLLECTION:
            case APPLE_SINGLE:

                return getDB().query(FruitColumn.Apple.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {

        switch (mUriMatcher.match(uri)) {
            case APPLE_SINGLE:
                long rowId = ContentUris.parseId(uri);
                int result = getDB().update(FruitColumn.Apple.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(rowId)});
                return result;
            default:
                throw new UnsupportedOperationException();
        }

    }
}
