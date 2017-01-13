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
    private int mCallerExerciseIndex;
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
        mCallerExerciseIndex = intent.getIntExtra("currentExerciseIndex", 0);

    }

    private void showExercises() {

        List<Exercise> exercises = new ArrayList<Exercise>();
        if (dbCurrentUser == null) {
            exercises = DB.getAllExercises();
        } else {
            exercises = DB.getAllExercisesOfUser(dbCurrentUser.getId());
        }

        final int exercisesListSize = exercises.size();

        TableLayout layout = (TableLayout) findViewById(R.id.layoutTableExercises);
        try {
            layout.removeAllViews();
            layout.setStretchAllColumns(true);

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mWidth = displaymetrics.widthPixels / maxHorizontalButtonCount;
        int maxVerticalButtonCount = (int) Math.ceil((double) exercisesListSize / maxHorizontalButtonCount);
        mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.8 /
                getApplicationContext().getResources().getDisplayMetrics().density);

        layout.setStretchAllColumns(true);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.weight = 1;

        for (int rowNumber = 0; rowNumber < maxVerticalButtonCount; rowNumber++) {
            TableRow row = new TableRow(this);
            //row.setMinimumHeight(mHeight);
            row.setLayoutParams(params);
            row.setGravity(Gravity.CENTER);
            for (int columnNumber = 0; columnNumber < maxHorizontalButtonCount; columnNumber++) {
                int exerciseIndexInList = rowNumber * maxHorizontalButtonCount + columnNumber;
                TextView txt = new TextView(this);
                txt.setId(exerciseIndexInList);
                txt.setHeight(mHeight);
                if (exerciseIndexInList < exercisesListSize) {
                    txt.setText(String.valueOf(exerciseIndexInList + 1));
                }
                txt.setTextSize(mTextSize);
                txt.setGravity(Gravity.CENTER);
                txt.setBackgroundResource(R.drawable.textview_border);
                txt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() >= exercisesListSize) {
                            return;
                        }
                        txtExercise_onClick((TextView)view);
                    }
                });
                row.addView(txt);

            }
            layout.addView(row);

        }
    }

    private void txtExercise_onClick(TextView view) {
        blink(view,this);
        int index = view.getId();
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityExerciseChoice.this, myClass);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseIndex", index);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
