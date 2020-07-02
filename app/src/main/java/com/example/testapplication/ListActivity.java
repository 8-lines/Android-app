package com.example.testapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    ListView pierList;
    DB db;
    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DB(this);
        db.open();
        refresh();
    }

    // Обновляем отображаемый список
    public void refresh(){
        //получаем данные из бд в виде курсора
        userCursor = db.getAllData();
        //header = (TextView)findViewById(R.id.listHeader);
        pierList = findViewById(R.id.listMain);

        // id столбцов
        final int id_index = userCursor.getColumnIndex("_id");
        final int status_index = userCursor.getColumnIndex("status");
        final int name_index = userCursor.getColumnIndex("name");
        final int date1_index = userCursor.getColumnIndex("date_start");
        final int date2_index = userCursor.getColumnIndex("date_finish");

        //Список клиентов
        ArrayList<HashMap<String, Object>> records = new ArrayList<>();
        //Список параметров конкретного клиента
        HashMap<String, Object> record;

        //Пробегаем по всем записям
        try {
            userCursor.moveToFirst();
            while (!userCursor.isAfterLast()) {
                record = new HashMap<>();
                final long tId = userCursor.getLong(id_index);
                final String tStatus = userCursor.getString(status_index);
                final String tName = userCursor.getString(name_index);
                final String tDate1 = userCursor.getString(date1_index);
                final String tDate2 = userCursor.getString(date2_index);

                record.put("_id", "Пирс №" + tId);
                record.put("status", "Статус: " + tStatus);
                record.put("name", "ФИО: " + tName);
                record.put("date_start", "С: " + tDate1);
                record.put("date_finish", "До: " + tDate2);

                //Закидываем запись в список
                records.add(record);

                //Переходим к следующему клиенту
                userCursor.moveToNext();
            }
        } catch (Exception e) { // catch по Exception ПЕРЕХВАТЫВАЕТ RuntimeException
            System.err.print("ERROR!");
        }
        userCursor.close();
        String[] from = { "_id", "status", "name", "date_start", "date_finish"};
        int[] to = { R.id.textPier, R.id.textStatus, R.id.textName, R.id.textDate1, R.id.textDate2};
        SimpleAdapter adapter = new SimpleAdapter(this, records,
                R.layout.adapter_item, from, to);
        pierList.setAdapter(adapter);
    }
}