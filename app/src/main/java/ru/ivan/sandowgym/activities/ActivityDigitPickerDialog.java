package ru.ivan.sandowgym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.blink;


public class ActivityDigitPickerDialog extends AppCompatActivity {

    private boolean mCallerIsNew;
    private String mCallerActivity;
    private int mCallerTrainingID;
    private int mCallerWeightChangeCalendarID;

    private String mCallerDigitTitle;
    private int mCallerExerciseIndex;

    private int mCallerDigit;
    private int newDigit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digit_picker);
        getIntentParams();
        updateScreen();
    }

    public void btChange_onClick(View view) {
        Button button = (Button) view;
        String value = String.valueOf(button.getText());
        newDigit += Integer.parseInt(value);
        if (newDigit < 0) {
            newDigit = 0;
        }
        updateScreen();
    }

    public void btNull_onClick(View view) {
        newDigit = 0;
        updateScreen();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        mCallerIsNew = intent.getBooleanExtra("isNew", false);
        mCallerActivity = intent.getStringExtra("currentActivity");
        mCallerTrainingID = intent.getIntExtra("currentTrainingId", 0);
        mCallerWeightChangeCalendarID = intent.getIntExtra("currentWeightChangeCalendarId", 0);
        mCallerExerciseIndex = intent.getIntExtra("currentExerciseIndex", 0);
        mCallerDigitTitle = intent.getStringExtra("currentDigitTitle");
        mCallerDigit = intent.getIntExtra("currentDigit", 0);
        newDigit = mCallerDigit;
    }

    private void updateScreen() {
        int mNewDigitID = getResources().getIdentifier("btNewDigit", "id", getPackageName());
        Button btNewDigit = findViewById(mNewDigitID);
        if (btNewDigit != null) {
            btNewDigit.setText(String.valueOf(newDigit));
        }
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(mCallerActivity);
//            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityDigitPickerDialog.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(View view) {

        blink(view, this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(mCallerActivity);
//            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ActivityDigitPickerDialog.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("currentWeightChangeCalendarId", mCallerWeightChangeCalendarID);
        intent.putExtra("currentTrainingId", mCallerTrainingID);
        intent.putExtra("currentExerciseIndex", mCallerExerciseIndex);
        intent.putExtra("currentDigitTitle", mCallerDigitTitle);
        intent.putExtra("currentDigit", newDigit);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
