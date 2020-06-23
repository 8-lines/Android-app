package com.example.testapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText mFullName, mPier, mDateStart, mDateFinish;
    TextView mListHeader;
    ListView userList;
    TextView header;
    DB db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);
        db.open();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        mFullName = (EditText) findViewById(R.id.fullName);
        mPier = (EditText) findViewById(R.id.pier);
        mDateStart = (EditText) findViewById(R.id.dateStart);
        mDateFinish = (EditText) findViewById(R.id.dateFinish);

        // получаем данные из полей ввода
        String name = mFullName.getText().toString();
        String pier = mPier.getText().toString();
        String date_start = mDateStart.getText().toString();
        String date_finish = mDateFinish.getText().toString();
        int int_pier;

        switch (v.getId()) {
            case R.id.btnAdd:
                // вставляем запись c ненулевым ID
                if (pier == "")
                    break;
                else
                    int_pier = Integer.parseInt(pier);
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                db.addRec(int_pier, name, date_start, date_finish);
                Log.d(LOG_TAG, "row inserted, ID = " + pier);
                break;
            case R.id.btnUpd:
                // обновляем запись c ненулевым ID
                if (pier == "")
                    break;
                else
                    int_pier = Integer.parseInt(pier);
                Log.d(LOG_TAG, "--- Update mytable: ---");
                db.updRec(int_pier, name, date_start, date_finish);
                Log.d(LOG_TAG, "row updated, ID = " + pier);
                break;
            case R.id.btnCln:
                mFullName.setText("");
                mPier.setText("");
                mDateStart.setText("");
                mDateFinish.setText("");
                break;
            case R.id.btnLoad:
                Log.d(LOG_TAG, "--- Updating BD ---");
                //получаем данные из бд в виде курсора
                userCursor = db.getAllData();
                header = (TextView)findViewById(R.id.listHeader);
                userList = (ListView)findViewById(R.id.listMain);

                // id столбцов
                final int id_index = userCursor.getColumnIndex("_id");
                final int name_index = userCursor.getColumnIndex("name");
                final int date1_index = userCursor.getColumnIndex("date_start");
                final int date2_index = userCursor.getColumnIndex("date_finish");

                //Список клиентов
                ArrayList<HashMap<String, Object>> clients = new ArrayList<>();
                //Список параметров конкретного клиента
                HashMap<String, Object> client;

                //Пробегаем по всем клиентам
                try {
                    System.err.println("Starting!");
                    userCursor.moveToFirst();
                    while (!userCursor.isAfterLast()) {
                        client = new HashMap<>();
                        final long tId = userCursor.getLong(id_index);
                        final String tName = userCursor.getString(name_index);
                        final String tDate1 = userCursor.getString(date1_index);
                        final String tDate2 = userCursor.getString(date2_index);
                        client.put("_id", tId);
                        client.put("name", tName);
                        client.put("date_start", tDate1);
                        client.put("date_finish", tDate2);

                        //Закидываем запись в список
                        clients.add(client);

                        //Переходим к следующему клиенту
                        userCursor.moveToNext();
                    }
                    System.err.println("OK!");
                } catch (Exception e) { // catch по Exception ПЕРЕХВАТЫВАЕТ RuntimeException
                    System.err.print("ERROR!");
                }
                userCursor.close();
                String[] from = { "_id", "name", "date_start", "date_finish"};
                int[] to = { R.id.textView, R.id.textView2, R.id.textView3, R.id.textView4};
                SimpleAdapter adapter = new SimpleAdapter(this, clients, R.layout.adapter_item, from, to);
                userList.setAdapter(adapter);
                Log.d(LOG_TAG, "--- Update finished ---");
                break;
            case R.id.btnClear:
                // удаляем все записи
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                int cnt = db.delAll();
                Log.d(LOG_TAG, "--- Mytable cleared " + cnt + " rows ---");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }


}