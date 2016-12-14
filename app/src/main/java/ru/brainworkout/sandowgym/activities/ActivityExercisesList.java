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

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.manager.AndroidDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.R;

public class ActivityExercisesList extends ActivityAbstract {

    private final int MAX_VERTICAL_BUTTON_COUNT = 15;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 10000;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

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

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btExercisesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }
        getPreferencesFromFile();
        Intent intent = getIntent();
        idIntentExercise = intent.getIntExtra("currentExerciseId", 0);

        updateExercises();
        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + idIntentExercise);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
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
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            rowsNumber = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
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
        List<Exercise> exercises = Common.createDefaultExercises(DB);
        for (Exercise ex : exercises) {
            ex.dbSave(DB);
        }
        updateExercises();

    }

    private void pageExercises() {
        currentPage = 1;
        List<Exercise> exercises = new ArrayList<Exercise>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            exercises = DB.getAllExercisesOfUser(dbCurrentUser.getId());
        }
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
        if (pageContent.size()!=0) {
            pagedExercices.put(pageNumber, pageContent);
        }
        if (pagedExercices.size()==0) {
            currentPage=0;
        }
    }

    private void showExercises() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedExercices.size());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
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

        List<Exercise> page = pagedExercices.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            Exercise exercise=page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + exercise.getId());
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

        int id = view.getId() % NUMBER_OF_VIEWS;

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
                            DB.deleteAllExercisesOfUser(dbCurrentUser.getId());
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
