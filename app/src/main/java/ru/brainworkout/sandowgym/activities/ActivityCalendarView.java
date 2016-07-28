package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.R;

public class ActivityCalendarView extends AppCompatActivity {


    private boolean mIsBeginDate;

    private String mOldDateFrom;
    private String mNewDate;
    private String mOldDateTo;

    private boolean mCallerIsNew;
    private int mCallerTrainingID;
    private int mCallerExerciseID;
    private int mCallerWeightChangeCalendarID;
    private String mCallerActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        getIntentParams();

        SetParametersOnScreen();

    }


    private void getIntentParams() {

        Intent intent = getIntent();

        mIsBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mCallerIsNew = intent.getBooleanExtra("IsNew", false);
        mCallerActivity = intent.getStringExtra("CurrentActivity");
        mCallerTrainingID = intent.getIntExtra("CurrentTrainingID", 0);
        mCallerExerciseID = intent.getIntExtra("CurrentExerciseID",0);
        mCallerWeightChangeCalendarID=intent.getIntExtra("CurrentWeightChangeCalendarID",0);

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

    }
    private void SetParametersOnScreen() {

        Calendar calendar = Calendar.getInstance();

        if (mIsBeginDate || mCallerActivity == "ActivityTraining") {
            if (mOldDateFrom != null && !"".equals(mOldDateFrom)) {
                Date d = Common.ConvertStringToDate(mOldDateFrom,Common.DATE_FORMAT_STRING);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateFrom;
            }

        } else {
            if (mOldDateTo != null && !"".equals(mOldDateTo)) {
                Date d = Common.ConvertStringToDate(mOldDateTo,Common.DATE_FORMAT_STRING);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateTo;
            }
        }

        if (mNewDate==null || mNewDate.equals("")) {
            mNewDate=Common.ConvertDateToString(Common.ConvertStringToDate(new StringBuilder().append(calendar.getTime().getYear()+1900)
                    .append("-").append(calendar.getTime().getMonth() + 1).append("-").append(calendar.getTime().getDate())
                    .append("").toString(), Common.DATE_FORMAT_STRING), Common.DATE_FORMAT_STRING);

        }
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis(), true, false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                mNewDate = Common.ConvertDateToString(Common.ConvertStringToDate(new StringBuilder().append(year)
                        .append("-").append(month + 1).append("-").append(dayOfMonth)
                        .append("").toString(),Common.DATE_FORMAT_STRING),Common.DATE_FORMAT_STRING);
            }
        });

        Common.setTitleOfActivity(this);
    }




    public void btSave_onClick(final View view) {

        Common.blink(view);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("IsNew", mCallerIsNew);
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentTrainingID", mCallerTrainingID);
        intent.putExtra("CurrentExerciseID", mCallerExerciseID);
        intent.putExtra("CurrentWeightChangeCalendarID", mCallerWeightChangeCalendarID);
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

    public void btClose_onClick(final View view) {

        Common.blink(view);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("IsNew", mCallerIsNew);
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentTrainingID", mCallerTrainingID);
        intent.putExtra("CurrentExerciseID", mCallerExerciseID);
        intent.putExtra("CurrentWeightChangeCalendarID", mCallerWeightChangeCalendarID);
        intent.putExtra("CurrentDate", mOldDateFrom);
        intent.putExtra("CurrentDateTo", mOldDateTo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
