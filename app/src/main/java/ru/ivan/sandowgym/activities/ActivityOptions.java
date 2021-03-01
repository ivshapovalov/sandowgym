package ru.ivan.sandowgym.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.util.ArrayList;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxAuthTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpAuthTask;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityOptions extends ActivityAbstract {

    public SharedPreferences mSettings;
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

        ImageButton ibFTPTestConnectionButton = (ImageButton) findViewById(R.id.ibFTPTestConnectionButton);

        ibFTPTestConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibFTPTestConnection_onClick(ibFTPTestConnectionButton);
            }
        });

        ImageButton ibDropboxTestConnectionButton = (ImageButton) findViewById(R.id.ibDropboxTestConnectionButton);

        ibDropboxTestConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibDropboxTestConnectionButton_onClick(ibDropboxTestConnectionButton);
            }
        });

        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }

    public void buttonSave_onClick(View view) {

        blink(view, this);
        savePreferences();
        this.finish();

    }

    private void savePreferences() {
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

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, mRowsOnPageInLists);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST, mFtpHost);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN, mFtpLogin);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD, mFtpPassword);
        editor.putString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, mDropboxAccessToken);
        editor.apply();
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

    public void ibFTPTestConnection_onClick(View view) {
        savePreferences();
        try {
            blink(view, this);
            displayMessage(ActivityOptions.this, "Test FTP connection started");
            FtpAuthTask ftpAuthTask = new FtpAuthTask(mSettings);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(ftpAuthTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityOptions.this, tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityOptions.this, "Test FTP connection failed " + e.getMessage());
        }
    }

    public void ibDropboxTestConnectionButton_onClick(View view) {
        savePreferences();
        try {
            blink(view, this);
            displayMessage(ActivityOptions.this, "Test Dropbox credentials started");

            mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-dropboxClient").build();
            DbxClientV2 dropboxClient = new DbxClientV2(config, mDropboxAccessToken);
            if (dropboxClient != null) {
                DropboxAuthTask dropboxAuthTask = new DropboxAuthTask(dropboxClient);

                List<BackgroundTask> tasks = new ArrayList<>();
                tasks.add(dropboxAuthTask);
                BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityOptions.this, tasks);
                AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();
            } else {
                displayMessage(ActivityOptions.this, "Test Dropbox credentials failed ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityOptions.this, "Test Dropbox credentials failed " + e.getMessage());
        }
    }
}
