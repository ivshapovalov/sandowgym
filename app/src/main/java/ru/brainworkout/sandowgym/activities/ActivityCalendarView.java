package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;

import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.R;

public class ActivityCalendarView extends AppCompatActivity {

    private boolean mIsBeginDate;

    private long mOldDateFromInMillis;
    private long mNewDateInMillis;
    private long mOldDateToInMillis;

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
            mOldDateFromInMillis = intent.getLongExtra("CurrentDateInMillis",0);
        } catch (Exception e) {
            mOldDateFromInMillis = 0;
        }
        try {
            mOldDateToInMillis = intent.getLongExtra("CurrentDateToInMillis",0);
        } catch (Exception e) {
            mOldDateToInMillis = 0;
        }

    }
    private void SetParametersOnScreen() {

        Calendar calendar = Calendar.getInstance();

        if (mIsBeginDate || mCallerActivity == "ActivityTraining") {
            if (mOldDateFromInMillis !=0) {
                calendar.setTimeInMillis(mOldDateFromInMillis);
                mNewDateInMillis = mOldDateFromInMillis;
            }

        } else {
            if (mOldDateToInMillis != 0 ) {
                calendar.setTimeInMillis(mOldDateToInMillis);
                mNewDateInMillis = mOldDateToInMillis;
            }
        }

        if (mNewDateInMillis ==0 ) {
            calendar.clear(Calendar.HOUR);
            calendar.clear(Calendar.HOUR_OF_DAY);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            mNewDateInMillis =calendar.getTimeInMillis();

        }
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(mNewDateInMillis, false, true);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(year,month,dayOfMonth,0,0,0);
                mNewDateInMillis =calendar.getTimeInMillis();

            }
        });

        setTitleOfActivity(this);
    }




    public void btSave_onClick(final View view) {

        blink(view,this);
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
            intent.putExtra("CurrentDateInMillis", mNewDateInMillis);
            intent.putExtra("CurrentDateToInMillis", mOldDateToInMillis);
        } else {
            intent.putExtra("CurrentDateInMillis", mOldDateFromInMillis);
            intent.putExtra("CurrentDateToInMillis", mNewDateInMillis);
        }
        intent.putExtra("", mIsBeginDate);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {

        blink(view,this);
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
        intent.putExtra("CurrentDateInMillis", mOldDateFromInMillis);
        intent.putExtra("CurrentDateToInMillis", mOldDateToInMillis);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
