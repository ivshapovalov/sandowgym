package ru.brainworkout.sandowgym.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import ru.brainworkout.sandowgym.R;

import static ru.brainworkout.sandowgym.common.Common.blink;
import static ru.brainworkout.sandowgym.common.Common.setTitleOfActivity;

public class ActivityOptions extends ActivityAbstract {

    private SharedPreferences mSettings;
    private int mRowsOnPageInLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getPreferencesFromFile();
        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }


    public void buttonSave_onClick(View view) {

        int mRowsOnPageID = getResources().getIdentifier("etRowsOnPageInLists", "id", getPackageName());
        EditText txt = (EditText) findViewById(mRowsOnPageID);
        if (txt != null) {
            try {
                mRowsOnPageInLists = Integer.valueOf(txt.getText().toString());
            } catch (ClassCastException e) {

            }
        }
        blink(view, this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, mRowsOnPageInLists);

        editor.apply();

        this.finish();

    }

    public void buttonCancel_onClick(final View view) {

        blink(view, this);
        this.finish();

    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS)) {
            mRowsOnPageInLists = mSettings.getInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, 17);
        } else {
            mRowsOnPageInLists = 17;
        }

    }

    private void setPreferencesOnScreen() {

        int mRowsOnPageID = getResources().getIdentifier("etRowsOnPageInLists", "id", getPackageName());
        EditText txt = (EditText) findViewById(mRowsOnPageID);
        if (txt != null) {
            txt.setText(String.valueOf(mRowsOnPageInLists));
        }
    }
}
