package com.example.wolverine.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wolverine.inventory.data.InventoryContract.InventoryEntry;


/**
 * Created by wolverine on 12/09/18.
 */

public class InventoryCursorLoader extends CursorAdapter {
    private Context mContext;

    public InventoryCursorLoader(Context context, Cursor c) {
        super(context, c, 0 );
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView product_name = (TextView) view.findViewById(R.id.product_txt);
        TextView price = (TextView) view.findViewById(R.id.price_txt);
        final TextView quantity = (TextView) view.findViewById(R.id.quantity_txt);
        Button buyBtn = (Button)view.findViewById(R.id.Sell_button);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String priceValue = cursor.getString(priceColumnIndex);
        final String inventoryQuantity = cursor.getString(quantityColumnIndex);
        //final int[] itemQuantity = {Integer.parseInt(inventoryQuantity)};
        final Integer Quan = cursor.getInt(quantityColumnIndex);
        product_name.setText(productName);
        price.setText(priceValue);
        quantity.setText(inventoryQuantity);

        /*buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemQuantity[0] == 0) {
                    Toast.makeText(mContext, "Item is out of Stock!", Toast.LENGTH_SHORT).show();
                } else {
                    itemQuantity[0]--;
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.PRODUCT_QUANTITY, itemQuantity[0]);
                    quantity.setText(itemQuantity[0] + "");
                    Uri currentItemUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI,
                            getItemId(cursor.getPosition()) +"");
                    mContext.getContentResolver().update(currentItemUri,
                            values,null,null);
                }
            }
        });*/

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (view != null) {
                        Object obj = view.getTag();
                        String st = obj.toString();
                        ContentValues values = new ContentValues();
                        values.put(InventoryEntry.PRODUCT_QUANTITY, Quan >= 1? Quan-1: 0);
                        Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, Integer.parseInt(st));

                        int rowsAffected = mContext.getContentResolver().update(currentPetUri, values, null, null);
                        if (rowsAffected == 0 || Quan == 0) {
                            Toast.makeText(mContext,"Item is out of Stock!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
        Object obj = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        buyBtn.setTag(obj);

    }
}
