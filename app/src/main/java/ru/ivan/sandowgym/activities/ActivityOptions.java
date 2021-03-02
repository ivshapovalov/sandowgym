package ru.ivan.sandowgym.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.util.ArrayList;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.scheduler.Scheduler;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxAuthTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpAuthTask;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.mBackupScheduleEnabled;
import static ru.ivan.sandowgym.common.Common.mBackupScheduleTimeHour;
import static ru.ivan.sandowgym.common.Common.mBackupScheduleTimeMinutes;
import static ru.ivan.sandowgym.common.Common.mDropboxAccessToken;
import static ru.ivan.sandowgym.common.Common.mFtpHost;
import static ru.ivan.sandowgym.common.Common.mFtpLogin;
import static ru.ivan.sandowgym.common.Common.mFtpPassword;
import static ru.ivan.sandowgym.common.Common.mRowsOnPageInLists;
import static ru.ivan.sandowgym.common.Common.mSettings;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityOptions extends ActivityAbstract {

    static final int TIME_DIALOG_ID = 999;

    private TimePicker mTimePicker;
    private TextView timeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Common.getPreferences(this);

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

    public void tvBackupScheduleTime_onClick(View view) {
        showDialog(TIME_DIALOG_ID);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        timePickerListener, mBackupScheduleTimeHour, mBackupScheduleTimeMinutes, DateFormat.is24HourFormat(this));
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            (view, selectedHour, selectedMinute) -> {
                mBackupScheduleTimeHour = selectedHour;
                mBackupScheduleTimeMinutes = selectedMinute;

                timeDisplay.setText(new StringBuilder().append(pad(mBackupScheduleTimeHour))
                        .append(":").append(pad(mBackupScheduleTimeMinutes)));

                mTimePicker.setHour(mBackupScheduleTimeHour);
                mTimePicker.setMinute(mBackupScheduleTimeMinutes);

            };

    //Для показания минут настраиваем отображение 0 впереди чисел со значением меньше 10:
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
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
                Common.mRowsOnPageInLists = Integer.valueOf(txtRowsOnPage.getText().toString());
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
        editor.putBoolean(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_ENABLED, mBackupScheduleEnabled);
        editor.putInt(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_HOUR, mBackupScheduleTimeHour);
        editor.putInt(ActivityMain.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_MINUTES, mBackupScheduleTimeMinutes);
        editor.apply();

        Scheduler.cancelAllWorkers(this);

        if (mBackupScheduleEnabled) {
            Scheduler.scheduleBackupTask(this);
        }
    }

    public void buttonCancel_onClick(final View view) {

        blink(view, this);
        this.finish();

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

        timeDisplay = findViewById(R.id.tvBackupScheduleTime);

        timeDisplay.setText(
                new StringBuilder().append(pad(mBackupScheduleTimeHour))
                        .append(":").append(pad(mBackupScheduleTimeMinutes)));
        timeDisplay.setVisibility(mBackupScheduleEnabled ? View.VISIBLE : View.INVISIBLE);

        mTimePicker = findViewById(R.id.timePicker);
        mTimePicker.setHour(mBackupScheduleTimeHour);
        mTimePicker.setMinute(mBackupScheduleTimeMinutes);

        int mBackupID = getResources().getIdentifier("rbScheduledBackup" + (mBackupScheduleEnabled ? "Yes" : "No"), "id", getPackageName());
        RadioButton but = findViewById(mBackupID);
        if (but != null) {
            but.setChecked(true);
        }

        RadioGroup radiogroup = findViewById(R.id.rgScheduledBackup);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbScheduledBackupYes:
                            mBackupScheduleEnabled = true;
                            break;
                        case R.id.rbScheduledBackupNo:
                            mBackupScheduleEnabled = false;
                            break;
                        default:
                            mBackupScheduleEnabled = false;
                            break;
                    }
                    setBackupTimeVisible();
                }
            });
        }
    }

    void setBackupTimeVisible() {
        timeDisplay = findViewById(R.id.tvBackupScheduleTime);
        timeDisplay.setVisibility(mBackupScheduleEnabled ? View.VISIBLE : View.INVISIBLE);

    }

    public void ibFTPTestConnection_onClick(View view) {
        savePreferences();
        try {
            blink(view, this);
            displayMessage(ActivityOptions.this, "Test FTP connection started", true);
            FtpAuthTask ftpAuthTask = new FtpAuthTask(mSettings);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(ftpAuthTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityOptions.this, tasks);
            AsyncTask<Void, Long, Boolean> checkResult = backgroundTaskExecutor.execute();
            checkResult.get();
            displayMessage(ActivityOptions.this, "Test FTP connection finished successfully", true);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityOptions.this, "Test FTP connection failed " + e.getMessage(), true);
        }
    }

    public void ibDropboxTestConnectionButton_onClick(View view) {
        savePreferences();
        try {
            blink(view, this);
            displayMessage(ActivityOptions.this, "Test Dropbox connection started", false);
            mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-dropboxClient").build();
            DbxClientV2 dropboxClient = new DbxClientV2(config, mDropboxAccessToken);
            if (dropboxClient != null) {
                DropboxAuthTask dropboxAuthTask = new DropboxAuthTask(dropboxClient);
                List<BackgroundTask> tasks = new ArrayList<>();
                tasks.add(dropboxAuthTask);
                BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityOptions.this, tasks);
                AsyncTask<Void, Long, Boolean> checkResult = backgroundTaskExecutor.execute();
                checkResult.get();
                displayMessage(ActivityOptions.this, "Test Dropbox connection finished successfully", true);
            } else {
                displayMessage(ActivityOptions.this, "Test Dropbox connection failed ", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityOptions.this, "Test Dropbox connection failed ", true);
        }
    }

}
