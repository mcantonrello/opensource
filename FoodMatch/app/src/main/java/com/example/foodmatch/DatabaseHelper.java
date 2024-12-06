package com.example.foodmatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "FoodMatch.db";
    public static final String TABLE_USER = "user_table";
    public static final String TABLE_SHOPPING_LIST = "shopping_list_table";

    // Columnas de la tabla de usuarios
    public static final String USER_COL_1 = "USER_ID";
    public static final String USER_COL_2 = "EMAIL";
    public static final String USER_COL_3 = "PASSWORD";

    // Columnas de la tabla de la lista de la compra
    public static final String SHOPPING_LIST_COL_1 = "ITEM_ID";
    public static final String SHOPPING_LIST_COL_2 = "USER_ID";
    public static final String SHOPPING_LIST_COL_3 = "ITEM_NAME";
    public static final String SHOPPING_LIST_COL_4 = "QUANTITY";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_SHOPPING_LIST + " (ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, ITEM_NAME TEXT, QUANTITY INTEGER, FOREIGN KEY (USER_ID) REFERENCES " + TABLE_USER + "(USER_ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        onCreate(db);
    }

    // Métodos CRUD para usuarios
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_2, email);
        contentValues.put(USER_COL_3, password);
        long result = db.insert(TABLE_USER, null, contentValues);
        return result != -1;
    }

    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE EMAIL=?", new String[]{email});
    }

    public boolean isEmailUnique(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE EMAIL = ?", new String[]{email});
        boolean isUnique = !cursor.moveToFirst(); // Si no encuentra registros, el correo es único
        cursor.close();
        return isUnique;
    }
}
