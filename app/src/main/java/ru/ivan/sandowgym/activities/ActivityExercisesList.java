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
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.manager.AndroidDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.hideEditorButton;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public class ActivityExercisesList extends ActivityAbstract {

    private final int maxVerticalButtonCount = 15;
    private final int maxHorizontalButtonCount = 2;
    private final int numberOfViews = 10000;

    private SharedPreferences mSettings;
    private int rowsNumber;
    private Map<Integer, List<Exercise>> pagedExercices = new HashMap<>();
    private int currentPage = 1;
    private int idIntentExercise;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);

        if (!Constants.IS_DEBUG) {
            int mEditorID = getResources().getIdentifier("btExercisesDBEditor", "id", getPackageName());
            Button btEditor = findViewById(mEditorID);
            hideEditorButton(btEditor);
        }
        getPreferencesFromFile();
        Intent intent = getIntent();
        idIntentExercise = intent.getIntExtra("currentExerciseId", 0);

        updateExercises();
        TableRow mRow = findViewById(numberOfViews + idIntentExercise);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }

    private void updateExercises() {
        pageExercises();
        showExercises();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            rowsNumber = 17;
        }
    }

    public void btExercisesAdd_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityExercise.class);
        intent.putExtra("isNew", true);
        startActivity(intent);

    }

    public void btExercisesFillDefault_onClick(final View view) {

        blink(view, this);
        List<Exercise> exercises = Common.createDefaultExercises(database);
        for (Exercise ex : exercises) {
            ex.save(database);
        }
        updateExercises();

    }

    private void pageExercises() {
        currentPage = 1;
        List<Exercise> exercises = new ArrayList<>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            exercises = database.getAllExercisesOfUser(dbCurrentUser.getId());
        }
        pagedExercices.clear();
        List<Exercise> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < exercises.size(); i++) {
            if (idIntentExercise != 0) {
                if (exercises.get(i).getId() == idIntentExercise) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(exercises.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedExercices.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedExercices.put(pageNumber, pageContent);
        }
        if (pagedExercices.size() == 0) {
            currentPage = 0;
        }
    }

    private void showExercises() {

        Button pageNumber = findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(currentPage + "/" + pagedExercices.size());
        }

        ScrollView sv = findViewById(R.id.svTableExercises);
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

        List<Exercise> page = pagedExercices.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            Exercise exercise = page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(numberOfViews + exercise.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowExercise_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(exercise.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(String.valueOf(exercise.getName()));
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

    private void rowExercise_onClick(final TableRow view) {

        blink(view, this);

        int id = view.getId() % numberOfViews;

        Intent intent = new Intent(getApplicationContext(), ActivityExercise.class);
        intent.putExtra("currentExerciseId", id);
        intent.putExtra("isNew", false);
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

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDeleteAllExercises_onClick(final View view) {

        blink(view, this);

        new AlertDialog.Builder(this)
                .setMessage("Do you wish to delete all user exrecises?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dbCurrentUser != null) {
                            database.deleteAllExercisesOfUser(dbCurrentUser.getId());
                            updateExercises();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedExercices.size()) {
            currentPage++;
        }
        showExercises();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showExercises();
    }
}
