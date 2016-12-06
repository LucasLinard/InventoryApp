package tech.linard.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tech.linard.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by lucas on 04/12/16.
 */

public class ProductDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ProductDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;


    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            final String PRIMARY_KEY = " PRIMARY KEY ";
            final String AUTOINCREMENT = " AUTOINCREMENT ";
            final String NOT_NULL  = " NOT NULL ";
            final String TEXT = " TEXT ";
            final String INTEGER = " INTEGER ";
            final String DOUBLE = " DOUBLE ";
            final String BLOB = " BLOB ";
            final String DEFAULT = " DEFAULT ";

            String SQL_CREATE_PODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME
                    + " ( "+ ProductEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT
                    + ", " + ProductEntry.COLUMN_PRODUCT_NAME+ TEXT + NOT_NULL
                    + ", " + ProductEntry.COLUMN_PRODUCT_PRICE+ DOUBLE + DEFAULT + " 0.0 "
                    + ", " + ProductEntry.COLUMN_PRODUCT_QUANTITY + INTEGER + DEFAULT + " 0 "
                    + ", " + ProductEntry.COLUMN_PRODUCT_IMAGE + BLOB
                    + ", " + ProductEntry.COLUMN_PRODUCT_ON_SALE + INTEGER +  DEFAULT + " 0 "
                    + ", " + ProductEntry.COLUMN_PRODUCT_SUPPLIER + TEXT +  DEFAULT + " 0 "
                    + ");"
                    ;
            sqLiteDatabase.execSQL(SQL_CREATE_PODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
