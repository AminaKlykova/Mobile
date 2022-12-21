package com.example.mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "catalog";
    public static final String TABLE_CONTACT = "contact";

    public static final String KEY_ID = "_id";
    public static final String FIO = "FIO";
    public static final String PHONE = "Phone";
    public static final String IMAGE = "Image";
    public static final String EMAIL = "Email";
    public static final String ADDRESS = "Address";
    public static final String OTHER = "Other";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACT + " ( " + KEY_ID + " INTEGER PRIMARY KEY, " + FIO + " TEXT, "
                + PHONE + " TEXT, " + IMAGE + " TEXT, " + EMAIL + " TEXT," + ADDRESS + " TEXT, " + OTHER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS plugin");
        onCreate(db);
    }
}