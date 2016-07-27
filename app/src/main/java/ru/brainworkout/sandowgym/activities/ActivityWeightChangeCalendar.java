package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.entities.Exercise;
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
        boolean mWeightChangeCalendarIsNew = intent.getBooleanExtra("IsNew", false);

        if (mWeightChangeCalendarIsNew) {
            mCurrentWeightChangeCalendar = new WeightChangeCalendar.WeightChangeCalendarBuilder(DB.getWeightChangeCalendarMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("CurrentWeightChangeCalendarID", 0);
            try {
                mCurrentWeightChangeCalendar = DB.getWeightChangeCalendar(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showWeightChangeCalendarOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Common.setTitleOfActivity(this);
    }

    public void tvDay_onClick(final View view) {

        Common.blink(view);

        Intent intent = new Intent(ActivityWeightChangeCalendar.this, ActivityCalendarView.class);

        intent.putExtra("IsNew", mWeightChangeCalendarIsNew);
        intent.putExtra("CurrentActivity", "ActivityWeightChangeCalendar");
        if (!mWeightChangeCalendarIsNew) {
            intent.putExtra("CurrentWeightChangeCalendar", mCurrentWeightChangeCalendar.getID());
        }
        if (mCurrentWeightChangeCalendar.getDay() == null) {
            intent.putExtra("CurrentDate", "");
        } else {
            intent.putExtra("CurrentDate", Common.ConvertDateToString(mCurrentWeightChangeCalendar.getDay(), Common.DATE_FORMAT_STRING));
        }

        startActivity(intent);

    }

    private void showWeightChangeCalendarOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentWeightChangeCalendar.getID()));
        }

        //Имя
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {
            etDay.setText(mCurrentWeightChangeCalendar.getDayString());
        }

        //Вес
        int mWeightID = getResources().getIdentifier("et_Weight", "id", getPackageName());
        EditText etWeight = (EditText) findViewById(mWeightID);
        if (etWeight != null) {
            etWeight.setText(String.valueOf(mCurrentWeightChangeCalendar.getWeight()));
        }

    }

    public void btClose_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
        intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void fillExerciseFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentWeightChangeCalendar.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }

        //Day
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {

            mCurrentWeightChangeCalendar.setDayString(String.valueOf(etDay.getText()));

        }

        //Weight
        int WeightID = getResources().getIdentifier("et_Weight", "id", getPackageName());
        EditText etWeight = (EditText) findViewById(WeightID);
        if (etWeight != null) {

            mCurrentWeightChangeCalendar.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));

        }

    }

    public void btSave_onClick(final View view) {

        Common.blink(view);
        fillExerciseFromScreen();

        mCurrentWeightChangeCalendar.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.putExtra("CurrentWeightChangeCalendarID", mCurrentWeightChangeCalendar.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {

        Common.blink(view);
        mCurrentWeightChangeCalendar.dbDelete(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}