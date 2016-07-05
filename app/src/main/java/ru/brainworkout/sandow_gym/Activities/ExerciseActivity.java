package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.database.TableDoesNotContainElementException;

/**
 * Created by Ivan on 14.05.2016.
 */
public class ExerciseActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    Exercise CurrentExercise;

    DatabaseManager db;

    private boolean mExerciseIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        mExerciseIsNew = intent.getBooleanExtra("IsNew", false);

        if (mExerciseIsNew) {
            CurrentExercise = new Exercise(db.getExerciseMaxNumber() + 1);
        } else {
            int id = intent.getIntExtra("id", 0);
            try {
                CurrentExercise = db.getExercise(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showExerciseOnScreen();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showExerciseOnScreen() {

        //активность
        int mIsActiveID = getResources().getIdentifier("cb_IsActive", "id", getPackageName());
        CheckBox cbIsActive = (CheckBox) findViewById(mIsActiveID);
        if (cbIsActive != null) {
            if (CurrentExercise.getIsActive() != 0) {
                cbIsActive.setChecked(true);
            } else {
                cbIsActive.setChecked(false);
            }
        }

        cbIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (CurrentExercise != null) {
                    if (isChecked) {
                        CurrentExercise.setIsActive(1);
                    } else {
                        CurrentExercise.setIsActive(0);
                    }

                }
            }
        });


        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(CurrentExercise.getID()));
        }
        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(CurrentExercise.getName());

        }
        //Описание
        int mExplanationID = getResources().getIdentifier("et_Explanation", "id", getPackageName());
        EditText etExplanation = (EditText) findViewById(mExplanationID);
        if (etExplanation != null) {
            etExplanation.setText(CurrentExercise.getExplanation());
        }
        //Картинка
        int mPictureID = getResources().getIdentifier("et_Picture", "id", getPackageName());
        EditText etPicture = (EditText) findViewById(mPictureID);
        if (etPicture != null) {
            etPicture.setText(CurrentExercise.getPicture());
        }
        //Количество по умолчанию
        int mVolumeID = getResources().getIdentifier("et_VolumeDefault", "id", getPackageName());
        EditText etVolume = (EditText) findViewById(mVolumeID);
        if (etVolume != null) {
            etVolume.setText(CurrentExercise.getVolumeDefault());
        }

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (CurrentExercise.getPicture()!=null&&!"".equals(CurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(CurrentExercise.getPicture(), "drawable", getPackageName()));
            }
        }
    }

    public void btClose_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ExercisesListActivity.class);
        intent.putExtra("id", CurrentExercise.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            CurrentExercise.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            CurrentExercise.setName(String.valueOf(etName.getText()));
        }
        //Описание
        int mExplanationID = getResources().getIdentifier("et_Explanation", "id", getPackageName());
        EditText etExplanation = (EditText) findViewById(mExplanationID);
        if (etExplanation != null) {
            CurrentExercise.setExplanation(String.valueOf(etExplanation.getText()));
        }
        //Картинка
        int mPictureID = getResources().getIdentifier("et_Picture", "id", getPackageName());
        EditText etPicture = (EditText) findViewById(mPictureID);
        if (etPicture != null) {
            CurrentExercise.setPicture(String.valueOf(etPicture.getText()));
        }
        //Количество по умолчанию
        int mVolumeID = getResources().getIdentifier("et_VolumeDefault", "id", getPackageName());
        EditText etVolume = (EditText) findViewById(mVolumeID);
        if (etVolume != null) {
            CurrentExercise.setVolumeDefault(String.valueOf(etVolume.getText()));
        }
    }

    public void btSave_onClick(View view) {

        //сначала сохраняем
        getPropertiesFromScreen();

        if (mExerciseIsNew) {
            db.addExercise(CurrentExercise);
        } else {
            db.updateExercise(CurrentExercise);
        }

        MyLogger(TAG, "Добавили " + String.valueOf(CurrentExercise.getID()));
        //потом закрываем

        Intent intent = new Intent(getApplicationContext(), ExercisesListActivity.class);
        intent.putExtra("id", CurrentExercise.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static void MyLogger(String TAG, String statement) {
        if (isDebug) {
            Log.e(TAG, statement);
        }
    }

    public void btDelete_onClick(View view) {

        if (!mExerciseIsNew) {


            MyLogger(TAG, "Удалили " + String.valueOf(CurrentExercise.getID()));
            //потом закрываем
            db.deleteExercise(CurrentExercise);

            Intent intent = new Intent(getApplicationContext(), ExercisesListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }
}