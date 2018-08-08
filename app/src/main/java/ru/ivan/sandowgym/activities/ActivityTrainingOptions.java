package ru.ivan.sandowgym.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;


public class ActivityTrainingOptions extends ActivityAbstract {

    private SharedPreferences mSettings;
    private boolean mUseCalendarForWeight;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowAmountDefaultButton;
    private boolean mShowAmountLastDayButton;
    private int mPlusMinusButtonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_options);

        getPreferencesFromFile();
        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }

    public void buttonSave_onClick(View view) {

        int mPlusMinusButtonID = getResources().getIdentifier("etPlusMinusButtonValue", "id", getPackageName());
        EditText txt = findViewById(mPlusMinusButtonID);
        if (txt != null) {
            try {
                mPlusMinusButtonValue = Integer.valueOf(txt.getText().toString());
            } catch (ClassCastException e) {

            }
        }
        blink(view, this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, mPlusMinusButtonValue);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT, mUseCalendarForWeight);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, mShowExplanation);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, mShowPicture);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON, mShowAmountDefaultButton);
        editor.putBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON, mShowAmountLastDayButton);
        editor.apply();

        this.finish();

    }

    public void buttonCancel_onClick(final View view) {

        blink(view, this);
        this.finish();

    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE)) {
            mPlusMinusButtonValue = mSettings.getInt(ActivityMain.APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE, 10);
        } else {
            mPlusMinusButtonValue = 10;
        }

        mUseCalendarForWeight = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT, false);

        mShowExplanation = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);

        mShowPicture = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);

        mShowAmountDefaultButton = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON, false);

        mShowAmountLastDayButton = mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON) && mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON, false);
    }

    private void setPreferencesOnScreen() {

        int mPlusMinusButtonID = getResources().getIdentifier("etPlusMinusButtonValue", "id", getPackageName());
        EditText txt = findViewById(mPlusMinusButtonID);
        if (txt != null) {
            txt.setText(String.valueOf(mPlusMinusButtonValue));
        }

        int mUseCalendarID = getResources().getIdentifier("rbUseCalendarForWeight" + (mUseCalendarForWeight ? "Yes" : "No"), "id", getPackageName());
        RadioButton but = findViewById(mUseCalendarID);
        if (but != null) {
            but.setChecked(true);
        }
        RadioGroup radiogroup = findViewById(R.id.rgUseCalendarForWeight);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbUseCalendarForWeightYes:
                            mUseCalendarForWeight = true;
                            break;
                        case R.id.rbUseCalendarForWeightNo:
                            mUseCalendarForWeight = false;
                            break;
                        default:
                            mUseCalendarForWeight = false;
                            break;
                    }
                }
            });
        }

        int mPictureID = getResources().getIdentifier("rbShowPicture" + (mShowPicture ? "Yes" : "No"), "id", getPackageName());
        but = findViewById(mPictureID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = findViewById(R.id.rgShowPicture);

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
        but = findViewById(mExpID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = findViewById(R.id.rgShowExplanation);

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


        int mAmountDefaultID = getResources().getIdentifier("rbShowAmountDefaultButton" + (mShowAmountDefaultButton ? "Yes" : "No"), "id", getPackageName());
        but = findViewById(mAmountDefaultID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = findViewById(R.id.rgShowAmountDefaultButton);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowAmountDefaultButtonYes:
                            mShowAmountDefaultButton = true;
                            break;
                        case R.id.rbShowAmountDefaultButtonNo:
                            mShowAmountDefaultButton = false;
                            break;
                        default:
                            mShowAmountDefaultButton = false;
                            break;
                    }
                }
            });
        }

        int mAmountLastDayID = getResources().getIdentifier("rbShowAmountLastDayButton" + (mShowAmountLastDayButton ? "Yes" : "No"), "id", getPackageName());
        but = findViewById(mAmountLastDayID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = findViewById(R.id.rgShowAmountLastDayButton);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowAmountLastDayButtonYes:
                            mShowAmountLastDayButton = true;
                            break;
                        case R.id.rbShowAmountLastDayButtonNo:
                            mShowAmountLastDayButton = false;
                            break;
                        default:
                            mShowAmountLastDayButton = false;
                            break;
                    }
                }
            });
        }
    }
}
