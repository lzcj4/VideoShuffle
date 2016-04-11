package com.nero.videoshuffle.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nero.videoshuffle.provider.Apple;
import com.nero.videoshuffle.provider.FruitColumn;

import java.util.ArrayList;
import java.util.List;

public class ScheduleReceiver extends BroadcastReceiver {
    public ScheduleReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(FruitColumn.Apple.COL_NAME, "Apple 1");
        cv.put(FruitColumn.Apple.COL_PRICE, 25);
        cv.put(FruitColumn.Apple.COL_AMOUNT, 14);
        Uri uri = cr.insert(FruitColumn.Apple.CONTENT_URI, cv);
        Log.i("content provide", uri.toString());

        Cursor cursor = cr.query(FruitColumn.Apple.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        List<Apple> list = new ArrayList<>();
        try {
            do {

                Apple item = new Apple();
                item.Name = cursor.getString(cursor.getColumnIndex(FruitColumn.Apple.COL_NAME));
                item.Price = cursor.getDouble(cursor.getColumnIndex(FruitColumn.Apple.COL_PRICE));
                item.Amount = cursor.getDouble(cursor.getColumnIndex(FruitColumn.Apple.COL_AMOUNT));
                list.add(item);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }

    }
}
