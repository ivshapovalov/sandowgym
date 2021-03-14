package ru.ivan.sandowgym.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.rosuh.filepicker.bean.FileItemBeanImpl;
import me.rosuh.filepicker.config.AbstractFileFilter;
import me.rosuh.filepicker.config.FilePickerManager;
import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.scheduler.Scheduler;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxAuthTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpAuthTask;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.mSettings;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityOptions extends ActivityAbstract {

    static final int TIME_DIALOG_ID = 999;

    private TimePicker mTimePicker;
    private TextView timeDisplay;

    private int mOptionActivityRowsOnPageInLists;
    private String mOptionActivityBackupLocalFolder;
    private String mOptionActivityNewBackupLocalFolder;
    private String mOptionActivityBackupFtpHost;
    private String mOptionActivityBackupFtpLogin;
    private String mOptionActivityBackupFtpPassword;
    private String mOptionActivityBackupDropboxAccessToken;
    private boolean mOptionActivityBackupScheduleEnabled;
    private int mOptionActivityBackupScheduleDateTimeHour;
    private int mOptionActivityBackupScheduleDateTimeMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Common.updatePreferences(this);
        readOptions();

        ImageButton ibFTPTestConnectionButton = findViewById(R.id.ibFTPTestConnectionButton);

        ibFTPTestConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibFTPTestConnection_onClick(ibFTPTestConnectionButton);
            }
        });

        ImageButton ibDropboxTestConnectionButton = findViewById(R.id.ibDropboxTestConnectionButton);

        ibDropboxTestConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibDropboxTestConnectionButton_onClick(ibDropboxTestConnectionButton);
            }
        });
        if (mOptionActivityNewBackupLocalFolder != null && !mOptionActivityNewBackupLocalFolder.isEmpty()) {
            mOptionActivityBackupLocalFolder = mOptionActivityNewBackupLocalFolder;
        }

        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOptionActivityNewBackupLocalFolder != null && !mOptionActivityNewBackupLocalFolder.isEmpty()) {
            mOptionActivityBackupLocalFolder = mOptionActivityNewBackupLocalFolder;
        }
        setPreferencesOnScreen();
    }

    private void readOptions() {
        mOptionActivityRowsOnPageInLists = Constants.mOptionRowsOnPageInLists;
        mOptionActivityBackupLocalFolder = Constants.mOptionBackupLocalFolder;
        mOptionActivityBackupFtpHost = Constants.mOptionBackupFtpHost;
        mOptionActivityBackupFtpLogin = Constants.mOptionBackupFtpLogin;
        mOptionActivityBackupFtpPassword = Constants.mOptionBackupFtpPassword;
        mOptionActivityBackupDropboxAccessToken = Constants.mOptionBackupDropboxAccessToken;
        mOptionActivityBackupScheduleEnabled = Constants.mOptionBackupScheduleEnabled;
        mOptionActivityBackupScheduleDateTimeHour = Constants.mOptionBackupScheduleDateTimeHour;
        mOptionActivityBackupScheduleDateTimeMinutes = Constants.mOptionBackupScheduleDateTimeMinutes;
    }

    private void saveOptions() {
        Constants.mOptionRowsOnPageInLists = mOptionActivityRowsOnPageInLists;
        Constants.mOptionBackupLocalFolder = mOptionActivityBackupLocalFolder;
        Constants.mOptionBackupFtpHost = mOptionActivityBackupFtpHost;
        Constants.mOptionBackupFtpLogin = mOptionActivityBackupFtpLogin;
        Constants.mOptionBackupFtpPassword = mOptionActivityBackupFtpPassword;
        Constants.mOptionBackupDropboxAccessToken = mOptionActivityBackupDropboxAccessToken;
        Constants.mOptionBackupScheduleEnabled = mOptionActivityBackupScheduleEnabled;
        Constants.mOptionBackupScheduleDateTimeHour = mOptionActivityBackupScheduleDateTimeHour;
        Constants.mOptionBackupScheduleDateTimeMinutes = mOptionActivityBackupScheduleDateTimeMinutes;

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Constants.APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS, Constants.mOptionRowsOnPageInLists);
        editor.putString(Constants.APP_PREFERENCES_BACKUP_LOCAL_FOLDER, Constants.mOptionBackupLocalFolder);
        editor.putString(Constants.APP_PREFERENCES_BACKUP_FTP_HOST, Constants.mOptionBackupFtpHost);
        editor.putString(Constants.APP_PREFERENCES_BACKUP_FTP_LOGIN, Constants.mOptionBackupFtpLogin);
        editor.putString(Constants.APP_PREFERENCES_BACKUP_FTP_PASSWORD, Constants.mOptionBackupFtpPassword);
        editor.putString(Constants.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, Constants.mOptionBackupDropboxAccessToken);
        editor.putBoolean(Constants.APP_PREFERENCES_BACKUP_SCHEDULE_ENABLED, Constants.mOptionBackupScheduleEnabled);
        editor.putInt(Constants.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_HOUR, Constants.mOptionBackupScheduleDateTimeHour);
        editor.putInt(Constants.APP_PREFERENCES_BACKUP_SCHEDULE_TIME_MINUTES, Constants.mOptionBackupScheduleDateTimeMinutes);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        if (requestCode == FilePickerManager.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                List<String> files = FilePickerManager.INSTANCE.obtainData();
                if (files != null && files.size() == 1) {
                    mOptionActivityNewBackupLocalFolder = files.get(0).trim();
                }
            }
        }
    }

    public void tvBackupScheduleTime_onClick(View view) {
        showDialog(TIME_DIALOG_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        timePickerListener, mOptionActivityBackupScheduleDateTimeHour, mOptionActivityBackupScheduleDateTimeMinutes, DateFormat.is24HourFormat(this));
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            (view, selectedHour, selectedMinute) -> {
                mOptionActivityBackupScheduleDateTimeHour = selectedHour;
                mOptionActivityBackupScheduleDateTimeMinutes = selectedMinute;

                timeDisplay.setText(new StringBuilder().append(pad(mOptionActivityBackupScheduleDateTimeHour))
                        .append(":").append(pad(mOptionActivityBackupScheduleDateTimeMinutes)));

                mTimePicker.setHour(mOptionActivityBackupScheduleDateTimeHour);
                mTimePicker.setMinute(mOptionActivityBackupScheduleDateTimeMinutes);

            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
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
                mOptionActivityRowsOnPageInLists = Integer.valueOf(txtRowsOnPage.getText().toString());
            } catch (ClassCastException e) {

            }
        }

        int mBackupLocalFolderID = getResources().getIdentifier("etBackupLocalFolder", "id", getPackageName());
        EditText etBackupLocalFolder = findViewById(mBackupLocalFolderID);
        if (etBackupLocalFolder != null) {
            try {
                mOptionActivityBackupLocalFolder = etBackupLocalFolder.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mFtpHostID = getResources().getIdentifier("etFtpHost", "id", getPackageName());
        EditText txtFtpHost = findViewById(mFtpHostID);
        if (txtFtpHost != null) {
            try {
                mOptionActivityBackupFtpHost = txtFtpHost.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mFtpLoginID = getResources().getIdentifier("etFtpLogin", "id", getPackageName());
        EditText txtFtpLogin = findViewById(mFtpLoginID);
        if (txtFtpLogin != null) {
            try {
                mOptionActivityBackupFtpLogin = txtFtpLogin.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mFtpPasswordID = getResources().getIdentifier("etFtpPassword", "id", getPackageName());
        EditText txtFtpPassword = findViewById(mFtpPasswordID);
        if (txtFtpPassword != null) {
            try {
                mOptionActivityBackupFtpPassword = txtFtpPassword.getText().toString();
            } catch (ClassCastException e) {

            }
        }

        int mDropboxAccessTokenID = getResources().getIdentifier("etDropboxAccessToken", "id", getPackageName());
        EditText txtDropboxAccessToken = findViewById(mDropboxAccessTokenID);
        if (txtDropboxAccessToken != null) {
            try {
                mOptionActivityBackupDropboxAccessToken = txtDropboxAccessToken.getText().toString();
            } catch (ClassCastException e) {

            }
        }
        saveOptions();
        handleSchedule();
    }

    private void handleSchedule() {
        if (Constants.mOptionBackupScheduleEnabled) {
            Scheduler.cancelAllWorks(this);
            Scheduler.scheduleNewDailyBackupTask(this);
        } else {
            Scheduler.cancelAllWorks(this);
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
            txtRowsOnPAge.setText(String.valueOf(mOptionActivityRowsOnPageInLists));
        }

        int mBackupLocalFolderID = getResources().getIdentifier("etBackupLocalFolder", "id", getPackageName());
        EditText etBackupLocalFolder = findViewById(mBackupLocalFolderID);
        if (etBackupLocalFolder != null) {
            etBackupLocalFolder.setText(mOptionActivityBackupLocalFolder);
        }

        int mFtpHostID = getResources().getIdentifier("etFtpHost", "id", getPackageName());
        EditText txtFtpHost = findViewById(mFtpHostID);
        if (txtFtpHost != null) {
            txtFtpHost.setText(mOptionActivityBackupFtpHost);
        }

        int mFtpLoginID = getResources().getIdentifier("etFtpLogin", "id", getPackageName());
        EditText txtFtpLogin = findViewById(mFtpLoginID);
        if (txtFtpLogin != null) {
            txtFtpLogin.setText(mOptionActivityBackupFtpLogin);
        }

        int mFtpPasswordID = getResources().getIdentifier("etFtpPassword", "id", getPackageName());
        EditText txtFtpPassword = findViewById(mFtpPasswordID);
        if (txtFtpPassword != null) {
            txtFtpPassword.setText(mOptionActivityBackupFtpPassword);
        }

        int mDropboxAccessTokenID = getResources().getIdentifier("etDropboxAccessToken", "id", getPackageName());
        EditText txtDropboxAccessToken = findViewById(mDropboxAccessTokenID);
        if (txtDropboxAccessToken != null) {
            txtDropboxAccessToken.setText(mOptionActivityBackupDropboxAccessToken);
        }

        timeDisplay = findViewById(R.id.tvBackupScheduleTime);

        timeDisplay.setText(
                new StringBuilder().append(pad(mOptionActivityBackupScheduleDateTimeHour))
                        .append(":").append(pad(mOptionActivityBackupScheduleDateTimeMinutes)));
        changeBackupScheduleButtonsVisibility();

        mTimePicker = findViewById(R.id.timePicker);
        mTimePicker.setHour(mOptionActivityBackupScheduleDateTimeHour);
        mTimePicker.setMinute(mOptionActivityBackupScheduleDateTimeMinutes);

        int mBackupID = getResources().getIdentifier("rbScheduledBackup" + (mOptionActivityBackupScheduleEnabled ? "Yes" : "No"), "id", getPackageName());
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
                            mOptionActivityBackupScheduleEnabled = true;
                            break;
                        case R.id.rbScheduledBackupNo:
                        default:
                            mOptionActivityBackupScheduleEnabled = false;
                            break;
                    }
                    changeBackupScheduleButtonsVisibility();
                }
            });
        }
    }

    void changeBackupScheduleButtonsVisibility() {
        timeDisplay = findViewById(R.id.tvBackupScheduleTime);
        timeDisplay.setVisibility(mOptionActivityBackupScheduleEnabled ? View.VISIBLE : View.GONE);
        Button btBackupsSchedule = findViewById(R.id.btBackupScheduleTimeShow);
        btBackupsSchedule.setVisibility(mOptionActivityBackupScheduleEnabled ? View.VISIBLE : View.GONE);
        if (!mOptionActivityBackupScheduleEnabled) {
            mOptionActivityBackupScheduleDateTimeHour = 0;
            mOptionActivityBackupScheduleDateTimeMinutes = 0;
        }
    }

    public void ibFTPTestConnection_onClick(View view) {
        savePreferences();
        try {
            blink(view, this);
            displayMessage(ActivityOptions.this, "Test FTP connection started", true);
            FtpAuthTask ftpAuthTask = new FtpAuthTask(this.getApplicationContext());
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

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-dropboxClient").build();
            DbxClientV2 dropboxClient = new DbxClientV2(config, mOptionActivityBackupDropboxAccessToken);
            if (dropboxClient != null) {
                DropboxAuthTask dropboxAuthTask = new DropboxAuthTask(this.getApplicationContext(), dropboxClient);
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

    //TODO delete
    public void btBackupScheduleTimeShow_onClick(View view) {
        if (mOptionActivityBackupScheduleEnabled) {
            List<String> backups = Scheduler.getActiveWorks(this);
            if (backups.size() > 0) {
                //            scheduleWork(Scheduler.TAG_BACKUP); // schedule your work
                String srt = "SCHEDULED BACKUPS: " + System.getProperty("line.separator") +
                        backups.stream().map(Object::toString)
                                .collect(Collectors.joining(System.getProperty("line.separator")));
                // srt= "SCHEDULED BACKUPS: "+backups.toString().replaceAll("\\[|\\]|,",System.getProperty("line.separator"));
                displayMessage(this, srt, true);
            }

        }
    }

    public void btBackupScheduleSaveTest_onClick(View view) {
        //handleSchedule();
    }

    public void ibSelectBackupLocalFolder_onClick(View view) {
        FilePickerManager.INSTANCE
                .from(this)
                .maxSelectable(1)
                .filter(new AbstractFileFilter() {
                    @NotNull
                    @Override
                    public ArrayList<FileItemBeanImpl> doFilter(@NotNull ArrayList<FileItemBeanImpl> arrayList) {
                        return new ArrayList<>(arrayList.stream()
                                .filter(item -> {
                                    return item.isDir();
                                })
                                .collect(Collectors.toList()));
                    }
                })
                .setTheme(R.style.FilePickerThemeReply)
                .skipDirWhenSelect(false)
                .forResult(FilePickerManager.REQUEST_CODE);
    }

    public void btClearBackupLocalFolder_onClick(View view) {
        int mBackupLocalFolderID = getResources().getIdentifier("etBackupLocalFolder", "id", getPackageName());
        EditText etBackupLocalFolder = findViewById(mBackupLocalFolderID);
        if (etBackupLocalFolder != null) {
            try {
                mOptionActivityBackupLocalFolder = "";
                etBackupLocalFolder.setText("");
            } catch (ClassCastException e) {

            }
        }
    }
}
