package com.example.wolverine.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.wolverine.inventory.data.InventoryContract.InventoryEntry;


/**
 * Created by wolverine on 12/09/18.
 */

public class InventoryProvider extends ContentProvider {

    private InventoryDbHelper mDbHelper;

    private static final int INVENTORYS = 100;

    private static final int INVENTORY_ID = 101;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORYS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORYS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORYS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORYS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        String productName = contentValues.getAsString(InventoryEntry.PRODUCT_NAME);
        if (productName == null) {
            Toast.makeText(getContext(),"Product requires a name",Toast.LENGTH_SHORT).show();
        }

        Integer price = contentValues.getAsInteger(InventoryEntry.PRODUCT_PRICE);
        if ((price != null && price < 0)) {
            Toast.makeText(getContext(),"Product requires Valid Price",Toast.LENGTH_SHORT).show();
        }

        Integer quantity = contentValues.getAsInteger(InventoryEntry.PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            Toast.makeText(getContext(),"Product requires valid Quantity",Toast.LENGTH_SHORT).show();
        }

        String supplierName = contentValues.getAsString(InventoryEntry.SUPPLIER_NAME);
        if (supplierName == null) {
            Toast.makeText(getContext(),"Product requires a supplier name",Toast.LENGTH_SHORT).show();
        }

        Integer supplierPhone = contentValues.getAsInteger(InventoryEntry.SUPPLIER_PHONE);
        if ((supplierPhone != null && supplierPhone < 0)) {
            Toast.makeText(getContext(),"Product requires supplier Phone",Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORYS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORYS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(InventoryEntry.PRODUCT_NAME)) {
            String productName = contentValues.getAsString(InventoryEntry.PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (contentValues.containsKey(InventoryEntry.PRODUCT_PRICE)) {
            Integer price = contentValues.getAsInteger(InventoryEntry.PRODUCT_PRICE);
            if ((price != null && price < 0) || price == null) {
                throw new IllegalArgumentException("Product requires Valid Price");
            }
        }


        if (contentValues.containsKey(InventoryEntry.PRODUCT_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryEntry.PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid Quantity");
            }
        }

        if (contentValues.containsKey(InventoryEntry.SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(InventoryEntry.SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        if (contentValues.containsKey(InventoryEntry.SUPPLIER_PHONE)) {
            Integer supplierPhone = contentValues.getAsInteger(InventoryEntry.SUPPLIER_PHONE);
            if ((supplierPhone != null && supplierPhone < 0) || supplierPhone == null) {
                throw new IllegalArgumentException("Product requires supplier Phone");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
