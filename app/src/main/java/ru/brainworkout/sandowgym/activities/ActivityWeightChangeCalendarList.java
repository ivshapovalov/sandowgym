package ru.brainworkout.sandowgym.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.brainworkout.sandowgym.R;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.database.entities.WeightChangeCalendar;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

public class ActivityWeightChangeCalendarList extends ActivityAbstract {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 3;
    private final int NUMBER_OF_VIEWS = 10000;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    private SharedPreferences mSettings;
    private int rows_number = 17;
    Map<Integer, List<WeightChangeCalendar>> pagingWeightChangeCalendar = new HashMap<>();
    private int currentPage = 1;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("bWeightChangeCalendarListDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        getPreferencesFromFile();
        pageWeightChangeCalendar();
        showWeightChangeCalendarList();

        setTitleOfActivity(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        getPreferencesFromFile();
        pageWeightChangeCalendar();
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

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rows_number = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rows_number = 17;
        }
    }

    public void btWeightChangeCalendarListAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void pageWeightChangeCalendar() {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<WeightChangeCalendar>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            weightChangeCalendarList = DB.getAllWeightChangeCalendarOfUser(dbCurrentUser.getID());
        }
        List<WeightChangeCalendar> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < weightChangeCalendarList.size(); i++) {
            pageContent.add(weightChangeCalendarList.get(i));
            if (pageContent.size() == rows_number) {
                pagingWeightChangeCalendar.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagingWeightChangeCalendar.put(pageNumber, pageContent);
        }
    }

    private void showWeightChangeCalendarList() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage));
        }
        ScrollView sv = (ScrollView) findViewById(R.id.svTableWeightChangeCalendarList);
        try {
            sv.removeAllViews();

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);
        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        List<WeightChangeCalendar> page = pagingWeightChangeCalendar.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {

            WeightChangeCalendar weightChangeCalendar=page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + weightChangeCalendar.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowWeightChangeCalendar_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendar.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendar.getDayString()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(weightChangeCalendar.getWeight()));
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

    private void rowWeightChangeCalendar_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % NUMBER_OF_VIEWS;

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("CurrentWeightChangeCalendarID", id);
        intent.putExtra("IsNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        blink(view, this);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);

    }


    public void buttonHome_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDeleteWeightChangeCalendarList_onClick(final View view) {

        blink(view, this);

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

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagingWeightChangeCalendar.size()) {
            currentPage++;
        }
        showWeightChangeCalendarList();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage != 1) {
            currentPage--;
        }
        showWeightChangeCalendarList();
    }

}
