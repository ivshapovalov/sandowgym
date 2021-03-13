package ru.ivan.sandowgym.activities;


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

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.database.entities.WeightChangeCalendar;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public class ActivityWeightChangeCalendarList extends ActivityAbstract {

    private final int maxVerticalButtonCount = 17;
    private final int maxHorizontalButtonCount = 3;
    private final int numberOfViews = 10000;

    private SharedPreferences mSettings;
    private int rowsNumber = 17;
    private Map<Integer, List<WeightChangeCalendar>> pagedWeightChangeCalendar = new HashMap<>();
    private int currentPage = 1;
    private int idIntentWeightChangeCalendar;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_change_calendar_list);

        if (!Constants.IS_DEBUG) {
            int mEditorID = getResources().getIdentifier("bWeightChangeCalendarListDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        getPreferencesFromFile();

        Intent intent = getIntent();
        idIntentWeightChangeCalendar = intent.getIntExtra("currentWeightChangeCalendarId", 0);
        updateweightChangeCalendarList();

        TableRow mRow = findViewById(numberOfViews + idIntentWeightChangeCalendar);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableWeightChangeCalendarList", "id", getPackageName());
            ScrollView mScrollView = findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }

    private void updateweightChangeCalendarList() {
        pageWeightChangeCalendarList();
        showWeightChangeCalendarList();
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    public void btWeightChangeCalendarListAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("isNew", true);
        startActivity(intent);

    }

    private void pageWeightChangeCalendarList() {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            weightChangeCalendarList = database.getAllWeightChangeCalendarOfUser(dbCurrentUser.getId());
        }
        pagedWeightChangeCalendar.clear();
        List<WeightChangeCalendar> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < weightChangeCalendarList.size(); i++) {
            if (idIntentWeightChangeCalendar != 0) {
                if (weightChangeCalendarList.get(i).getId() == idIntentWeightChangeCalendar) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(weightChangeCalendarList.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedWeightChangeCalendar.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedWeightChangeCalendar.put(pageNumber, pageContent);
        }
        if (pagedWeightChangeCalendar.size()==0) {
            currentPage=0;
        }
    }

    private void showWeightChangeCalendarList() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedWeightChangeCalendar.size());
        }
        ScrollView sv = findViewById(R.id.svTableWeightChangeCalendarList);
        try {
            sv.removeAllViews();

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = findViewById(R.id.trowButtons);
        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

        List<WeightChangeCalendar> page = pagedWeightChangeCalendar.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {

            WeightChangeCalendar weightChangeCalendar=page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(numberOfViews + weightChangeCalendar.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowWeightChangeCalendar_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
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

        int id = view.getId() % numberOfViews;

        Intent intent = new Intent(getApplicationContext(), ActivityWeightChangeCalendar.class);
        intent.putExtra("currentWeightChangeCalendarId", id);
        intent.putExtra("isNew", false);
        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(intent);
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
                .setMessage("Do you wish to delete all weight changes of user?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (dbCurrentUser != null) {
                            database.deleteAllWeightChangeCalendarOfUser(dbCurrentUser.getId());
                            updateweightChangeCalendarList();
                        }

                    }
                }).setNegativeButton("No", null).show();
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedWeightChangeCalendar.size()) {
            currentPage++;
        }
        showWeightChangeCalendarList();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showWeightChangeCalendarList();
    }
}
