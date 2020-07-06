package com.example.testapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    final int pierNumber = 15;
    GridView pierGrid;
    TextView mListDate;
    DB db;
    Cursor userCursor;
    String cDate;

    //Список пирсов
    ArrayList<HashMap<String, Object>> arrayList;
    HashMap<String, Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        cDate = intent.getStringExtra("cDate");
        mListDate = findViewById(R.id.listDate);
        String tempDate = "Дата: " + cDate;
        mListDate.setText(tempDate);

        pierGrid = findViewById(R.id.gridView);

        db = new DB(this);
        db.open();
        day_refresh();

        // обрабатываем нажатия на пункты списка
        pierGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(),"itemClick: " + arrayList.get(position)
                //        + ", id = " + id, Toast.LENGTH_SHORT).show();
                pierAdd(id+1);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        day_refresh();
    }


    // Обновляем отображаемый список
    public void day_refresh(){
        //Получаем данные из бд в виде курсора
        userCursor = db.getDayData(cDate);
        int[] pierArray = new int[pierNumber+1];

        //Массив для подсчета количества записей для каждого пирса
        for (int i = 1; i <= pierNumber; i++)
            pierArray[i] = 0;

        // id столбцов
        final int pier_index = userCursor.getColumnIndex("pier");

        //Пробегаем по всем записям
        try {
            userCursor.moveToFirst();
            while (!userCursor.isAfterLast()) {
                int tPier = userCursor.getInt(pier_index);
                pierArray[tPier]++;

                //Переходим к следующей записи
                userCursor.moveToNext();
            }
        } catch (Exception e) { // catch по Exception ПЕРЕХВАТЫВАЕТ RuntimeException
            Log.d(LOG_TAG, "--- Unknown error! ---");
            System.err.print("ERROR!");
        }
        userCursor.close();

        arrayList = new ArrayList<>();
        String[] from = new String[]{"Pier", "Number"};
        int[] to = new int[]{R.id.tvText1, R.id.tvText2};

        for (int i = 1; i <= pierNumber; i++) {
            map = new HashMap<>();
            map.put("Pier", "Пирс №" + i);
            map.put("Number", "Записей: " + pierArray[i]);
            arrayList.add(map);
        }

        SimpleAdapter pierAdapter = new SimpleAdapter(this, arrayList,
                R.layout.adapter_pier_item, from, to)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View itemView = super.getView(position, convertView, parent);
                int nmb = counter(getItem(position).toString());
                if (nmb == 0)
                    itemView.setBackgroundColor(Color.GREEN);
                if (nmb > 0 && nmb < 6)
                    itemView.setBackgroundColor(Color.YELLOW);
                if (nmb > 6)
                    itemView.setBackgroundColor(Color.RED);

                return itemView;
            }
        };

        // определяем список и присваиваем ему адаптер
        pierGrid.setAdapter(pierAdapter);

    }

    public int counter(String str)
    {
        String[] parts = ((str.split(","))[0].split(" "));
        String number = parts[1];
        return Integer.parseInt(number);
    }

    public void pierAdd(long pier)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("cPier", Long.toString(pier));
        intent.putExtra("cDate", cDate);
        startActivity(intent);
    }

}