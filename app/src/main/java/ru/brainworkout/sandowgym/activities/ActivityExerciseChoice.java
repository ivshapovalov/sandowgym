package ru.brainworkout.sandowgym.activities;

import android.content.Intent;
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

import ru.brainworkout.sandowgym.R;
import ru.brainworkout.sandowgym.common.Common;
import ru.brainworkout.sandowgym.database.entities.Exercise;

import static ru.brainworkout.sandowgym.common.Common.HideEditorButton;
import static ru.brainworkout.sandowgym.common.Common.blink;
import static ru.brainworkout.sandowgym.common.Common.dbCurrentUser;
import static ru.brainworkout.sandowgym.common.Common.setTitleOfActivity;

public class ActivityExerciseChoice extends ActivityAbstract {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_choice);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btExercisesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }
        getPreferencesFromFile();
        Intent intent = getIntent();
        idIntentExercise = intent.getIntExtra("currentExerciseId", 0);

        updateExercises();
        TableRow mRow = (TableRow) findViewById(numberOfViews + idIntentExercise);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableExercises", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }


    private void pageExercises() {
        currentPage = 1;
        List<Exercise> exercises = new ArrayList<Exercise>();
        if (dbCurrentUser == null) {
            //exercises = DB.getAllExercises();
        } else {
            exercises = DB.getAllExercisesOfUser(dbCurrentUser.getId());
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

        mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
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

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void btClose_onClick(final View view) {

        blink(view,this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("isBeginDate", mIsBeginDate);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseId", mCallerExerciseID);
        intent.putExtra("currentWeightChangeCalendarId", mCallerWeightChangeCalendarID);
        intent.putExtra("currentDateInMillis", mOldDateFromInMillis);
        intent.putExtra("currentDateToInMillis", mOldDateToInMillis);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
