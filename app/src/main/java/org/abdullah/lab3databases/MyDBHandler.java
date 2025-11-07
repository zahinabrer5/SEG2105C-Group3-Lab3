package org.abdullah.lab3databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_cmd = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " DOUBLE " + ")";

        db.execSQL(create_table_cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null); // returns "cursor" all products from the table
    }
	
	public Cursor findProducts(String name, String price) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor;

		boolean hasName = name != null && !name.isEmpty();
		boolean hasPrice = price != null && !price.isEmpty();

		if (hasName && !hasPrice) {
			// Find by name only (starts with)
			cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " LIKE ?", new String[]{name + "%"});
		} else if (!hasName && hasPrice) {
			// Find by price only (exact)
			cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_PRICE + " = ?", new String[]{price});
		} else if (hasName && hasPrice) {
			// Find by name and price (both)
			cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " LIKE ? AND " + COLUMN_PRODUCT_PRICE + " = ?", new String[]{name + "%", price});
		} else {
			// Show all
			cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		}

		return cursor;
	}


    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, product.getProductPrice());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Product findProduct(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        Product product = null;
        if (cursor.moveToFirst())
            product = new Product(cursor.getString(1), Double.parseDouble(cursor.getString(2)));
        cursor.close();
        db.close();
        return product;
    }

    public boolean deleteProduct(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        try {
            db.execSQL(query);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
