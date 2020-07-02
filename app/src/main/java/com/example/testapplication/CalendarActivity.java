package com.example.testapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class CalendarActivity extends AppCompatActivity {

    String tempDateText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        final TextView mDateText = findViewById(R.id.currDate);
        final CalendarView cv = findViewById(R.id.calendarView);

        // получаем текущую дату
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String currDate = sdf.format(cv.getDate());
        mDateText.setText(currDate);
        tempDateText = currDate;

        final NumberFormat formatter = new DecimalFormat("00");
        String s = formatter.format(1); // ----> 01

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            String date = currDate;

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                //String mYear = String.valueOf(year);
                String mYear = formatter.format(year);
                String mMonth = formatter.format(month+1);
                String mDay = formatter.format(dayOfMonth);

                String selectedDate = new StringBuilder().append(mDay)
                        .append("/").append(mMonth).append("/").append(mYear).toString();

                if (!date.equals(selectedDate)) {
                    date = selectedDate;
                    mDateText.setText(selectedDate);
                    tempDateText = selectedDate;
                }
            }
        });

    }

    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("cDate", tempDateText);
        startActivity(intent);
    }


}