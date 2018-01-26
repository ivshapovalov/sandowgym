package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityExercise extends ActivityAbstract {

    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);
    private Exercise mCurrentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Intent intent = getIntent();
        boolean mExerciseIsNew = intent.getBooleanExtra("isNew", false);

        if (mExerciseIsNew) {
            mCurrentExercise = new Exercise.Builder(DB).build();
        } else {
            int id = intent.getIntExtra("currentExerciseId", 0);
            try {
                mCurrentExercise = Exercise.getExerciseFromDB(DB, id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
        showExerciseOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setTitleOfActivity(this);
    }

    private void showExerciseOnScreen() {

        int mIsActiveID = getResources().getIdentifier("cbIsActive", "id", getPackageName());
        CheckBox cbIsActive = findViewById(mIsActiveID);
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

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(mCurrentExercise.getId()));
        }

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentExercise.getName());
        }

        int mExplanationID = getResources().getIdentifier("etExplanation", "id", getPackageName());
        EditText etExplanation = findViewById(mExplanationID);
        if (etExplanation != null) {
            etExplanation.setText(mCurrentExercise.getExplanation());
        }

        int mPictureID = getResources().getIdentifier("etPicture", "id", getPackageName());
        EditText etPicture = findViewById(mPictureID);
        if (etPicture != null) {
            etPicture.setText(mCurrentExercise.getPicture());
        }

        int mVolumeID = getResources().getIdentifier("etVolumeDefault", "id", getPackageName());
        EditText etVolume = findViewById(mVolumeID);
        if (etVolume != null) {
            etVolume.setText(mCurrentExercise.getVolumeDefault());
        }

        ImageView ivPicture = findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {

                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
            }
        }
    }

    private void fillExerciseFromScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = findViewById(mID);
        if (tvID != null) {
            mCurrentExercise.setID(Integer.parseInt(String.valueOf(tvID.getText())));
        }

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = findViewById(mNameID);
        if (etName != null) {

            mCurrentExercise.setName(String.valueOf(etName.getText()));
        }

        int mExplanationID = getResources().getIdentifier("etExplanation", "id", getPackageName());
        EditText etExplanation = findViewById(mExplanationID);
        if (etExplanation != null) {
            mCurrentExercise.setExplanation(String.valueOf(etExplanation.getText()));
        }

        int mPictureID = getResources().getIdentifier("etPicture", "id", getPackageName());
        EditText etPicture = findViewById(mPictureID);
        if (etPicture != null) {

            mCurrentExercise.setPicture(String.valueOf(etPicture.getText()));
        }

        int mVolumeID = getResources().getIdentifier("etVolumeDefault", "id", getPackageName());
        EditText etVolume = findViewById(mVolumeID);
        if (etVolume != null) {
            mCurrentExercise.setVolumeDefault(String.valueOf(etVolume.getText()));
        }
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        fillExerciseFromScreen();
        mCurrentExercise.dbSave(DB);
        closeActivity();
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity();
    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
        intent.putExtra("currentExerciseId", mCurrentExercise.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current exercise?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCurrentExercise.dbDelete(DB);
                        Intent intent = new Intent(getApplicationContext(), ActivityExercisesList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}