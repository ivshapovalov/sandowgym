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
import android.widget.Toast;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Created by Ivan on 14.05.2016.
 */
public class TrainingActivity extends AppCompatActivity{

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

        CurrentTraining = intent.getParcelableExtra("CurrentTraining");

        if (CurrentTraining==null ) {
            if (mTrainingIsNew) {
                CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
            } else {
                int id = intent.getIntExtra("CurrentID", 0);
                if (id == 0) {
                    CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
                } else {
                    CurrentTraining = db.getTraining(id);
                }
            }
        }

//        String mCurrentDate = intent.getStringExtra("CurrentDate");
//
//        if (!"".equals(mCurrentDate) && mCurrentDate!=null) {
//            try {
//                CurrentTraining.setDay(ConvertStringToDate(mCurrentDate));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        showTrainingOnScreen();
    }

    private void showTrainingOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(CurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {
            if (CurrentTraining.getDay() == null) {
                etDay.setText("");
            } else {
                etDay.setText(ConvertDateToString(CurrentTraining.getDay()));


//            DatePicker mDatePicker = (DatePicker) findViewById(R.id.dPickerTrainingDate);
//            Date d=CurrentTraining.getDay();
//            mDatePicker.updateDate(d.getYear()+1900, d.getMonth(), d.getDate());

        }
        }

    }

    private String ConvertDateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = dateformat.format(date);

        return  sDate;
    }

    private Date ConvertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public void btClose_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            CurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {

            Date d=ConvertStringToDate(String.valueOf(tvDay.getText()));

            if (d!=null) {
                try {
                    CurrentTraining.setDay(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

    public void tvDay_onClick(View view) {

        Intent intent = new Intent(TrainingActivity.this, CalendarViewActivity.class);

        intent.putExtra("CurrentTraining",CurrentTraining);
        intent.putExtra("IsNew",mTrainingIsNew);
//        if (!mTrainingIsNew) {
//        intent.putExtra("CurrentID",CurrentTraining.getID());
//        }
//        if (CurrentTraining.getDay()==null) {
//            intent.putExtra("CurrentDate","");
//        }else {
//        intent.putExtra("CurrentDate",ConvertDateToString(CurrentTraining.getDay()));}

        startActivity(intent);
    }
}