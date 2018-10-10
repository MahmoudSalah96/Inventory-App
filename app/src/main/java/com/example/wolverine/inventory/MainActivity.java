package com.example.wolverine.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wolverine.inventory.data.InventoryContract.InventoryEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    InventoryCursorLoader mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView productListView = (ListView) findViewById(R.id.List);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mAdapter = new InventoryCursorLoader(this, null);
        productListView.setAdapter(mAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries :
                deleteAllInventoryItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.PRODUCT_NAME, "Milk");
        values.put(InventoryEntry.PRODUCT_PRICE, 4);
        values.put(InventoryEntry.PRODUCT_QUANTITY, 7);
        values.put(InventoryEntry.SUPPLIER_NAME, "Mahmoud Salah");
        values.put(InventoryEntry.SUPPLIER_PHONE, 01274767);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    private void deleteAllInventoryItem() {
        if(mAdapter.isEmpty())
            Toast.makeText(this, R.string.No_data,Toast.LENGTH_SHORT).show();
        else {
            int rowDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
            Toast.makeText(this, R.string.Cleared,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.PRODUCT_NAME,
                InventoryEntry.PRODUCT_PRICE,
                InventoryEntry.PRODUCT_QUANTITY};

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
