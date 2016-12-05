package tech.linard.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;

import tech.linard.android.inventoryapp.data.ProductAdapter;
import tech.linard.android.inventoryapp.data.ProductContract.ProductEntry;
import tech.linard.android.inventoryapp.data.ProductDBHelper;

public class MainActivity extends AppCompatActivity {
    ProductDBHelper productDBHelper;
    ListView listView;
    ProductAdapter mAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                // insertDummyData();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cursor.close();
    }
    private void displayDatabaseItems() {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_ON_SALE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER
        };
        cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null);
        listView = (ListView) findViewById(R.id.list_products);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        mAdapter = new ProductAdapter(this, cursor);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        byte[] bytes = new byte[100];
        Arrays.fill( bytes, (byte) 0 );
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Android Programming");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, bytes);
        productDBHelper = new ProductDBHelper(this);
        SQLiteDatabase db = productDBHelper.getWritableDatabase();
        long result = db.insert(ProductEntry.TABLE_NAME, null, values);
        if (result == -1) {
            Toast.makeText(this, "INSERT UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "INSERT SUCCESSFUL with ID of " + result, Toast.LENGTH_SHORT).show();
        }
    }
}
