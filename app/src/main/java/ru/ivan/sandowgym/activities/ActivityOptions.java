package ru.ivan.sandowgym.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityOptions extends ActivityAbstract {

    private SharedPreferences mSettings;
    private int mRowsOnPageInLists;
    private String mFtpHost;
    private String mFtpLogin;
    private String mFtpPassword;
    private String mDropboxAccessToken;

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
        EditText txtRowsOnPage = findViewById(mRowsOnPageID);
        if (txtRowsOnPage != null) {
            try {
                mRowsOnPageInLists = Integer.valueOf(txtRowsOnPage.getText().toString());
            } catch (ClassCastException e) {

            }
        }

        int mFtpHostID = getResources().getIdentifier("etFtpHost", "id", getPackageName());
        EditText txtFtpHost = findViewById(mFtpHostID);
        if (txtFtpHost != null) {
            try {
                mFtpHost = txtFtpHost.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mFtpLoginID = getResources().getIdentifier("etFtpLogin", "id", getPackageName());
        EditText txtFtpLogin = findViewById(mFtpLoginID);
        if (txtFtpLogin != null) {
            try {
                mFtpLogin = txtFtpLogin.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mFtpPasswordID = getResources().getIdentifier("etFtpPassword", "id", getPackageName());
        EditText txtFtpPassword = findViewById(mFtpPasswordID);
        if (txtFtpPassword != null) {
            try {
                mFtpPassword = txtFtpPassword.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mDropboxAccessTokenID = getResources().getIdentifier("etDropboxAccessToken", "id", getPackageName());
        EditText txtDropboxAccessToken = findViewById(mDropboxAccessTokenID);
        if (txtDropboxAccessToken != null) {
            try {
                mDropboxAccessToken = txtDropboxAccessToken.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        blink(view, this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, mRowsOnPageInLists);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST, mFtpHost);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN, mFtpLogin);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD, mFtpPassword);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, mDropboxAccessToken);
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

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST)) {
            mFtpHost = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST, "");
        } else {
            mFtpHost = "";
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN)) {
            mFtpLogin = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN, "");
        } else {
            mFtpLogin = "";
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD)) {
            mFtpPassword = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD, "");
        } else {
            mFtpPassword = "";
        }
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN)) {
            mDropboxAccessToken = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, "");
        } else {
            mDropboxAccessToken = "";
        }

    }

    private void setPreferencesOnScreen() {

        int mRowsOnPageID = getResources().getIdentifier("etRowsOnPageInLists", "id", getPackageName());
        EditText txtRowsOnPAge = findViewById(mRowsOnPageID);
        if (txtRowsOnPAge != null) {
            txtRowsOnPAge.setText(String.valueOf(mRowsOnPageInLists));
        }

        int mFtpHostID = getResources().getIdentifier("etFtpHost", "id", getPackageName());
        EditText txtFtpHost = findViewById(mFtpHostID);
        if (txtFtpHost != null) {
            txtFtpHost.setText(mFtpHost);
        }

        int mFtpLoginID = getResources().getIdentifier("etFtpLogin", "id", getPackageName());
        EditText txtFtpLogin = findViewById(mFtpLoginID);
        if (txtFtpLogin != null) {
            txtFtpLogin.setText(mFtpLogin);
        }

        int mFtpPasswordID = getResources().getIdentifier("etFtpPassword", "id", getPackageName());
        EditText txtFtpPassword = findViewById(mFtpPasswordID);
        if (txtFtpPassword != null) {
            txtFtpPassword.setText(mFtpPassword);
        }

        int mDropboxAccessTokenID = getResources().getIdentifier("etDropboxAccessToken", "id", getPackageName());
        EditText txtDropboxAccessToken = findViewById(mDropboxAccessTokenID);
        if (txtDropboxAccessToken != null) {
            txtDropboxAccessToken.setText(mDropboxAccessToken);
        }
    }
}
