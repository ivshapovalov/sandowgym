package ru.brainworkout.sandow_gym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ru.brainworkout.sandow_gym.MainActivity;
import ru.brainworkout.sandow_gym.commons.Common;
import ru.brainworkout.sandow_gym.database.AndroidDatabaseManager;
import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.commons.Training;

public class TrainingsListActivity extends AppCompatActivity {
    private static final int MAX_VERTICAL_BUTTONS_COUNT = 15;
    private static final int MAX_HORIZONTAL_BUTTONS_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 20000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private String mCurrentDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        Intent intent = getIntent();

        mCurrentDate = intent.getStringExtra("CurrentDate");
        if (mCurrentDate == null) {
            mCurrentDate = "";
        }

        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = (Button) findViewById(mDayID);
        if (btDay != null) {
            if (mCurrentDate == null || mCurrentDate.equals("")) {
                btDay.setText("");
            } else {
                btDay.setText(mCurrentDate);
            }
        }

        showTrainings();

    }

    @Override
    public void onResume() {
        super.onResume();

        showTrainings();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        if (id == 0) {
            if (!(mCurrentDate == null || mCurrentDate.equals(""))) {
                List<Training> trainings = DB.getTrainingsByDates(mCurrentDate, mCurrentDate);
                if (trainings.size() == 1) {
                    id = trainings.get(0).getID();
                }
            }

        }
        if (id != 0) {
            TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id);
            if (mRow != null) {

                int mScrID = getResources().getIdentifier("svTableTrainings", "id", getPackageName());
                ScrollView mScrollView = (ScrollView) findViewById(mScrID);
                if (mScrollView != null) {

                    mScrollView.requestChildFocus(mRow, mRow);
                }
            }
        }
    }


    public void bt_TrainingsAdd_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showTrainings() {

        List<Training> trainings = DB.getAllTrainings();

        ScrollView sv = (ScrollView) findViewById(R.id.svTableTrainings);
        try {
            sv.removeAllViews();
        } catch (Exception e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTONS_COUNT;
        int mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTONS_COUNT;
        int mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);

        layout.setStretchAllColumns(true);
        for (int numEx = 0; numEx < trainings.size(); numEx++) {
            TableRow mRow = new TableRow(this);
            String data = "";
            data = trainings.get(numEx).getDayString();
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setId(NUMBER_OF_VIEWS + trainings.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowTraining_onClick((TableRow) v);
                }
            });

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(trainings.get(numEx).getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            if (mCurrentDate != null && mCurrentDate.equals(data)) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(getResources().getColor(R.color.text_color));
            }
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(data);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setBackgroundResource(R.drawable.bt_border);
            if (mCurrentDate != null && mCurrentDate.equals(data)) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(getResources().getColor(R.color.text_color));
            }
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);
        }
        sv.addView(layout);

    }

    private void rowTraining_onClick(final TableRow v) {

        Common.blink(v);
        int id = v.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
        intent.putExtra("CurrentID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        Common.blink(view);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);

    }

    public void buttonHome_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDeleteAllTrainings_onClick(final View view) {

        Common.blink(view);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить все тренировки и их содержимое?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DB.deleteAllTrainings();
                        DB.deleteAllTrainingContent();
                        showTrainings();
                    }
                }).setNegativeButton("Нет", null).show();

    }


    public void btDay_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(TrainingsListActivity.this, CalendarViewActivity.class);
        intent.putExtra("CurrentDate", mCurrentDate);
        intent.putExtra("CurrentActivity", "TrainingsListActivity");

        startActivity(intent);
    }

    public void btTrainingsFilterDelete_onClick(final View view) {

        Common.blink(view);
        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = (Button) findViewById(mDayID);
        if (btDay != null) {

            btDay.setText("");
            mCurrentDate = "";
            showTrainings();

        }

    }
}
