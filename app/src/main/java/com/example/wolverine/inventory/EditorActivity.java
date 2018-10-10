package com.example.wolverine.inventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wolverine.inventory.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentUri;

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mSupplierName;
    private EditText mSupplierPhone;

    private boolean mProductHasChanged = false;

    private ImageButton mQuantityIncrementBtn;
    private ImageButton mQuantityDecrementBtn;
    private Button CallBtn;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if (mCurrentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhone = (EditText) findViewById(R.id.edit_supplier_phone);

        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);

        mQuantityDecrementBtn = (ImageButton) findViewById(R.id.Quantity_Decrease);
        mQuantityDecrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemQuantity = 1;
                try {
                    if (mProductQuantity.getText() != null ) {
                        itemQuantity = Integer.valueOf(mProductQuantity.getText().toString());
                        if(itemQuantity <= 0 ){
                            Toast.makeText(EditorActivity.this, "You can`t have negative number in Quantity"
                                    , Toast.LENGTH_SHORT).show();
                        }else {
                            itemQuantity--;
                        }
                    }
                } catch (NumberFormatException e) {
                    itemQuantity = 1;
                }
                    mProductQuantity.setText(Integer.toString(itemQuantity));
            }
        });

        mQuantityIncrementBtn = (ImageButton) findViewById(R.id.Quantity_Increase);
        mQuantityIncrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemQuantity = 0;

                try {
                    if (mProductQuantity.getText() != null) {
                        itemQuantity = Integer.valueOf(mProductQuantity.getText().toString());
                    }
                } catch (NumberFormatException e) {
                    itemQuantity = 0;
                }
                itemQuantity++;
                mProductQuantity.setText(Integer.toString(itemQuantity));
            }
        });

        CallBtn = (Button)findViewById(R.id.callSupplier);
        CallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPhoneNumber = mSupplierPhone.getText().toString().trim();
                if(!mPhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",mPhoneNumber , null));
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Number Phone need",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mQuantityDecrementBtn.setOnTouchListener(mTouchListener);
        mQuantityIncrementBtn.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveProduct() {
        String productName = mProductName.getText().toString().trim();
        String productPrice = mProductPrice.getText().toString().trim();
        String productQuantity = mProductQuantity.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierPhone = mSupplierPhone.getText().toString().trim();

        if (productName.isEmpty()) {
            mProductName.setError(getString(R.string.Name_Required));
            Toast.makeText(this,getString(R.string.Name_Required),Toast.LENGTH_SHORT).show();
            return;
        }
        if (productPrice.isEmpty()) {
            mProductPrice.setError(getString(R.string.Price_Required));
            Toast.makeText(this,getString(R.string.Price_Required),Toast.LENGTH_SHORT).show();
            return;
        }
        if (productQuantity.isEmpty()) {
            mProductQuantity.setError(getString(R.string.Quantity_Required));
            Toast.makeText(this,getString(R.string.Quantity_Required),Toast.LENGTH_SHORT).show();
            return;
        }
        if (supplierName.isEmpty()) {
            mSupplierName.setError(getString(R.string.supplier_name_Required));
            Toast.makeText(this,getString(R.string.supplier_name_Required),Toast.LENGTH_SHORT).show();
            return;
        }
        if (supplierPhone.isEmpty()) {
            mSupplierPhone.setError(getString(R.string.supplier_phone_Required));
            Toast.makeText(this,getString(R.string.supplier_phone_Required),Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int itemPrice = Integer.parseInt(productPrice);
        } catch (NumberFormatException e) {
            Toast.makeText(this,getString(R.string.Enter_Valid_number),Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int itemQuantity = Integer.parseInt(productQuantity);
        } catch (NumberFormatException e) {
            Toast.makeText(this,getString(R.string.Enter_Valid_number),Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int itemPrice = Integer.parseInt(supplierPhone);
        } catch (NumberFormatException e) {
            Toast.makeText(this,getString(R.string.Enter_Valid_number),Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(InventoryEntry.PRODUCT_NAME, productName);
        values.put(InventoryEntry.PRODUCT_PRICE, productPrice);
        values.put(InventoryEntry.PRODUCT_QUANTITY, productQuantity);
        values.put(InventoryEntry.SUPPLIER_NAME, supplierName);
        values.put(InventoryEntry.SUPPLIER_PHONE, supplierPhone);

        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_Product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {

        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.PRODUCT_NAME,
                InventoryEntry.PRODUCT_PRICE,
                InventoryEntry.PRODUCT_QUANTITY,
                InventoryEntry.SUPPLIER_NAME,
                InventoryEntry.SUPPLIER_PHONE};
        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_PHONE);

            String ProductName = cursor.getString(productNameColumnIndex);
            int price = cursor.getInt(productPriceColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mProductName.setText(ProductName);
            mProductPrice.setText(Integer.toString(price));
            mProductQuantity.setText(Integer.toString(productQuantity));
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }
}
