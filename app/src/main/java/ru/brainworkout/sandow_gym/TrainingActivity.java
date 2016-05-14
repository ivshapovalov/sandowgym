package ru.brainworkout.sandow_gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Ivan on 14.05.2016.
 */
public class TrainingActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    Training CurrentTraining;

    DatabaseManager db;

    private boolean mTrainingIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        if (mTrainingIsNew) {
            CurrentTraining = new Training(db.getTrainingMaxNumber() + 1);
        } else {
            int id = intent.getIntExtra("id", 0);
            CurrentTraining = db.getTraining(id);
        }

        showTrainingOnScreen();
    }

    private void showTrainingOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(CurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {
            etDay.setText(CurrentTraining.getDay());

        }

    }

    public void btClose_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            CurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("et_Day", "id", getPackageName());
        EditText etDay = (EditText) findViewById(mDayID);
        if (etDay != null) {
            CurrentTraining.setDay(String.valueOf(etDay.getText()));
        }

    }

    public void btSave_onClick(View view) {

        //сначала сохраняем
        getPropertiesFromScreen();

        if (mTrainingIsNew) {
            db.addTraining(CurrentTraining);
        } else {
            db.updateTraining(CurrentTraining);
        }

        MyLogger(TAG, "Добавили " + String.valueOf(CurrentTraining.getID()));
        //потом закрываем

        Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
        intent.putExtra("id", CurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }

    public void btDelete_onClick(View view) {

        if (!mTrainingIsNew) {


            MyLogger(TAG, "Удалили " + String.valueOf(CurrentTraining.getID()));
            //потом закрываем
            db.deleteTraining(CurrentTraining);

            Intent intent = new Intent(getApplicationContext(), TrainingsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }
}