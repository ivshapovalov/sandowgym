package ru.brainworkout.sandowgym.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandowgym.R;
import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.entities.WeightChangeCalendar;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;

public class ActivityWeightChangeCalendarList extends AppCompatActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 3;
    private final int NUMBER_OF_VIEWS = 10000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar_list);

        showWeightChangeCalendarList();

        setTitleOfActivity(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        showWeightChangeCalendarList();

        Intent intent = getIntent();
        int id = intent.getIntExtra("CurrentWeightChangeCalendarID", 0);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableWeightChangeCalendarList", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void btWeightChangeCalendarListAdd_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }


    private void showWeightChangeCalendarList() {

        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<WeightChangeCalendar>();

        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            weightChangeCalendarList = DB.getAllWeightChangeCalendarOfUser(dbCurrentUser.getID());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableWeightChangeCalendarList);
        try

        {

            sv.removeAllViews();

        } catch (
                NullPointerException e
                )

        {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null)

        {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        for (
                int numEx = 0;
                numEx < weightChangeCalendarList.size(); numEx++)

        {
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + weightChangeCalendarList.get(numEx).getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowWeightChangeCalendar_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendarList.get(numEx).getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendarList.get(numEx).getDayString()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendarList.get(numEx).getWeight()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);

            layout.addView(mRow);

        }

        sv.addView(layout);

    }

    private void rowWeightChangeCalendar_onClick(final TableRow v) {

        blink(v);

        int id = v.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("CurrentWeightChangeCalendarID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        blink(view);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);

    }


    public void buttonHome_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDeleteWeightChangeCalendarList_onClick(final View view) {

        blink(view);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить все изменения весов пользователия?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (dbCurrentUser != null) {
                            DB.deleteAllWeightChangeCalendarOfUser(dbCurrentUser.getID());
                            showWeightChangeCalendarList();
                        }

                    }
                }).setNegativeButton("Нет", null).show();
    }

}
