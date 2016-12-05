package tech.linard.android.inventoryapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Blob;

import tech.linard.android.inventoryapp.MainActivity;
import tech.linard.android.inventoryapp.data.ProductContract.ProductEntry;
import tech.linard.android.inventoryapp.R;

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
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        byte[] image =  cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
        int onSale = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ON_SALE));

        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        TextView txtPrice = (TextView) view.findViewById(R.id.txt_price);
        TextView txtQuantity = (TextView) view.findViewById(R.id.txt_quantity);
        ImageView imgSale = (ImageView) view.findViewById(R.id.sale_button);
        if (onSale == 0) {
            imgSale.setVisibility(View.GONE);

        } else {
            imgSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "SALE!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        txtName.setText(name);
        txtPrice.setText(Double.toString(price));
        txtQuantity.setText(Integer.toString(quantity));
    }
}
