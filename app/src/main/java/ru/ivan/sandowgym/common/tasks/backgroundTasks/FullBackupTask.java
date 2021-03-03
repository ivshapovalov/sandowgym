package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.activities.ActivityMain;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.isProcessingInProgress;
import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class FullBackupTask implements BackgroundTask {
    private Context context;
    private SharedPreferences mSettings;
    private String mDropboxAccessToken;
    private boolean doInBackground;

    public FullBackupTask(Context context, boolean doInBackground) {
        this.context = context;
        this.doInBackground = doInBackground;
        getPreferencesFromFile();

    }

    private void getPreferencesFromFile() {
        mSettings = context.getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN)) {
            mDropboxAccessToken = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, "");
        } else {
            mDropboxAccessToken = "";
        }
    }

    @Override
    public boolean execute() {
        if (isProcessingInProgress(context)) {
            return false;
        }
        displayMessage(context, "Full backup started", false);
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
            ExportToFileTask exportToFileTask = new ExportToFileTask(context, outputFile, 0, 0);

            FtpUploadTask ftpUploadTask = new FtpUploadTask(mSettings, outputFile);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(ftpUploadTask);
            tasks.add(dropboxUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(context, tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();
            if (!doInBackground) {
                done.get();
                displayMessage(context, "Full backup finished successfully ", false);
            }
        } catch (Exception e) {
            displayMessage(context, "Full backup failed", false);
            processingInProgress = false;
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "Full backup task";
    }
}
