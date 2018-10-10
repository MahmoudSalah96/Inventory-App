package com.example.wolverine.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.wolverine.inventory.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "Inventory.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.PRODUCT_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.PRODUCT_QUANTITY + " INTEGER, "
                + InventoryEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryEntry.SUPPLIER_PHONE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_INVENTORY_ITEM_ENTRY =
                "DELETE FROM " + InventoryEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_INVENTORY_ITEM_ENTRY);
    }
}
