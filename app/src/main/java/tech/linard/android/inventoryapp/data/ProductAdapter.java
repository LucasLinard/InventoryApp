package tech.linard.android.inventoryapp.data;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tech.linard.android.inventoryapp.R;
import tech.linard.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by lucas on 04/12/16.
 */

public class ProductAdapter extends CursorAdapter {
    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //name price quantity image
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        final String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        final double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final byte[] image =  cursor.getBlob(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
        final int onSale = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ON_SALE));
        final String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER));

        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        TextView txtPrice = (TextView) view.findViewById(R.id.txt_price);
        TextView txtQuantity = (TextView) view.findViewById(R.id.txt_quantity);
        ImageView imgSale = (ImageView) view.findViewById(R.id.sale_button);
        imgSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;

                    Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, (long) productId);

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity );
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_ON_SALE, onSale);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, image);

                    int rowsAffected = view.getContext().getContentResolver().update(currentProductUri, values, null, null);
                    if (rowsAffected > 0) {
                        Toast.makeText(view.getContext(), "Product SOLD!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtName.setText(name);
        txtPrice.setText(Double.toString(price));
        txtQuantity.setText(Integer.toString(quantity));
    }
}
