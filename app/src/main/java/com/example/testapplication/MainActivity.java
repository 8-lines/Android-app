package com.example.testapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    int int_pier;
    long row;
    EditText mID, mFullName, mTimeStart, mTimeFinish;
    String name, time_start, time_finish, str_id;
    int rec_id;
    ListView userList;
    TextView mPier, mDate;
    DB db;
    Cursor userCursor;
    String cDate, cPier, tmpDate, tmpPier;

    //Список записей
    ArrayList<HashMap<String, Object>> records;
    //Список параметров конкретной записи
    HashMap<String, Object> record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mID = findViewById(R.id.rec_id);
        mPier = findViewById(R.id.pierSet);
        mDate = findViewById(R.id.dateSet);
        mFullName = findViewById(R.id.fullName);
        mTimeStart = findViewById(R.id.timeStart);
        mTimeFinish = findViewById(R.id.timeFinish);
        userList = findViewById(R.id.listMain);

        Intent intent = getIntent();
        cPier = intent.getStringExtra("cPier");
        cDate = intent.getStringExtra("cDate");
        tmpPier = "Пирс №" + cPier;
        tmpDate = "Дата: " + cDate;
        mPier.setText(tmpPier);
        mDate.setText(tmpDate);


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new DB(this);
        db.open();
        refresh();

        // обрабатываем нажатия на пункты списка
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(),"itemClick: " + records.get(position)
                //        + ", id = " + id, Toast.LENGTH_SHORT).show();
                copyRec(records.get(position).toString());
            }

        });



    }

    public void onClick(View v) throws ParseException {

        // получаем текущие данные из полей ввода
        get_data();

        // скрываем клавиатуру
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        switch (v.getId()) {
            case R.id.btnAdd:
                // вставляем запись c ненулевыми полями
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                if (name.equals("") || time_start.equals("") || time_finish.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка: заполните все поля (кроме ID)",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "--- Inserting failed ---");
                    break;
                }
                int_pier = Integer.parseInt(cPier);
                // если запись затрагивает два дня
                if (isCurrDay(time_start, time_finish)) {
                    row = db.addRec(int_pier, cDate, name, time_start, time_finish);
                }
                else
                {
                    row = db.addRec(int_pier, cDate, name, time_start, "-");
                    Log.d(LOG_TAG, "row inserted, ID = " + row);
                    String dateNew = nextDate(cDate);
                    row = db.addRec(int_pier, dateNew, name, "-", time_finish);
                }
                Log.d(LOG_TAG, "row inserted, ID = " + row);
                refresh();
                Toast.makeText(getApplicationContext(), "Запись добавлена",
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.btnUpd:
                str_id = mID.getText().toString();
                // обновляем запись c ненулевым ID
                Log.d(LOG_TAG, "--- Update in mytable: ---");
                if (str_id.equals("") || name.equals("") ||  time_start.equals("") ||
                        time_finish.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка: заполните все поля",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "--- Inserting failed ---");
                    break;
                }
                int_pier = Integer.parseInt(cPier);
                rec_id = Integer.parseInt(str_id);
                row = db.updRec(rec_id, int_pier, cDate, name, time_start, time_finish);
                Log.d(LOG_TAG, "--- Updating ---" + row);
                if (row == 0)
                {
                    Log.d(LOG_TAG, "--- Updating failed, incorrect ID ---");
                    Toast.makeText(getApplicationContext(),"Ошибка: запись с таким ID " +
                                    "не существует",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                refresh();
                Log.d(LOG_TAG, "row updated, ID = " + row);
                Toast.makeText(getApplicationContext(), "Запись обновлена",
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.btnCln:
                // очищаем поля
                mID.setText("");
                //mPier.setText("");
                //mDate.setText("");
                mFullName.setText("");
                mTimeStart.setText("");
                mTimeFinish.setText("");
                break;

            /* case R.id.btnClear:
                // удаляем все записи
                // запрашиваем подтверждение
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.show(manager, "myDialog");
                break; */
            case R.id.btnRm:
                // удаляем запись по ID
                str_id = mID.getText().toString();
                // удаляем запись c ненулевым ID
                Log.d(LOG_TAG, "--- Update in mytable: ---");
                if (str_id.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка: укажите ID (и только ID)",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "--- Deleting failed ---");
                    break;
                }
                rec_id = Integer.parseInt(str_id);
                row = db.delRec(rec_id);
                Log.d(LOG_TAG, "--- Deleting --- " + row);
                if (row == 0)
                {
                    Log.d(LOG_TAG, "--- Deleting failed, incorrect ID ---");
                    Toast.makeText(getApplicationContext(),"Ошибка: запись с таким ID " +
                                    "не существует",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                refresh();
                Log.d(LOG_TAG, "row deleted, ID = " + row);
                Toast.makeText(getApplicationContext(), "Запись удалена",
                        Toast.LENGTH_LONG).show();
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
        // получаем данные из полей ввода
        //pier = mPier.getText().toString();
        //date = mDate.getText().toString();
        name = mFullName.getText().toString();
        time_start = mTimeStart.getText().toString();
        time_finish = mTimeFinish.getText().toString();
    }

    // Обновляем отображаемый список
    public void refresh(){
        //получаем данные из бд в виде курсора
        userCursor = db.getDayPierData(cPier, cDate);
        //header = (TextView)findViewById(R.id.listHeader);

        // id столбцов
        final int id_index = userCursor.getColumnIndex("_id");
        final int pier_index = userCursor.getColumnIndex("pier");
        final int date_index = userCursor.getColumnIndex("date");
        final int name_index = userCursor.getColumnIndex("name");
        final int time1_index = userCursor.getColumnIndex("time_start");
        final int time2_index = userCursor.getColumnIndex("time_finish");

        records = new ArrayList<>();

        //Пробегаем по всем записям
        try {
            userCursor.moveToFirst();
            while (!userCursor.isAfterLast()) {
                record = new HashMap<>();
                final int tId = userCursor.getInt(id_index);
                final int tPier = userCursor.getInt(pier_index);
                final String tDate = userCursor.getString(date_index);
                final String tName = userCursor.getString(name_index);
                final String tTime1 = userCursor.getString(time1_index);
                final String tTime2 = userCursor.getString(time2_index);


                record.put("_id", "ID: " + tId);
                record.put("pier", "Пирс №" + tPier);
                record.put("date", "Дата: " + tDate);
                record.put("name", "ФИО: " + tName);
                record.put("time_start", "С: " + tTime1);
                record.put("time_finish", "До: " + tTime2);

                //Закидываем запись в список
                records.add(record);

                //Переходим к следующему клиенту
                userCursor.moveToNext();
            }
        } catch (Exception e) { // catch по Exception ПЕРЕХВАТЫВАЕТ RuntimeException
            Log.d(LOG_TAG, "--- Unknown error! ---");
            System.err.print("ERROR!");
        }
        userCursor.close();
        String[] from = { "_id", "name", "time_start", "time_finish"};
        int[] to = { R.id.textID, R.id.textName, R.id.textTime1,
                R.id.textTime2};
        SimpleAdapter adapter = new SimpleAdapter(this, records,
                R.layout.adapter_item, from, to);
        userList.setAdapter(adapter);
    }

    public void copyRec(String str) {
        String[] parts = str.split(", ");
        String time2 = parts[1].split(" ")[1];
        String time1 = parts[2].split(" ")[1];
        String id = parts[4].split(" ")[1];
        String tmp = parts[5].split(": ")[1];
        String name = tmp.substring(0, tmp.length() - 1);
        mID.setText(id);
        mFullName.setText(name);
        mTimeStart.setText(time1);
        mTimeFinish.setText(time2);
    }

    public boolean isCurrDay(String time1, String time2) {
        int hour1 = Integer.parseInt(time1.split(":")[0]);
        int min1 = Integer.parseInt(time1.split(":")[1]);
        int hour2 = Integer.parseInt(time2.split(":")[0]);
        int min2 = Integer.parseInt(time2.split(":")[1]);

        // если следующий день
        if ((hour1 > hour2) || (hour1 == hour2 && min1 >= min2)) {
            Log.d(LOG_TAG, "--- NextDay --- " + hour1 + "" + hour2);
            return false;
        }
        Log.d(LOG_TAG, "--- CurrDay ---");
        return true;
    }

    public String nextDate(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat =
                new SimpleDateFormat("dd/MM/yyyy");
        Date myDate = myFormat.parse(date);
        Date tempDate = DateUtil.addDays(myDate, 1);
        return myFormat.format(tempDate);
    }

    public static class DateUtil
    {
        public static Date addDays(Date date, int days)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days); //minus number would decrement the days
            return cal.getTime();
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