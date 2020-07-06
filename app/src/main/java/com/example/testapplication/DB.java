package com.example.testapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

    // названия для БД
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "mytable";

    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PIER = "pier";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME1 = "time_start";
    public static final String COLUMN_TIME2 = "time_finish";

    // команда создания БД
    public static final String DB_CREATE = "CREATE TABLE mytable ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PIER + " INTEGER, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_TIME1 + " TEXT, "
            + COLUMN_TIME2 + " TEXT);";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
   /* public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }*/

    // получить данные из таблицы DB_TABLE за один день
    public Cursor getDayData(String currDate) {
        String selection = "date = ?";
        String[] selectionArgs = new String[] { currDate };
        return mDB.query(DB_TABLE, null, selection, selectionArgs, null, null, null, null);
    }

    public Cursor getDayPierData(String currPier, String currDate) {
        String selection = "date = ? AND pier = ?";
        String[] selectionArgs = new String[] { currDate, currPier };
        String orderBy = "time_start";
        return mDB.query(DB_TABLE, null, selection, selectionArgs, null, null, orderBy, null);
    }


    // добавить запись в DB_TABLE
    public long addRec(int pier, String date, String name, String time1, String time2) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PIER, pier);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_TIME1, time1);
        cv.put(COLUMN_TIME2, time2);
        return mDB.insert(DB_TABLE, null, cv);
    }

    // обновить запись в DB_TABLE по ID
    public long updRec(int id, int pier, String date, String name, String time1, String time2) {
        ContentValues cv = new ContentValues();
        if (name.equals("")) {
            name = "-";
        }
        if (time1.equals(""))
            time1 = "-";
        if (time2.equals(""))
            time2 = "-";
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_PIER, pier);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_TIME1, time1);
        cv.put(COLUMN_TIME2, time2);
        return mDB.update(DB_TABLE, cv, "_id = ?",
                new String[] { Integer.toString(id) });
    }


    // удалить запись из DB_TABLE по ID
     public int delRec(int id) {
        return mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // удалить все записи из DB_TABLE
    public int delAll() {
        int clearCount = mDB.delete(DB_TABLE, null, null);
        mDB.execSQL("DROP TABLE mytable;");
        mDB.execSQL(DB_CREATE);
        ContentValues cv = new ContentValues();
        for (int i = 1; i < 6; i++) {
            cv.put(COLUMN_ID, i);
            cv.put(COLUMN_PIER, i);
            cv.put(COLUMN_DATE, "08/08/2020");
            cv.put(COLUMN_NAME, "-");
            cv.put(COLUMN_TIME1, "-");
            cv.put(COLUMN_TIME2, "-");
            mDB.insert(DB_TABLE, null, cv);
        }
        return clearCount;
    }


    // класс по созданию и управлению БД
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            final String LOG_TAG = "myLogs";
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL(DB_CREATE);
            // добавление начальных данных
            ContentValues cv = new ContentValues();
            for (int i = 1; i < 6; i++) {
                cv.put(COLUMN_ID, i);
                cv.put(COLUMN_PIER, i);
                cv.put(COLUMN_DATE, "08/08/2020");
                cv.put(COLUMN_NAME, "-");
                cv.put(COLUMN_TIME1, "-");
                cv.put(COLUMN_TIME2, "-");
                db.insert(DB_TABLE, null, cv);
            }
        }

        // очистить БД

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}