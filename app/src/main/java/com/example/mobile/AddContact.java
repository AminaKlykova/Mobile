package com.example.mobile;

import static com.example.mobile.DBHelper.ADDRESS;
import static com.example.mobile.DBHelper.EMAIL;
import static com.example.mobile.DBHelper.FIO;
import static com.example.mobile.DBHelper.KEY_ID;
import static com.example.mobile.DBHelper.OTHER;
import static com.example.mobile.DBHelper.PHONE;
import static com.example.mobile.DBHelper.TABLE_CONTACT;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.plugin.Plugin;
import com.example.mobile.plugin.impl.PluginImpl;

public class AddContact extends AppCompatActivity {
    private EditText name;
    private EditText number;
    private EditText email;
    private EditText address;
    private EditText other;
    private DBHelper dbHelper;
    private Plugin plugin;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        this.name = findViewById(R.id.name);
        this.number = findViewById(R.id.number);
        this.email = findViewById(R.id.email);
        this.address = findViewById(R.id.address);
        this.other = findViewById(R.id.other);
        this.dbHelper = new DBHelper(this);
        this.plugin = new PluginImpl(this);
        var id = this.getIntent().getStringExtra("id");
        if (id != null) {
            var db = this.dbHelper.getReadableDatabase();
            var cursor = db.query(TABLE_CONTACT, null, KEY_ID + " = ?",
                    new String[]{id}, null, null, null);
            if (cursor.moveToFirst()) {
                this.name.setText(cursor.getString(cursor.getColumnIndex(FIO)));
                this.number.setText(cursor.getString(cursor.getColumnIndex(PHONE)));
                this.email.setText(cursor.getString(cursor.getColumnIndex(EMAIL)));
                this.address.setText(cursor.getString(cursor.getColumnIndex(ADDRESS)));
                this.other.setText(cursor.getString(cursor.getColumnIndex(OTHER)));
            }
            cursor.close();
            db.close();
        }
    }

    public void save(View view) {
        var contentValues = new ContentValues();
        var db = dbHelper.getReadableDatabase();
        contentValues.put(FIO, this.name.getText().toString());
        contentValues.put(PHONE, this.number.getText().toString());
        contentValues.put(EMAIL, this.email.getText().toString());
        contentValues.put(ADDRESS, this.address.getText().toString());
        contentValues.put(OTHER, this.other.getText().toString());
        if (this.name.getText().toString().isEmpty()) {
            this.name.setError("Name is empty");
        } else {
            long id;
            if (this.getIntent().getStringExtra("id") == null) {
                id = db.insert(TABLE_CONTACT, null, contentValues);
            } else {
                id = Long.parseLong(this.getIntent().getStringExtra("id"));
                db.update(TABLE_CONTACT, contentValues, KEY_ID + " = " + id, null);
            }
            db.close();
            if (id > 0L) {
                this.plugin.save(id);
                var intent = new Intent(AddContact.this, ViewContact.class);
                intent.putExtra("id", String.valueOf(id));
                startActivity(intent);
                finish();
            } else {
                Log.d("mLog", "Не удалось добавить контакт");
            }
        }
    }

    public void cancel(View view) {
        finish();
    }


    public void addPlugin(View view) {
        this.plugin.getPlugin(findViewById(R.id.input), true, this.getIntent().getStringExtra("id"));
        this.plugin.addPlugin(findViewById(R.id.input));
    }
}