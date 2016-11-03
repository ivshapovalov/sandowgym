package ru.brainworkout.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.database.entities.Training;

public class ActivityTrainingsList extends ActivityAbstract {
    private static final int MAX_VERTICAL_BUTTONS_COUNT = 15;
    private static final int MAX_HORIZONTAL_BUTTONS_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 20000;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    private long mCurrentDateInMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("bTrainingsDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        Intent intent = getIntent();

        mCurrentDateInMillis = intent.getLongExtra("CurrentDateInMillis",0);

        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = (Button) findViewById(mDayID);
        if (btDay != null) {
            if (mCurrentDateInMillis == 0) {
                btDay.setText("");
            } else {
                btDay.setText(ConvertMillisToString(mCurrentDateInMillis));
            }
        }

        showTrainings();

        setTitleOfActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        showTrainings();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        if (id == 0) {
            if (!(mCurrentDateInMillis == 0)) {
                List<Training> trainings = DB.getTrainingsByDates(mCurrentDateInMillis, mCurrentDateInMillis);
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

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityTraining.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showTrainings() {

        List<Training> trainings=new ArrayList<Training>();
        if (dbCurrentUser == null) {
            //trainings = DB.getAllTrainings();
        } else {
            trainings = DB.getAllTrainingsOfUser(dbCurrentUser.getID());
        }
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
            long data = 0;
            data = trainings.get(numEx).getDay();
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
            if (mCurrentDateInMillis != 0 && mCurrentDateInMillis==data) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(getResources().getColor(R.color.text_color));
            }
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(ConvertMillisToString(data));
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setBackgroundResource(R.drawable.bt_border);
            if (mCurrentDateInMillis != 0 && mCurrentDateInMillis==data) {
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

    private void rowTraining_onClick(final TableRow view) {

        blink(view,this);
        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityTraining.class);
        intent.putExtra("CurrentTrainingID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        blink(view,this);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);

    }

    public void buttonHome_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDeleteAllTrainings_onClick(final View view) {

        blink(view,this);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить все тренировки и их содержимое?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            if (dbCurrentUser !=null) {
                                DB.deleteAllTrainingsOfUser(dbCurrentUser.getID());
                                DB.deleteAllTrainingContentOfUser(dbCurrentUser.getID());

                                showTrainings();
                            }


                    }
                }).setNegativeButton("Нет", null).show();

    }


    public void btDay_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(ActivityTrainingsList.this, ActivityCalendarView.class);
        intent.putExtra("CurrentDateInMillis", mCurrentDateInMillis);
        intent.putExtra("CurrentActivity", "ActivityTrainingsList");

        startActivity(intent);
    }

    public void btTrainingsFilterDelete_onClick(final View view) {

        blink(view,this);
        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = (Button) findViewById(mDayID);
        if (btDay != null) {

            btDay.setText("");
            mCurrentDateInMillis = 0;
            showTrainings();
        }
    }
}
