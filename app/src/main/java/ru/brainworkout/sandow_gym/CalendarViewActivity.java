package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ivan on 17.05.2016.
 */
public class CalendarViewActivity extends AppCompatActivity {

    //private String mCurrentDate;
    private boolean mTrainingIsNew;
   // private int mCurrentID;
    private Training mCurrentTraining;

    private boolean isChecked;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();

        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        mCurrentTraining = intent.getParcelableExtra("CurrentTraining");

        if (mCurrentTraining!=null &mCurrentTraining.getDay()!=null) {

            Date d = mCurrentTraining.getDay();

            if (d != null) {
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
            }
        }
        //mCurrentID = intent.getIntExtra("CurrentID", 0);
        //mCurrentDate = intent.getStringExtra("CurrentDate");

//        if (!"".equals(mCurrentDate) && mCurrentDate != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date d = null;
//            try {
//                d = dateFormat.parse(String.valueOf(mCurrentDate));
//            } catch (Exception e) {
//                d = null;
//            }
//
//            if (d != null) {
//                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
//            }
//        }
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis(), true, false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                mCurrentTraining.setDayString(new StringBuilder().append(mYear)
                        .append("-").append(mMonth + 1).append("-").append(mDay)
                        .append("").toString());
            }
        });


    }

    public void btSave_onClick(View view) {
        Intent intent = new Intent(CalendarViewActivity.this, TrainingActivity.class);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentTraining", mCurrentTraining);
//        intent.putExtra("CurrentID", mCurrentID);
//        intent.putExtra("CurrentDate", mCurrentDate);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(View view) {
        Intent intent = new Intent(CalendarViewActivity.this, TrainingActivity.class);
        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentTraining", mCurrentTraining);
//        intent.putExtra("CurrentID", mCurrentID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
