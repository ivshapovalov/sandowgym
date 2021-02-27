package ru.ivan.sandowgym.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxUploadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.ExportToFileTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpUploadTask;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.dbCurrentUser;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.isProcessingInProgress;
import static ru.ivan.sandowgym.common.Common.processingInProgress;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityMain extends ActivityAbstract {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private SharedPreferences mSettings;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS = "rows_on_page_in_lists";
    public static final String APP_PREFERENCES_BACKUP_FTP_HOST = "backup_ftp_host";
    public static final String APP_PREFERENCES_BACKUP_FTP_LOGIN = "backup_ftp_login";
    public static final String APP_PREFERENCES_BACKUP_FTP_PASSWORD = "backup_ftp_password";
    public static final String APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN = "backup_dropbox_access_token";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_AMOUNT_DEFAULT_BUTTON = "training_show_amount_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_AMOUNT_LAST_DAY_BUTTON = "training_show_amount_last_day_button";
    public static final String APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE = "training_plus_minus_button_value";
    public static final String APP_PREFERENCES_TRAINING_USE_CALENDAR_FOR_WEIGHT = "training_use_calendar_for_weight";

    private final int maxVerticalButtonCount = 10;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);
    private String mDropboxAccessToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPreferencesFromFile();
        showElementsOnScreen();
        setTitleOfActivity(this);
        setPermissions();
    }

    private void setPermissions() {
        ActivityCompat.requestPermissions(ActivityMain.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(ActivityMain.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                2);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(ActivityMain.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//            case 2: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(ActivityMain.this, "Permission denied to write your External storage", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN)) {
            mDropboxAccessToken = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, "");
        } else {
            mDropboxAccessToken = "";
        }
    }

    private Date getLastDateOfWeightChange() {
        return new Date();
    }

    private void showElementsOnScreen() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        for (int i = 0; i <= maxVerticalButtonCount; i++) {
            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
            Button btName = findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }

        Date lastDateOfWeightUpdate = getLastDateOfWeightChange();
        int tvMessageID = getResources().getIdentifier("tvMessage", "id", getPackageName());
        TextView tvMessage = findViewById(tvMessageID);
        if (tvMessage != null) {
            tvMessage.setText(" ");
        }
    }

    public void btUsers_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btWeightCalendarList_onClick(View view) {
        blink(view, this);
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityWeightChangeCalendarList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btExercises_onClick(final View view) {
        blink(view, this);
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityExercisesList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btTrainings_onClick(final View view) {

        blink(view, this);
        if (isUserDefined() & isDBNotEmpty()) {
            Intent intent = new Intent(ActivityMain.this, ActivityTrainingsList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btNewTraining_onClick(final View view) {

        blink(view, this);
        if (isUserDefined() & isDBNotEmpty()) {
            Intent intent = new Intent(ActivityMain.this, ActivityTraining.class);
            intent.putExtra("isNew", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btTools_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityMain.this, ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private boolean isDBNotEmpty() {

        List<Exercise> list = new ArrayList<>();
        if (dbCurrentUser == null) {
        } else {
            list = DB.getAllActiveExercisesOfUser(dbCurrentUser.getId());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "There is no one active exercises!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Do you wish to close the program?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

    public void rowComplexExport_onClick(View view) {
        blink(view, this);
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        displayMessage(ActivityMain.this, "Complex backup started");
        processingInProgress = true;
        File outputDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        String fileName = "sandow-gym-" + dateFormat.format(date) + ".xlsx";
        File outputFile = new File(outputDir, fileName);
        //for tests
//                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//                outputFile = new File(exportDir, "trainings.xlsx");
        try {
            if (outputFile.createNewFile()) {
            } else {
            }
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, 0, 0);

            FtpUploadTask ftpUploadTask = new FtpUploadTask(mSettings, outputFile);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(ftpUploadTask);
            tasks.add(dropboxUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityMain.this, tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();
        } catch (Exception e) {
            displayMessage(ActivityMain.this, "Complex backup failed");
            processingInProgress = false;
        }
    }
}
