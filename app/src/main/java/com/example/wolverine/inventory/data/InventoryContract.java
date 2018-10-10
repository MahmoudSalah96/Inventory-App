package com.example.wolverine.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wolverine on 12/09/18.
 */

public final class InventoryContract {
    public InventoryContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.wolverine.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventorys";

    public static final class InventoryEntry implements BaseColumns {
        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "product_name";
        public final static String PRODUCT_PRICE = "price";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String SUPPLIER_NAME = "supplier_name";
        public final static String SUPPLIER_PHONE = "supplier_phone_number";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}
