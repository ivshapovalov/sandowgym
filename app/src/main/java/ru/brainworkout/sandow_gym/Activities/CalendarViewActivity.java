package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.R;

public class CalendarViewActivity extends AppCompatActivity {

    private boolean mTrainingIsNew;
    private boolean mIsBeginDate;

    private String mOldDateFrom;
    private String mNewDate;
    private String mOldDateTo;

    private int mCallerTrainingID;
    private int mCallerExerciseID;
    private String mCallerActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();

        mIsBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);
        mCallerActivity = intent.getStringExtra("CurrentActivity");
        mCallerTrainingID = intent.getIntExtra("CurrentTrainingID", 0);
        mCallerExerciseID = intent.getIntExtra("CurrentExerciseID",0);
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

        if (mIsBeginDate || mCallerActivity == "TrainingActivity") {
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

                mNewDate = Common.ConvertDateToString(Common.ConvertStringToDate(new StringBuilder().append(year)
                        .append("-").append(month + 1).append("-").append(dayOfMonth)
                        .append("").toString()));
            }
        });

        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }
    }

    public void btSave_onClick(final View view) {

        Common.blink(view);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(CalendarViewActivity.this, myClass);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentID", mCallerTrainingID);
        intent.putExtra("CurrentExerciseID", mCallerExerciseID);
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
        Intent intent = new Intent(CalendarViewActivity.this, myClass);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentID", mCallerTrainingID);
        intent.putExtra("CurrentExerciseID", mCallerExerciseID);
        intent.putExtra("CurrentDate", mOldDateFrom);
        intent.putExtra("CurrentDateTo", mOldDateTo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
