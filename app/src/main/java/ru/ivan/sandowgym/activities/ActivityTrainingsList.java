package ru.ivan.sandowgym.activities;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.database.entities.Training;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public class ActivityTrainingsList extends ActivityAbstract {
    private final int maxVerticalButtonsCount = 15;
    private final int maxHorizontalButtonsCount = 2;
    private final int numberOfViews = 20000;

    private Map<Integer, List<Training>> pagedTrainings = new HashMap<>();
    private int currentPage = 1;
    private int mCurrentTrainingId;
    private boolean mCallerForSelect;
    private String mCallerActivity;
    private int mCallerExerciseIndex;
    private long mCallerDateInMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);

        if (!Constants.IS_DEBUG) {
            int mEditorID = getResources().getIdentifier("btTrainingsDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        Intent intent = getIntent();
        mCallerForSelect = intent.getBooleanExtra("forSelect", false);
        mCallerActivity = intent.getStringExtra("currentActivity");
        mCallerExerciseIndex = intent.getIntExtra("currentExerciseIndex", 0);
        mCallerDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        mCurrentTrainingId = intent.getIntExtra("currentTrainingId", 0);

        if (mCurrentTrainingId == 0) {
            if (mCallerDateInMillis != 0) {
                List<Training> trainings = database.getTrainingsByDates(mCallerDateInMillis, mCallerDateInMillis);
                if (trainings.size() == 1) {
                    mCurrentTrainingId = trainings.get(0).getId();
                }
            }
        }
        updateTrainings();
        if (mCurrentTrainingId != 0) {
            TableRow mRow = findViewById(numberOfViews + mCurrentTrainingId);
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
            if (mCallerDateInMillis == 0) {
                btDay.setText("");
            } else {
                btDay.setText(convertMillisToString(mCallerDateInMillis));
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
            trainings = database.getAllTrainingsOfUser(dbCurrentUser.getId());
        }
        pagedTrainings.clear();
        List<Training> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < trainings.size(); i++) {
            if (mCurrentTrainingId != 0) {
                if (trainings.get(i).getId() == mCurrentTrainingId) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(trainings.get(i));
            if (pageContent.size() == Constants.mOptionRowsOnPageInLists) {
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
            pageNumber.setText(currentPage + "/" + pagedTrainings.size());
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
            if (mCallerDateInMillis != 0 && mCallerDateInMillis == currentDay) {
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
            if (mCallerDateInMillis != 0 && mCallerDateInMillis == currentDay) {
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
        Class<?> myClass = null;
        Intent intent;
        if (mCallerForSelect) {
            try {
                myClass = Class.forName(mCallerActivity);
                //myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent = new Intent(ActivityTrainingsList.this, myClass);
            intent.putExtra("currentTrainingId", mCurrentTrainingId);
            intent.putExtra("selectedTrainingId", id);
            intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);
        } else {
            intent = new Intent(ActivityTrainingsList.this, ActivityTraining.class);
            intent.putExtra("currentTrainingId", id);
        }
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
                            database.deleteAllTrainingsOfUser(dbCurrentUser.getId());
                            updateTrainings();
                        }
                    }
                }).setNegativeButton("No", null).show();

    }


    public void btDay_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityTrainingsList.this, ActivityCalendarView.class);
        intent.putExtra("currentDateInMillis", mCallerDateInMillis);
        intent.putExtra("currentActivity", getClass().getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btTrainingsFilterDelete_onClick(final View view) {

        blink(view, this);
        int mDayID = getResources().getIdentifier("btDay", "id", getPackageName());
        Button btDay = findViewById(mDayID);
        if (btDay != null) {

            btDay.setText("");
            mCallerDateInMillis = 0;
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
