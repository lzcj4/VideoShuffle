package com.nero.videoshuffle.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nlang on 15/11/24.
 */
public class MyDBOpenHelper extends SQLiteOpenHelper {

    public final String TAG = this.getClass().getSimpleName();
    public static final int VERSION = 1;
    private static final String DBFILE = "Fruit.db";

    public MyDBOpenHelper(Context context) {
        this(context, DBFILE);
    }

    public MyDBOpenHelper(Context context, String file) {
        super(context, file, null, VERSION);
    }

    private final String SQL_CREATE_TB_APPLE = " CREATE TABLE IF NOT EXISTS tb_apple (" +
            " _id integer PRIMARY KEY AUTOINCREMENT ," +
            " name varchar(50) not null," +
            " price DECIMAL," +
            " amount DECIMAL" +
            " );";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TB_APPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Log.i(TAG, " My database upgrade");
        }

    }
}
