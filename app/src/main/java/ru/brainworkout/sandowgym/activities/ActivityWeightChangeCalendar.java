package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;

import ru.brainworkout.sandowgym.R;
import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.entities.WeightChangeCalendar;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class ActivityWeightChangeCalendar extends AppCompatActivity {


    private WeightChangeCalendar mCurrentWeightChangeCalendar;
    private boolean mWeightChangeCalendarIsNew;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar);

        Intent intent = getIntent();
        mWeightChangeCalendarIsNew = intent.getBooleanExtra("IsNew", false);

        String mCurrentDate = intent.getStringExtra("CurrentDate");
        int id = intent.getIntExtra("CurrentWeightChangeCalendarID", 0);
        defineCurrentWeightChangeCalendar(id,mCurrentDate);

        showWeightChangeCalendarOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void defineCurrentWeightChangeCalendar(int mCurrentId, String mCurrentDate) {
        if (mWeightChangeCalendarIsNew) {

            mCurrentWeightChangeCalendar = new WeightChangeCalendar.Builder(DB.getWeightChangeCalendarMaxNumber() + 1).build();
            //Calendar calendar = Calendar.getInstance();
            if ((mCurrentDate == null)) {
                String cal = (Calendar.getInstance().getTime()).toLocaleString();
                try {
                    mCurrentWeightChangeCalendar.setDay(Calendar.getInstance().getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mCurrentWeightChangeCalendar.setDay(ConvertStringToDate(mCurrentDate, DATE_FORMAT_STRING));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {

            try {

            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }

            if (mCurrentId == 0) {
                mCurrentWeightChangeCalendar = new WeightChangeCalendar.Builder(DB.getWeightChangeCalendarMaxNumber() + 1).build();
            } else {

                try {
                    mCurrentWeightChangeCalendar = DB.getWeightChangeCalendar(mCurrentId);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    //возможно удалили элемент
                    tableDoesNotContainElementException.printStackTrace();
                }
            }
            try {
                if ((mCurrentDate != null)) {
                    mCurrentWeightChangeCalendar.setDayString(mCurrentDate);
                }
            } catch (Exception e) {
            }
        }
    }

    public void tvDay_onClick(final View view) {

        blink(view);

        fillWeightChangeCalendarFromScreen();
        if (mCurrentWeightChangeCalendar.getWeight()!=0) {
            saveCurrentWeightChangeCalendarToDB();
        }

        Intent intent = new Intent(ActivityWeightChangeCalendar.this, ActivityCalendarView.class);

        intent.putExtra("IsNew", mWeightChangeCalendarIsNew);
        intent.putExtra("CurrentActivity", "ActivityWeightChangeCalendar");
        if (!mWeightChangeCalendarIsNew) {
            intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        }
        if (mCurrentWeightChangeCalendar.getDay() == null) {
            intent.putExtra("CurrentDate", "");
        } else {
            intent.putExtra("CurrentDate", ConvertDateToString(mCurrentWeightChangeCalendar.getDay(), DATE_FORMAT_STRING));
        }

        startActivity(intent);

    }

    private void saveCurrentWeightChangeCalendarToDB() {
        mCurrentWeightChangeCalendar.dbSave(DB);
        mWeightChangeCalendarIsNew=false;
    }

    private void showWeightChangeCalendarOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentWeightChangeCalendar.getID()));
        }

        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {
            if (mCurrentWeightChangeCalendar.getDay() == null) {
                tvDay.setText("");
            } else {
                tvDay.setText(ConvertDateToString(mCurrentWeightChangeCalendar.getDay(), DATE_FORMAT_STRING));
            }
        }

        //Вес
        int mWeightID = getResources().getIdentifier("etWeight", "id", getPackageName());
        EditText etWeight = (EditText) findViewById(mWeightID);
        if (etWeight != null) {
            etWeight.setText(String.valueOf(mCurrentWeightChangeCalendar.getWeight()));
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void fillWeightChangeCalendarFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentWeightChangeCalendar.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }

        //Day
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {

            mCurrentWeightChangeCalendar.setDayString(String.valueOf(tvDay.getText()));

        }

        //Weight
        int WeightID = getResources().getIdentifier("etWeight", "id", getPackageName());
        EditText etWeight = (EditText) findViewById(WeightID);
        if (etWeight != null) {

            mCurrentWeightChangeCalendar.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));

        }

    }

    public void btSave_onClick(final View view) {

        blink(view);
        fillWeightChangeCalendarFromScreen();

        mCurrentWeightChangeCalendar.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {

        blink(view);
        mCurrentWeightChangeCalendar.dbDelete(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}