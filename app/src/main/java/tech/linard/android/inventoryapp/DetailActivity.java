package tech.linard.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import tech.linard.android.inventoryapp.data.ProductContract;
import tech.linard.android.inventoryapp.data.ProductDBHelper;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    // Views
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int intOnSale;
    private CheckBox mOnSale;
    private EditText  mProductName;
    private EditText  mProductPrice;
    private EditText  mProductQuantity;
    private Button mButtonAdd;
    private Button mButtonRemove;
    private Button mButtonContact;
    private ImageView mProductImage;
    private ProductDBHelper mPproductDBHelper;
    private EditText mSupplier;
    private byte[] mEncodedImage;
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
        setContentView(R.layout.activity_detail);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new PRODUCT or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        // assign views
        mButtonAdd = (Button) findViewById(R.id.btn_add);
        mButtonRemove = (Button) findViewById(R.id.btn_remove);
        mButtonContact = (Button) findViewById(R.id.btn_contact);
        mProductImage = (ImageView) findViewById(R.id.product_image);
        mProductName = (EditText) findViewById(R.id.name_edit);
        mProductPrice = (EditText) findViewById(R.id.price_edit);
        mProductQuantity = (EditText) findViewById(R.id.quantity_edit);
        mOnSale  = (CheckBox) findViewById(R.id.checkbox_on_sale);
        mSupplier = (EditText) findViewById(R.id.supplier_edit);
        mProductImage.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
        mButtonContact.setOnClickListener(this);
        mProductQuantity.setOnClickListener(this);
        mOnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSale.isChecked()) {
                    intOnSale = 1;
                } else {
                    intOnSale = 0;
                }
            }
        });

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_prd));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            readDataFromDB();
        }
    }

    private void readDataFromDB() {
        setTitle(getString(R.string.editor_activity_title_product_info));

        // assign listener
        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_ON_SALE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER
        };

        Cursor cursor = getContentResolver().query(mCurrentProductUri, projection, null, null, null);
        try {
            assert cursor != null;
            cursor.moveToPosition(0);

            String name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            byte[] image =  cursor.getBlob(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
            int onSale = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_ON_SALE));
            String supplier = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER));

            if (image != null) {
                  Bitmap bm = BitmapFactory.decodeByteArray(image, 0 ,image.length);
                  mProductImage.setImageBitmap(bm);
            }

            mProductName.setText(name);
            mProductPrice.setText(String.valueOf(price));
            mProductQuantity.setText(String.valueOf(quantity));
            mSupplier.setText(supplier);

            if (onSale == 1) {
                mOnSale.setChecked(true);
            } else {
                mOnSale.setChecked(false);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid fields", Toast.LENGTH_SHORT).show();
        }
        finally {
            assert cursor != null;
            if (cursor.getCount() == 0)
                cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new PRODUCT, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save PRODUCT to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the PRODUCT hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void increaseQtd() {
        int qtd = Integer.parseInt(mProductQuantity.getText().toString());
        qtd++;
        mProductQuantity.setText(String.valueOf(qtd));
        saveProduct();
    }
    private void decreaseQtd() {
        int qtd = Integer.parseInt(mProductQuantity.getText().toString());
        if (qtd >=1){
        qtd--;
        }
        mProductQuantity.setText(String.valueOf(qtd));
        saveProduct();
    }

    private void saveProduct() {
        String nameString = mProductName.getText().toString().trim();
        String priceString = mProductPrice.getText().toString().trim();
        String quantityString = mProductQuantity.getText().toString().trim();
        String supplier = mSupplier.getText().toString().trim();

        if (mCurrentProductUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(quantityString)
                ) {
            // nothing to save, return early
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        if (TextUtils.isEmpty(priceString)) {
            priceString = "0";
        }
        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
        }
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, Double.parseDouble(priceString));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantityString));
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_ON_SALE, intOnSale);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, mEncodedImage);

        if (mCurrentProductUri == null) {
            // new Product, call insert
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_prd_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_prd_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_prd_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_prd_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the PRODUCT.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this PRODUCT.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the PRODUCT.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the Product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_prd_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_prd_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                increaseQtd();
                break;
            case R.id.btn_remove:
                decreaseQtd();
                break;
            case R.id.btn_contact:
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:"+mSupplier.getText().toString()));
                startActivity(dial);
                break;
            case R.id.product_image:
                dispatchTakePictureIntent();
            }
        }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mProductImage.setImageBitmap(imageBitmap);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            mEncodedImage =  byteArray;

        }
    }
}
