package com.example.testapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    int int_pier;
    long row;
    String name, pier, date_start, date_finish;
    //Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText mFullName, mPier, mDateStart, mDateFinish;
    ListView userList;
    //TextView header;
    DB db;
    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new DB(this);
        db.open();
        refresh();
    }

    public void onClick(View v) {

        // получаем текущие данные из полей ввода
        get_data();

        // скрываем клавиатуру
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        switch (v.getId()) {
            case R.id.btnAdd:
                // вставляем запись c ненулевым ID
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                if (pier.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка: укажите номер пирса",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "--- Inserting failed, null ID ---");
                    break;
                }
                int_pier = Integer.parseInt(pier);
                row = db.addRec(int_pier, name, date_start, date_finish);
                if (row == -1)
                {
                    Log.d(LOG_TAG, "--- Inserting failed, incorrect ID ---");
                    Toast.makeText(getApplicationContext(),"Ошибка: запись уже существует",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                refresh();
                Log.d(LOG_TAG, "row inserted, ID = " + row);
                Toast.makeText(getApplicationContext(), "Запись добавлена",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnUpd:
                // обновляем запись c ненулевым ID
                Log.d(LOG_TAG, "--- Update in mytable: ---");
                if (pier.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка: укажите номер пирса",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "--- Updating failed, null ID ---");
                    break;
                }
                int_pier = Integer.parseInt(pier);
                row = db.updRec(int_pier, name, date_start, date_finish);
                Log.d(LOG_TAG, "--- Updating ---" + row);
                if (row == 0)
                {
                    Log.d(LOG_TAG, "--- Updating failed, incorrect ID ---");
                    Toast.makeText(getApplicationContext(),"Ошибка: запись не существует",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                refresh();
                Log.d(LOG_TAG, "row updated, ID = " + row);
                Toast.makeText(getApplicationContext(), "Запись обновлена",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnCln:
                mFullName.setText("");
                mPier.setText("");
                mDateStart.setText("");
                mDateFinish.setText("");
                break;
            case R.id.btnClear:
                // удаляем все записи
                // запрашиваем подтверждение
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.show(manager, "myDialog");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    // При нажатии "ОК" очищаем таблицу
    public void okClicked() {
        Log.d(LOG_TAG, "--- Clear mytable: ---");
        int cnt = db.delAll();
        refresh();
        Toast.makeText(getApplicationContext(), "Удалено " + cnt + " записей",
                Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "--- Mytable cleared " + cnt + " rows ---");
    }

    // Или отменяем операцию
    public void cancelClicked() {
        Toast.makeText(getApplicationContext(), "Отмена",
                Toast.LENGTH_LONG).show();
    }

    // Получаем информацию с заполненных полей
    public void get_data() {
        mFullName = findViewById(R.id.fullName);
        mPier = findViewById(R.id.pier);
        mDateStart = findViewById(R.id.dateStart);
        mDateFinish = findViewById(R.id.dateFinish);

        // получаем данные из полей ввода
        name = mFullName.getText().toString();
        pier = mPier.getText().toString();
        date_start = mDateStart.getText().toString();
        date_finish = mDateFinish.getText().toString();
    }

    // Обновляем отображаемый список
    public void refresh(){
        //получаем данные из бд в виде курсора
        userCursor = db.getAllData();
        //header = (TextView)findViewById(R.id.listHeader);
        userList = findViewById(R.id.listMain);

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
        userList.setAdapter(adapter);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }


}