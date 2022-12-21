package com.example.mobile;

import static com.example.mobile.DBHelper.ADDRESS;
import static com.example.mobile.DBHelper.EMAIL;
import static com.example.mobile.DBHelper.FIO;
import static com.example.mobile.DBHelper.KEY_ID;
import static com.example.mobile.DBHelper.OTHER;
import static com.example.mobile.DBHelper.PHONE;
import static com.example.mobile.DBHelper.TABLE_CONTACT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.plugin.Plugin;
import com.example.mobile.plugin.impl.PluginImpl;

public class ViewContact extends AppCompatActivity {
    private EditText name;
    private EditText number;
    private EditText email;
    private EditText address;
    private EditText other;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        this.name = findViewById(R.id.name);
        this.number = findViewById(R.id.number);
        this.email = findViewById(R.id.email);
        this.address = findViewById(R.id.address);
        this.other = findViewById(R.id.other);
        var db = new DBHelper(this).getReadableDatabase();
        var cursor = db.query(TABLE_CONTACT, null, KEY_ID + " = ?",
                new String[]{this.getIntent().getStringExtra("id")}, null, null, null);
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

    public void edit(View view) {
        var intent = new Intent(ViewContact.this, AddContact.class);
        intent.putExtra("id", this.getIntent().getStringExtra("id"));
        startActivity(intent);
    }

    public void back(View view) {
        var intent = new Intent(ViewContact.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void delete(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить контакт?")
                .setPositiveButton(R.string.Cancel, null)
                .setNegativeButton(R.string.Delete, (dialog, which) -> { //Обрабочик нажатие на удалить
                    try (var dbHelper = new DBHelper(this)) {
                        var db = dbHelper.getReadableDatabase();
                        db.delete(TABLE_CONTACT, "_id = ?", new String[]{getIntent().getStringExtra("id")});
                        Plugin plugin = new PluginImpl(this);
                        plugin.delete(getIntent().getStringExtra("id"));
                        db.close();
                        back(view);
                    } catch (Exception e) {
                        Log.d("mLog", e.getMessage());
                    }
                })
                .show();
    }

    public void viewPlugin(View view) {
        Plugin plugin = new PluginImpl(this);
        plugin.getPlugin(findViewById(R.id.view), this.getIntent().getStringExtra("id"));
        findViewById(R.id.plugin).setEnabled(false);
    }
}