package ru.brainworkout.sandow_gym.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.common.Common;


public class ActivityTrainingOptions extends AppCompatActivity {

    private SharedPreferences mSettings;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowVolumeDefaultButton;
    private boolean mShowVolumeLastDayButton;
    private int mPlusMinusButtonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_options);

        getPreferencesFromFile();
        setPreferencesOnScreen();

        Common.setTitleOfActivity(this);
    }


    public void buttonSave_onClick(View view) {

        int mPlusMinusButtonID = getResources().getIdentifier("etPlusMinusButtonValue", "id", getPackageName());
        EditText txt = (EditText) findViewById(mPlusMinusButtonID);
        if (txt != null) {
            try {
                mPlusMinusButtonValue = Integer.valueOf(txt.getText().toString());
            } catch (ClassCastException e) {

            }
        }

        Common.blink(view);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, mPlusMinusButtonValue);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, mShowExplanation);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, mShowPicture);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON, mShowVolumeDefaultButton);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON, mShowVolumeLastDayButton);
        editor.apply();

        this.finish();

    }

    public void buttonCancel_onClick(final View view) {

        Common.blink(view);
        this.finish();

    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE)) {
            mPlusMinusButtonValue = mSettings.getInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, 10);
        } else {
            mPlusMinusButtonValue = 10;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION)) {
            mShowExplanation = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);
        } else {
            mShowExplanation = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE)) {
            mShowPicture = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);
        } else {
            mShowPicture = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON)) {
            mShowVolumeDefaultButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON, false);
        } else {
            mShowVolumeDefaultButton = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON)) {
            mShowVolumeLastDayButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON, false);
        } else {
            mShowVolumeLastDayButton = false;
        }
    }

    private void setPreferencesOnScreen() {

        int mPlusMinusButtonID = getResources().getIdentifier("etPlusMinusButtonValue", "id", getPackageName());
        EditText txt = (EditText) findViewById(mPlusMinusButtonID);
        if (txt != null) {
            txt.setText(String.valueOf(mPlusMinusButtonValue));
        }

        int mPictureID = getResources().getIdentifier("rbShowPicture" + (mShowPicture ? "Yes" : "No"), "id", getPackageName());
        RadioButton but = (RadioButton) findViewById(mPictureID);
        if (but != null) {
            but.setChecked(true);
        }
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgShowPicture);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowPictureYes:
                            mShowPicture = true;
                            break;
                        case R.id.rbShowPictureNo:
                            mShowPicture = false;
                            break;
                        default:
                            mShowPicture = false;
                            break;
                    }
                }
            });
        }

        int mExpID = getResources().getIdentifier("rbShowExplanation" + (mShowExplanation ? "Yes" : "No"), "id", getPackageName());
        but = (RadioButton) findViewById(mExpID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = (RadioGroup) findViewById(R.id.rgShowExplanation);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowExplanationYes:
                            mShowExplanation = true;
                            break;
                        case R.id.rbShowExplanationNo:
                            mShowExplanation = false;
                            break;
                        default:
                            mShowExplanation = false;
                            break;
                    }
                }
            });
        }


        int mVolumeDefaultID = getResources().getIdentifier("rbShowVolumeDefaultButton" + (mShowVolumeDefaultButton ? "Yes" : "No"), "id", getPackageName());
        but = (RadioButton) findViewById(mVolumeDefaultID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = (RadioGroup) findViewById(R.id.rgShowVolumeDefaultButton);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowVolumeDefaultButtonYes:
                            mShowVolumeDefaultButton = true;
                            break;
                        case R.id.rbShowVolumeDefaultButtonNo:
                            mShowVolumeDefaultButton = false;
                            break;
                        default:
                            mShowVolumeDefaultButton = false;
                            break;
                    }
                }
            });
        }

        int mVolumeLastDayID = getResources().getIdentifier("rbShowVolumeLastDayButton" + (mShowVolumeLastDayButton ? "Yes" : "No"), "id", getPackageName());
        but = (RadioButton) findViewById(mVolumeLastDayID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = (RadioGroup) findViewById(R.id.rgShowVolumeLastDayButton);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowVolumeLastDayButtonYes:
                            mShowVolumeLastDayButton = true;
                            break;
                        case R.id.rbShowVolumeLastDayButtonNo:
                            mShowVolumeLastDayButton = false;
                            break;
                        default:
                            mShowVolumeLastDayButton = false;
                            break;
                    }
                }
            });
        }

    }
}
