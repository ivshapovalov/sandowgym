package ru.ivan.sandowgym.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.*;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Tasks.BackgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.Tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.Tasks.BackgroundTasks.DropboxUploadTask;
import ru.ivan.sandowgym.common.Tasks.BackgroundTasks.ExportToFileTask;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class ActivityMain extends ActivityAbstract {

    private SharedPreferences mSettings;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ROWS_ON_PAGE_IN_LISTS = "rows_on_page_in_lists";
    public static final String APP_PREFERENCES_BACKUP_FTP_HOST = "backup_ftp_host";
    public static final String APP_PREFERENCES_BACKUP_FTP_LOGIN = "backup_ftp_login";
    public static final String APP_PREFERENCES_BACKUP_FTP_PASSWORD = "backup_ftp_password";
    public static final String APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN= "backup_dropbox_access_token";
    public static final String APP_PREFERENCES_TRAINING_SHOW_PICTURE = "training_show_picture";
    public static final String APP_PREFERENCES_TRAINING_SHOW_EXPLANATION = "training_show_explanation";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON = "training_show_volume_default_button";
    public static final String APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON = "training_show_volume_last_day_button";
    public static final String APP_PREFERENCES_TRAINING_PLUS_MINUS_BUTTON_VALUE = "training_plus_minus_button_value";

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
    }

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
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }

        Date lastDateOfWeightUpdate = getLastDateOfWeightChange();
        int tvMessageID = getResources().getIdentifier("tvMessage", "id", getPackageName());
        TextView tvMessage = (TextView) findViewById(tvMessageID);
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

        List<Exercise> list = new ArrayList<Exercise>();
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

    public void imExportToDropbox_onClick(View view) {
        if (Common.isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        processingInProgress = true;
        try {
            blink(view, this);
            File outputDir = getCacheDir();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String fileName = "sandow-gym-" + dateFormat.format(date)+".xls";
            File outputFile = new File(outputDir, fileName);
            //for tests
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//            File outputFile = new File(exportDir, "trainings.xls");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(),outputFile,0,0);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(dropboxUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this.getApplicationContext(), tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();

        } catch (Exception e) {
            e.printStackTrace();
            processingInProgress = false;
        }
    }

}
