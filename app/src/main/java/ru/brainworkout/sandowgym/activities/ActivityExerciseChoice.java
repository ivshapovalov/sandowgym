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

    private int mCallerTrainingID;
    private int mCallerExerciseID;
    private String mCallerActivity;

    private final int maxHorizontalButtonCount = 4;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_choice);

        getIntentParams();
        setTitleOfActivity(this);

        showExercises();

    }


    private void getIntentParams() {

        Intent intent = getIntent();

        mCallerActivity = intent.getStringExtra("currentActivity");
        mCallerTrainingID = intent.getIntExtra("currentTrainingId", 0);
        mCallerExerciseID = intent.getIntExtra("currentExerciseId", 0);

    }

    private void showExercises() {

        List<Exercise> exercises = new ArrayList<Exercise>();
        if (dbCurrentUser == null) {
            exercises = DB.getAllExercises();
        } else {
            exercises = DB.getAllExercisesOfUser(dbCurrentUser.getId());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableExercises);
        try {
            sv.removeAllViews();
        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
        mHeight = displaymetrics.heightPixels / (int)Math.ceil(exercises.size()/maxHorizontalButtonCount);
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);

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

    public void onBackPressed() {

        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityExerciseChoice.this, myClass);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseId", mCallerExerciseID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityExerciseChoice.this, myClass);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseId", mCallerExerciseID);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
