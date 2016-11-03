package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import ru.brainworkout.sandowgym.R;
import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.entities.WeightChangeCalendar;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class ActivityWeightChangeCalendar extends ActivityAbstract {

    private WeightChangeCalendar mCurrentWeightChangeCalendar;
    private boolean mWeightChangeCalendarIsNew;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar);

        Intent intent = getIntent();
        mWeightChangeCalendarIsNew = intent.getBooleanExtra("IsNew", false);

        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis",0);
        int id = intent.getIntExtra("CurrentWeightChangeCalendarID", 0);
        defineCurrentWeightChangeCalendar(id,currentDateInMillis);

        showWeightChangeCalendarOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void defineCurrentWeightChangeCalendar(int mCurrentId, long currentDateInMillis) {
        if (mWeightChangeCalendarIsNew) {

            mCurrentWeightChangeCalendar = new WeightChangeCalendar.Builder(DB.getWeightChangeCalendarMaxNumber() + 1).build();
            //Calendar calendar = Calendar.getInstance();
            if ((currentDateInMillis==0)) {
                Calendar cal = Calendar.getInstance();
                cal.clear(Calendar.MILLISECOND);
                cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0,0);

                mCurrentWeightChangeCalendar.setDay(cal.getTimeInMillis());

            } else {

                    mCurrentWeightChangeCalendar.setDay(currentDateInMillis);

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
                if ((currentDateInMillis != 0)) {
                    mCurrentWeightChangeCalendar.setDay(currentDateInMillis);
                }
            } catch (Exception e) {
            }
        }
    }

    public void tvDay_onClick(final View view) {

        blink(view,this);

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
        if (mCurrentWeightChangeCalendar.getDay() == 0) {
            intent.putExtra("CurrentDateInMillis", 0);
        } else {
            intent.putExtra("CurrentDateInMillis", mCurrentWeightChangeCalendar.getDay());
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
            if (mCurrentWeightChangeCalendar.getDay() == 0) {
                tvDay.setText("");
            } else {
                tvDay.setText(ConvertMillisToString(mCurrentWeightChangeCalendar.getDay()));
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

        blink(view,this);
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

        blink(view,this);
        fillWeightChangeCalendarFromScreen();

        mCurrentWeightChangeCalendar.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {

        blink(view,this);
        mCurrentWeightChangeCalendar.dbDelete(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}