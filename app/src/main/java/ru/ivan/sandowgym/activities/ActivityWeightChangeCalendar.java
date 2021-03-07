package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.tasks.Digit;
import ru.ivan.sandowgym.database.entities.WeightChangeCalendar;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityWeightChangeCalendar extends ActivityAbstract {

    private WeightChangeCalendar mCurrentWeightChangeCalendar;
    private boolean mWeightChangeCalendarIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar);

        Intent intent = getIntent();
        mWeightChangeCalendarIsNew = intent.getBooleanExtra("isNew", false);

        long currentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        int id = intent.getIntExtra("currentWeightChangeCalendarId", 0);
        defineCurrentWeightChangeCalendar(id, currentDateInMillis);

        int currentDigit = intent.getIntExtra("currentDigit", 0);
        String currentDigitTitle = intent.getStringExtra("currentDigitTitle");

        if (Digit.WEIGHT.equalValue(currentDigitTitle)) {
            mCurrentWeightChangeCalendar.setWeight(currentDigit);
        }

        showWeightChangeCalendarOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void defineCurrentWeightChangeCalendar(int mCurrentId, long currentDateInMillis) {
        if (mWeightChangeCalendarIsNew) {

            mCurrentWeightChangeCalendar = new WeightChangeCalendar.Builder(database.getWeightChangeCalendarMaxNumber() + 1).build();
            if ((currentDateInMillis == 0)) {
                Calendar cal = Calendar.getInstance();
                cal.clear(Calendar.MILLISECOND);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
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
                mCurrentWeightChangeCalendar = new WeightChangeCalendar.Builder(database.getWeightChangeCalendarMaxNumber() + 1).build();
            } else {

                try {
                    mCurrentWeightChangeCalendar = database.getWeightChangeCalendar(mCurrentId);
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

        blink(view, this);

        fillWeightChangeCalendarFromScreen();
        if (mCurrentWeightChangeCalendar.getWeight() != 0) {
            saveCurrentWeightChangeCalendarToDB();
        }

        Intent intent = new Intent(ActivityWeightChangeCalendar.this, ActivityCalendarView.class);
        intent.putExtra("isNew", mWeightChangeCalendarIsNew);
        intent.putExtra("currentActivity", getClass().getName());
        if (!mWeightChangeCalendarIsNew) {
            intent.putExtra("currentWeightChangeCalendarId", mCurrentWeightChangeCalendar.getId());
        }
        if (mCurrentWeightChangeCalendar.getDay() == 0) {
            intent.putExtra("currentDateInMillis", 0);
        } else {
            intent.putExtra("currentDateInMillis", mCurrentWeightChangeCalendar.getDay());
        }

        startActivity(intent);
    }

    public void btWeight_onClick(View view) {
        blink(view, this);

        fillWeightChangeCalendarFromScreen();
        if (mCurrentWeightChangeCalendar.getDay() != 0) {
            saveCurrentWeightChangeCalendarToDB();
        }
        Intent intent = new Intent(ActivityWeightChangeCalendar.this, ActivityDigitPickerDialog.class);
        intent.putExtra("isNew", mWeightChangeCalendarIsNew);
        if (!mWeightChangeCalendarIsNew) {
            intent.putExtra("currentWeightChangeCalendarId", mCurrentWeightChangeCalendar.getId());
        }
        intent.putExtra("currentDigitTitle", Digit.WEIGHT.name());
        intent.putExtra("currentActivity", getClass().getName());
        intent.putExtra("currentDigit", mCurrentWeightChangeCalendar.getWeight());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void saveCurrentWeightChangeCalendarToDB() {
        mCurrentWeightChangeCalendar.save(database);
        mWeightChangeCalendarIsNew = false;
    }

    private void showWeightChangeCalendarOnScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentWeightChangeCalendar.getId()));
        }

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = findViewById(mDayID);
        if (tvDay != null) {
            if (mCurrentWeightChangeCalendar.getDay() == 0) {
                tvDay.setText("");
            } else {
                tvDay.setText(convertMillisToString(mCurrentWeightChangeCalendar.getDay()));
            }
        }

        int mWeightID = getResources().getIdentifier("btWeight", "id", getPackageName());
        Button btWeight = findViewById(mWeightID);
        if (btWeight != null) {
            btWeight.setText(String.valueOf(mCurrentWeightChangeCalendar.getWeight()));
        }
    }

    private void fillWeightChangeCalendarFromScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            mCurrentWeightChangeCalendar.setID(Integer.parseInt(String.valueOf(tvID.getText())));
        }

        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = findViewById(mDayID);
        if (tvDay != null) {
            mCurrentWeightChangeCalendar.setDayString(String.valueOf(tvDay.getText()));
        }

        int WeightID = getResources().getIdentifier("etWeight", "id", getPackageName());
        EditText etWeight = findViewById(WeightID);
        if (etWeight != null) {
            mCurrentWeightChangeCalendar.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        fillWeightChangeCalendarFromScreen();

        mCurrentWeightChangeCalendar.save(database);

        closeActivity();

    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
        intent.putExtra("currentWeightChangeCalendarId", mCurrentWeightChangeCalendar.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity();

    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current plan??")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCurrentWeightChangeCalendar.delete(database);
                        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendarList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}