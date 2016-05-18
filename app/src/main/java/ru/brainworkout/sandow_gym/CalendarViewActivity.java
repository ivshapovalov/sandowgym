package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ivan on 17.05.2016.
 */
public class CalendarViewActivity extends AppCompatActivity {

    //private String mCurrentDate;
    private boolean mTrainingIsNew;
    // private int mCurrentID;
    private Training mTrainingCurrent;
    private Training mTrainingNew;
    private String mCurrentActivity;

    private boolean isChecked;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();

        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);
        mCurrentActivity = intent.getStringExtra("CurrentActivity");
        mTrainingCurrent = intent.getParcelableExtra("CurrentTraining");

        if (mTrainingCurrent != null & mTrainingCurrent.getDay() != null) {

            Date d = mTrainingCurrent.getDay();

            if (d != null) {
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
            }
        }

        try {
            mTrainingCurrent.setDay(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mTrainingNew = new Training(mTrainingCurrent.getID(), mTrainingCurrent.getDay());

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis(), true, false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                mTrainingNew.setDayString(new StringBuilder().append(mYear)
                        .append("-").append(mMonth + 1).append("-").append(mDay)
                        .append("").toString());
            }
        });


    }


    public void btSave_onClick(View view) {
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + "." + mCurrentActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(CalendarViewActivity.this, myClass);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentTraining", mTrainingNew);
//        intent.putExtra("CurrentID", mCurrentID);
//        intent.putExtra("CurrentDate", mCurrentDate);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(View view) {
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + "." + mCurrentActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(CalendarViewActivity.this, myClass);
        //Intent intent = new Intent(CalendarViewActivity.this, TrainingActivity.class);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentTraining", mTrainingCurrent);
//        intent.putExtra("CurrentID", mCurrentID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
