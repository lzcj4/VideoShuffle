package com.nero.videoshuffle.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nlang on 15/11/24.
 */
public class FruitColumn {


    public static interface Apple extends BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + MyContentProvider.AUTHORITIES + "/apple");
        public static final String TABLE_NAME = "tb_apple";

        public static final String COL_NAME = "name";
        public static final String COL_PRICE = "price";
        public static final String COL_AMOUNT = "amount";


    }
}
