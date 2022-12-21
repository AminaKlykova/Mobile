package com.example.mobile.plugin.impl;

import static com.example.mobile.DBHelper.ADDRESS;
import static com.example.mobile.DBHelper.EMAIL;
import static com.example.mobile.DBHelper.FIO;
import static com.example.mobile.DBHelper.IMAGE;
import static com.example.mobile.DBHelper.KEY_ID;
import static com.example.mobile.DBHelper.OTHER;
import static com.example.mobile.DBHelper.PHONE;
import static com.example.mobile.DBHelper.TABLE_CONTACT;
import static com.example.mobile.plugin.Plugin.CONTACT_ID;
import static com.example.mobile.plugin.Plugin.ID;
import static com.example.mobile.plugin.Plugin.NAME_FIELD;
import static com.example.mobile.plugin.Plugin.TABLE_NAME;
import static com.example.mobile.plugin.Plugin.VALUE_FIELD;
import static org.junit.Assert.assertNotEquals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.LinearLayout;

import com.example.mobile.DBHelper;
import com.example.mobile.plugin.Plugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PluginImplTest {
    private Plugin plugin;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        var builder = new SQLiteDatabase.OpenParams.Builder();
        var context = Mockito.mock(Context.class);
        this.plugin = new PluginImpl(context);
        this.db = SQLiteDatabase.createInMemory(builder.build());
        this.db.execSQL("CREATE TABLE " + TABLE_CONTACT + "_test ( " + KEY_ID + " INTEGER PRIMARY KEY, "
                + FIO + " TEXT, " + PHONE + " TEXT, " + IMAGE + " TEXT, " + EMAIL + " TEXT," + ADDRESS +
                " TEXT, " + OTHER + " TEXT)");
        this.db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "_test ( " + ID + " INTEGER PRIMARY KEY, "
                + NAME_FIELD + " TEXT, " + VALUE_FIELD + " TEXT, " + CONTACT_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + CONTACT_ID + ") REFERENCES " + TABLE_CONTACT + " (" + DBHelper.KEY_ID + "))");
    }

    @After
    public void tearDown() {
        this.db.close();
    }

    @Test
    public void save() {
        var id = this.setContact();
        this.setPlugin(id);
        var layout = Mockito.mock(LinearLayout.class);
        var countChild = layout.getChildCount();
        this.plugin.getPlugin(layout, false, String.valueOf(id));
        assertNotEquals(countChild, layout.getChildCount());
    }

    @Test
    public void addPlugin() {
        var id = this.setContact();
        this.setPlugin(id);
        var layout = Mockito.mock(LinearLayout.class);
        var countChild = layout.getChildCount();
        this.plugin.addPlugin(layout);
        assertNotEquals(countChild, layout.getChildCount());
    }

    @Test
    public void getPlugin() {
        var id = this.setContact();
        this.setPlugin(id);
        var layout = Mockito.mock(LinearLayout.class);
        var countChild = layout.getChildCount();
        this.plugin.getPlugin(layout, false, String.valueOf(id));
        assertNotEquals(countChild, layout.getChildCount());
    }

    @Test(expected = SQLiteException.class)
    public void delete() {
        var id = this.setContact();
        this.setPlugin(id);
        this.plugin.delete(String.valueOf(id));
        this.plugin.getPlugin(Mockito.mock(LinearLayout.class), false, String.valueOf(id));
    }

    private long setContact() {
        var contentValues = new ContentValues();
        contentValues.put(FIO, "Тестовый Иван Иванович");
        contentValues.put(IMAGE, "path");
        contentValues.put(EMAIL, "test.ivan@test.com");
        contentValues.put(ADDRESS, "РФ");
        contentValues.put(OTHER, "Дополнительная информация");
        return this.db.insert(TABLE_CONTACT + "_test", null, contentValues);
    }

    private void setPlugin(long contactId) {
        var contentValues = new ContentValues();
        contentValues.put(NAME_FIELD, "тестовое поле");
        contentValues.put(VALUE_FIELD, "тестовое значение");
        contentValues.put(CONTACT_ID, contactId);
        this.plugin.save(contactId);
    }
}