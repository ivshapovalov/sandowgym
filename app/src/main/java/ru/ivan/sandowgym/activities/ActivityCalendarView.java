package ru.ivan.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityCalendarView extends AppCompatActivity {

    private boolean mIsBeginDate;

    private long mOldDateFromInMillis;
    private long mNewDateInMillis;
    private long mOldDateToInMillis;

    private boolean mCallerIsNew;
    private int mCallerTrainingID;
    private int mCallerExerciseIndex;
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

        mIsBeginDate = intent.getBooleanExtra("isBeginDate", true);
        mCallerIsNew = intent.getBooleanExtra("isNew", false);
        mCallerActivity = intent.getStringExtra("currentActivity");
        mCallerTrainingID = intent.getIntExtra("currentTrainingId", 0);
        mCallerExerciseIndex = intent.getIntExtra("currentExerciseIndex",0);
        mCallerWeightChangeCalendarID=intent.getIntExtra("currentWeightChangeCalendarId",0);

        try {
            mOldDateFromInMillis = intent.getLongExtra("currentDateInMillis",0);
        } catch (Exception e) {
            mOldDateFromInMillis = 0;
        }
        try {
            mOldDateToInMillis = intent.getLongExtra("currentDateToInMillis",0);
        } catch (Exception e) {
            mOldDateToInMillis = 0;
        }

    }
    private void SetParametersOnScreen() {

        Calendar calendar = Calendar.getInstance();

        if (mIsBeginDate || mCallerActivity.equals("ActivityTraining")) {
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
        CalendarView calendarView = findViewById(R.id.calendarView);
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
            myClass = Class.forName(mCallerActivity);
            //myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("isBeginDate", mIsBeginDate);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);
        intent.putExtra("currentWeightChangeCalendarId", mCallerWeightChangeCalendarID);
        if (mIsBeginDate) {
            intent.putExtra("currentDateInMillis", mNewDateInMillis);
            intent.putExtra("currentDateToInMillis", mOldDateToInMillis);
        } else {
            intent.putExtra("currentDateInMillis", mOldDateFromInMillis);
            intent.putExtra("currentDateToInMillis", mNewDateInMillis);
        }
        intent.putExtra("", mIsBeginDate);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {

        blink(view,this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(mCallerActivity);
//            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("isBeginDate", mIsBeginDate);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);
        intent.putExtra("currentWeightChangeCalendarId", mCallerWeightChangeCalendarID);
        intent.putExtra("currentDateInMillis", mOldDateFromInMillis);
        intent.putExtra("currentDateToInMillis", mOldDateToInMillis);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
