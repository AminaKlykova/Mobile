package com.example.mobile;

import static com.example.mobile.DBHelper.FIO;
import static com.example.mobile.DBHelper.KEY_ID;
import static com.example.mobile.DBHelper.TABLE_CONTACT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Map<Integer, Integer> allContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.allContact = new HashMap<>();
        ListView listView = findViewById(R.id.lvMain);
        var dbHelper = new DBHelper(this);
        var cursor = dbHelper.getWritableDatabase().query(TABLE_CONTACT, null, null, null, null, null, FIO);
        var out = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex(KEY_ID);
            int index = cursor.getColumnIndex(FIO);
            int i = 0;
            do {
                this.allContact.put(i, Integer.parseInt(cursor.getString(id)));
                out[i] = cursor.getString(index);
                i++;
            } while (cursor.moveToNext());
        } else {
            Log.d("mLog", "БД пустая");
        }
        cursor.close();
        dbHelper.close();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, out));
        listView.setOnItemClickListener((parent, v, position, id) -> { //Обработчик нажатие на список контактов
            var intent = new Intent(MainActivity.this, ViewContact.class);
            intent.putExtra("id", Objects.requireNonNull(this.allContact.get(position)).toString());
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Создать контакт");
        menu.add(0, 2, 0, "Импорт контактов");
        menu.add(0, 3, 0, "Экспорт контактов");
        menu.add(0, 4, 0, "Выход");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                this.addContact();
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                finish();
                return true;
            default:
                return true;
        }
    }

    public void addContact(View view) {
        this.addContact();
    }

    private void addContact() { //Запуск формы создание контакта
        var intent = new Intent(MainActivity.this, AddContact.class);
        startActivity(intent);
    }
}