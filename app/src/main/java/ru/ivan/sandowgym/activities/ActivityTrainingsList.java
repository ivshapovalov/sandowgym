package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.database.entities.Training;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.dbCurrentUser;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityTrainingsList extends ActivityAbstract {
    private final int maxVerticalButtonsCount = 15;
    private final int maxHorizontalButtonsCount = 2;
    private final int numberOfViews = 20000;

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);
    private SharedPreferences mSettings;
    private int rowsNumber;
    private Map<Integer, List<Training>> pagedTrainings = new HashMap<>();
    private int currentPage = 1;
    private int idIntentTraining;

    private long mCurrentDateInMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btTrainingsDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        Intent intent = getIntent();
        mCurrentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        idIntentTraining = intent.getIntExtra("id", 0);

        if (idIntentTraining == 0) {
            if (mCurrentDateInMillis != 0) {
                List<Training> trainings = DB.getTrainingsByDates(mCurrentDateInMillis, mCurrentDateInMillis);
                if (trainings.size() == 1) {
                    idIntentTraining = trainings.get(0).getId();
                }
            }
        }
        getPreferencesFromFile();
        updateTrainings();
        if (idIntentTraining != 0) {
            TableRow mRow = findViewById(numberOfViews + idIntentTraining);
            if (mRow != null) {

                int mScrID = getResources().getIdentifier("svTableTrainings", "id", getPackageName());
                ScrollView mScrollView = findViewById(mScrID);
                if (mScrollView != null) {

                    mScrollView.requestChildFocus(mRow, mRow);
                }
            }
        }

        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = findViewById(mDayID);
        if (btDay != null) {
            if (mCurrentDateInMillis == 0) {
                btDay.setText("");
            } else {
                btDay.setText(convertMillisToString(mCurrentDateInMillis));
            }
        }

        setTitleOfActivity(this);
    }

    private void updateTrainings() {
        pageTrainings();
        showTrainings();
    }

    private void pageTrainings() {
        List<Training> trainings = new ArrayList<>();
        if (dbCurrentUser == null) {
        } else {
            trainings = DB.getAllTrainingsOfUser(dbCurrentUser.getId());
        }
        pagedTrainings.clear();
        List<Training> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < trainings.size(); i++) {
            if (idIntentTraining != 0) {
                if (trainings.get(i).getId() == idIntentTraining) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(trainings.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedTrainings.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedTrainings.put(pageNumber, pageContent);
        }
        if (pagedTrainings.size() == 0) {
            currentPage = 0;
        }
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    public void bt_TrainingsAdd_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityTraining.class);
        intent.putExtra("isNew", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showTrainings() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage) + "/" + pagedTrainings.size());
        }

        ScrollView sv = findViewById(R.id.svTableTrainings);
        try {
            sv.removeAllViews();
        } catch (Exception e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        int mHeight = displaymetrics.heightPixels / maxVerticalButtonsCount;
        int mWidth = displaymetrics.widthPixels / maxHorizontalButtonsCount;
        int mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);
        TableRow trowButtons = findViewById(R.id.trowButtons);
        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }
        TableLayout layout = new TableLayout(this);

        layout.setStretchAllColumns(true);

        List<Training> page = pagedTrainings.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            TableRow mRow = new TableRow(this);
            Training training = page.get(num);
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setId(numberOfViews + training.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowTraining_onClick((TableRow) v);
                }
            });

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(training.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            long currentDay = page.get(num).getDay();
            if (mCurrentDateInMillis != 0 && mCurrentDateInMillis == currentDay) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(getResources().getColor(R.color.text_color));
            }
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(convertMillisToString(currentDay));
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setBackgroundResource(R.drawable.bt_border);
            if (mCurrentDateInMillis != 0 && mCurrentDateInMillis == currentDay) {
                txt.setTextColor(Color.RED);
            } else {
                txt.setTextColor(getResources().getColor(R.color.text_color));
            }
            mRow.addView(txt);

//            txt = new TextView(this);
//            txt.setText(">>");
//            txt.setGravity(Gravity.CENTER);
//            txt.setHeight(mHeight);
//            txt.setTextSize(mTextSize);
//            txt.setBackgroundResource(R.drawable.bt_border);
//            if (mCurrentDateInMillis != 0 && mCurrentDateInMillis == currentDay) {
//                txt.setTextColor(Color.RED);
//            } else {
//                txt.setTextColor(getResources().getColor(R.color.text_color));
//            }
//            txt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TableRow row = (TableRow) v.getParent();
//                    txtTrainingCopy_onClick(row);
//                }
//            });
//
//            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);
        }
        sv.addView(layout);

    }

    private void rowTraining_onClick(final TableRow view) {

        blink(view, this);
        int id = view.getId() % numberOfViews;
        Intent intent = new Intent(getApplicationContext(), ActivityTraining.class);
        intent.putExtra("currentTrainingId", id);
        intent.putExtra("isNew", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void txtTrainingCopy_onClick(final TableRow view) {

//        //blink(view, this);
//        int id = view.getId() % numberOfViews;
//        Intent intent = new Intent(getApplicationContext(), ActivityTraining.class);
//        intent.putExtra("currentTrainingId", 0);
//        intent.putExtra("isNew", true);
//        intent.putExtra("trainingIdForCopy", id);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

    }

    public void bt_Edit_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void buttonHome_onClick(final View view) {

        blink(view, this);
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

        blink(view, this);

        new AlertDialog.Builder(this)
                .setMessage("Do you wish to delete all training with content?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dbCurrentUser != null) {
                            DB.deleteAllTrainingsOfUser(dbCurrentUser.getId());
                            updateTrainings();
                        }
                    }
                }).setNegativeButton("No", null).show();

    }


    public void btDay_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityTrainingsList.this, ActivityCalendarView.class);
        intent.putExtra("currentDateInMillis", mCurrentDateInMillis);
        intent.putExtra("currentActivity", "ActivityTrainingsList");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btTrainingsFilterDelete_onClick(final View view) {

        blink(view, this);
        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = findViewById(mDayID);
        if (btDay != null) {

            btDay.setText("");
            mCurrentDateInMillis = 0;
            showTrainings();
        }
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedTrainings.size()) {
            currentPage++;
        }
        showTrainings();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showTrainings();
    }
}
