package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Created by Ivan on 14.05.2016.
 */
public class TrainingActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    Training CurrentTraining;

    DatabaseManager db;

    private boolean mTrainingIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        if (mTrainingIsNew) {
            CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
        } else {
            int id = intent.getIntExtra("id", 0);
            CurrentTraining = db.getTraining(id);
        }

        showTrainingOnScreen();
    }

    private void showTrainingOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(CurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {
            if (CurrentTraining.getDay() == null) {
                etDay.setText("");
            } else {
                etDay.setText(ConvertDateToString(CurrentTraining.getDay()));


//            DatePicker mDatePicker = (DatePicker) findViewById(R.id.dPickerTrainingDate);
//            Date d=CurrentTraining.getDay();
//            mDatePicker.updateDate(d.getYear()+1900, d.getMonth(), d.getDate());

            Date d = CurrentTraining.getDay();
            Calendar calendar = Calendar.getInstance();
            calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());

            CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
            calendarView.setMinDate(calendar.getTimeInMillis() - 2000);

            calendarView.setDate(calendar.getTimeInMillis(), true, false);
        }
        }

    }

    private String ConvertDateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd");
        String sDate = dateformat.format(date);

        return  sDate;
    }

    public void btClose_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            CurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date d = null;
            try {
                d = dateFormat.parse(String.valueOf(etDay.getText()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                CurrentTraining.setDay(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    public void btSave_onClick(View view) {

        //сначала сохраняем
        getPropertiesFromScreen();

        if (mTrainingIsNew) {
            db.addTraining(CurrentTraining);
        } else {
            db.updateTraining(CurrentTraining);
        }

        MyLogger(TAG, "Добавили " + String.valueOf(CurrentTraining.getID()));
        //потом закрываем

        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }

    public void btDelete_onClick(View view) {

        if (!mTrainingIsNew) {


            MyLogger(TAG, "Удалили " + String.valueOf(CurrentTraining.getID()));
            //потом закрываем
            db.deleteTraining(CurrentTraining);

            Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }
}