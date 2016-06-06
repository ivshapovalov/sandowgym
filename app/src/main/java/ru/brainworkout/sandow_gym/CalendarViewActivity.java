package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
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

    private boolean mTrainingIsNew;
    private Boolean mIsBeginDate;

    private String mOldDateFrom;
    private String mNewDate;
    private String mOldDateTo;

    private int mCurrentTraningID;
    private String mCurrentActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();

        mIsBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);
        mCurrentActivity = intent.getStringExtra("CurrentActivity");
        mCurrentTraningID = intent.getIntExtra("CurrentTrainingID", 0);
        try {
            mOldDateFrom = intent.getStringExtra("CurrentDate");
        } catch (Exception e) {
            mOldDateFrom = "";
        }
        try {
            mOldDateTo = intent.getStringExtra("CurrentDateTo");
        } catch (Exception e) {
            mOldDateTo = "";
        }

        if (mIsBeginDate || mCurrentActivity == "TrainingActivity") {
            if (mOldDateFrom != null && !"".equals(mOldDateFrom)) {
                Date d = Common.ConvertStringToDate(mOldDateFrom);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateFrom;
            }

        } else {
            if (mOldDateTo != null && !"".equals(mOldDateTo)) {
                Date d = Common.ConvertStringToDate(mOldDateTo);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateTo;
            }
        }


        if (mNewDate==null || mNewDate.equals("")) {
            mNewDate=Common.ConvertDateToString(Common.ConvertStringToDate(new StringBuilder().append(calendar.getTime().getYear()+1900)
                    .append("-").append(calendar.getTime().getMonth() + 1).append("-").append(calendar.getTime().getDate())
                    .append("").toString()));

        }
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis(), true, false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                mNewDate = Common.ConvertDateToString(Common.ConvertStringToDate(new StringBuilder().append(mYear)
                        .append("-").append(mMonth + 1).append("-").append(mDay)
                        .append("").toString()));
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
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentID", mCurrentTraningID);
        if (mIsBeginDate) {
            intent.putExtra("CurrentDate", mNewDate);
            intent.putExtra("CurrentDateTo", mOldDateTo);
        } else {
            intent.putExtra("CurrentDate", mOldDateFrom);
            intent.putExtra("CurrentDateTo", mNewDate);
        }
        intent.putExtra("", mIsBeginDate);
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
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentID", mCurrentTraningID);
        intent.putExtra("CurrentDate", mOldDateFrom);
        intent.putExtra("CurrentDateTo", mOldDateTo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
