package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.brainworkout.sandow_gym.common.Common;
import ru.brainworkout.sandow_gym.database.entities.Exercise;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class ActivityExercise extends AppCompatActivity {

    private Exercise mCurrentExercise;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Intent intent = getIntent();
        boolean mExerciseIsNew = intent.getBooleanExtra("IsNew", false);

        if (mExerciseIsNew) {
            mCurrentExercise = new Exercise(DB.getExerciseMaxNumber() + 1);
        } else {
            int id = intent.getIntExtra("id", 0);
            try {
                mCurrentExercise = DB.getExercise(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showExerciseOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (Common.mCurrentUser!=null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }
    }

    private void showExerciseOnScreen() {

        int mIsActiveID = getResources().getIdentifier("cb_IsActive", "id", getPackageName());
        CheckBox cbIsActive = (CheckBox) findViewById(mIsActiveID);
        if (cbIsActive != null) {
            if (mCurrentExercise.getIsActive() != 0) {
                cbIsActive.setChecked(true);
            } else {
                cbIsActive.setChecked(false);
            }
        }

        cbIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mCurrentExercise != null) {
                    if (isChecked) {
                        mCurrentExercise.setIsActive(1);
                    } else {
                        mCurrentExercise.setIsActive(0);
                    }

                }
            }
        });


        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentExercise.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentExercise.getName());
        }

        //Описание
        int mExplanationID = getResources().getIdentifier("et_Explanation", "id", getPackageName());
        EditText etExplanation = (EditText) findViewById(mExplanationID);
        if (etExplanation != null) {
            etExplanation.setText(mCurrentExercise.getExplanation());
        }

        //Картинка
        int mPictureID = getResources().getIdentifier("et_Picture", "id", getPackageName());
        EditText etPicture = (EditText) findViewById(mPictureID);
        if (etPicture != null) {
            etPicture.setText(mCurrentExercise.getPicture());
        }

        //Количество по умолчанию
        int mVolumeID = getResources().getIdentifier("et_VolumeDefault", "id", getPackageName());
        EditText etVolume = (EditText) findViewById(mVolumeID);
        if (etVolume != null) {
            etVolume.setText(mCurrentExercise.getVolumeDefault());
        }

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {

                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
            }
        }
    }

    public void btClose_onClick(final View view) {

        Common.blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
        intent.putExtra("id", mCurrentExercise.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void getPropertiesFromScreen() {

        //ID
        int mID = getResources().getIdentifier("tv_ID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentExercise.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }

        //Имя
        int mNameID = getResources().getIdentifier("et_Name", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            mCurrentExercise.setName(String.valueOf(etName.getText()));

        }

        //Описание
        int mExplanationID = getResources().getIdentifier("et_Explanation", "id", getPackageName());
        EditText etExplanation = (EditText) findViewById(mExplanationID);
        if (etExplanation != null) {

            mCurrentExercise.setExplanation(String.valueOf(etExplanation.getText()));

        }

        //Картинка
        int mPictureID = getResources().getIdentifier("et_Picture", "id", getPackageName());
        EditText etPicture = (EditText) findViewById(mPictureID);
        if (etPicture != null) {

            mCurrentExercise.setPicture(String.valueOf(etPicture.getText()));

        }
        //Количество по умолчанию
        int mVolumeID = getResources().getIdentifier("et_VolumeDefault", "id", getPackageName());
        EditText etVolume = (EditText) findViewById(mVolumeID);
        if (etVolume != null) {

            mCurrentExercise.setVolumeDefault(String.valueOf(etVolume.getText()));

        }
    }

    public void btSave_onClick(final View view) {

        Common.blink(view);
        getPropertiesFromScreen();

//        if (mExerciseIsNew) {
//
//            //DB.addExercise(mCurrentExercise);
//            mCurrentExercise.dbAdd(DB);
//
//        } else {
//
//           // DB.updateExercise(mCurrentExercise);
//            mCurrentExercise.dbUpdate(DB);
//        }

        mCurrentExercise.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
        intent.putExtra("id", mCurrentExercise.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {

        Common.blink(view);
        //DB.deleteExercise(mCurrentExercise);
        mCurrentExercise.dbDelete(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}