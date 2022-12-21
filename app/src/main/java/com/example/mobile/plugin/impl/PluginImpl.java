package com.example.mobile.plugin.impl;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mobile.DBHelper;
import com.example.mobile.plugin.Fields;
import com.example.mobile.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginImpl implements Plugin {
    private final Context context;
    private final List<EditText> plugins; // Список с добавленными плагинами
    private final List<EditText> newPlugins; //Список с плагинами, которые надо будет добавить

    public PluginImpl(Context context) {
        this.plugins = new ArrayList<>();
        this.newPlugins = new ArrayList<>();
        this.context = context;
        try (var dbHelper = new DBHelper(this.context)) {
            var sql = dbHelper.getReadableDatabase();
            sql.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " ( " + ID + " INTEGER PRIMARY KEY, " + NAME_FIELD + " TEXT, " + VALUE_FIELD +
                    " TEXT, " + CONTACT_ID + " INTEGER NOT NULL," + "FOREIGN KEY (" + CONTACT_ID +
                    ") REFERENCES " + DBHelper.TABLE_CONTACT + " (" + DBHelper.KEY_ID + "))");
        }
    }

    @Override
    public void save(long contactId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try (var db = new DBHelper(this.context)) {
                var sql = db.getReadableDatabase();
                this.plugins.forEach(p -> { //Обновление старых плагинов
                    var contentValues = new ContentValues();
                    contentValues.put(VALUE_FIELD, p.getText().toString());
                    sql.update(TABLE_NAME, contentValues, NAME_FIELD + " = "
                            + p.getHint().toString() + " AND " + CONTACT_ID + " = " + contactId, null);
                });

                this.newPlugins.forEach(n -> { //Сохранение новых плагинов
                    var contentValues = new ContentValues();
                    contentValues.put(CONTACT_ID, contactId);
                    contentValues.put(NAME_FIELD, n.getHint().toString());
                    contentValues.put(VALUE_FIELD, n.getText().toString());
                    sql.insert(TABLE_NAME, null, contentValues);
                });
            }
        }
    }

    @Override
    public void addPlugin(LinearLayout linearLayout) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            var fields = Arrays.stream(Fields.values())
                    .map(Fields::getField)
                    .toArray(String[]::new);
            new AlertDialog.Builder(this.context) //Создание диалогового окна со списком плагинов
                    .setTitle("Выбериты дополнительное поле")
                    .setItems(fields, (dialog, which) -> { //Обработка выбранного из списка
                        var editText = this.buildEditText("", fields[which]);
                        linearLayout.addView(editText); //Добавляет в Layout
                        this.newPlugins.add(editText);
                    })
                    .show();
        }
    }

    @SuppressLint({"Recycle", "Range"})
    @Override
    public void getPlugin(LinearLayout linearLayout, boolean isEnabled, String... contactId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!this.plugins.isEmpty() || contactId[0] == null) return;
        }
        try (var db = new DBHelper(this.context)) {
            var sql = db.getReadableDatabase();
            var cursor = sql.query(TABLE_NAME, new String[]{NAME_FIELD, VALUE_FIELD},
                    CONTACT_ID + " = ?", contactId, null, null, null);
            if (cursor.moveToFirst()) {
                var nameField = cursor.getColumnIndex(NAME_FIELD);
                var valueField = cursor.getColumnIndex(VALUE_FIELD);
                do {
                    var editText = this.buildEditText(cursor.getString(valueField), cursor.getString(nameField));
                    editText.setEnabled(isEnabled);
                    linearLayout.addView(editText);
                    this.plugins.add(editText);
                } while (cursor.moveToNext());
            }
        }
    }

    @Override
    public void delete(String... contactId) {
        try (var db = new DBHelper(this.context)) {
            var sql = db.getReadableDatabase();
            sql.delete(TABLE_NAME, CONTACT_ID + " = ?", contactId);
        }
    }

    private EditText buildEditText(String text, String hint) {
        var editText = new EditText(this.context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        editText.setText(text);
        editText.setHint(hint);
        return editText;
    }
}
