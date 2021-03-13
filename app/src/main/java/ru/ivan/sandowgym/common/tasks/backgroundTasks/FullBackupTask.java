package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.isProcessingInProgress;
import static ru.ivan.sandowgym.common.Constants.processingInProgress;

public class FullBackupTask implements BackgroundTask {
    private Context context;
    private boolean doInBackground;

    public FullBackupTask(Context context, boolean doInBackground) {
        this.context = context;
        this.doInBackground = doInBackground;
        Common.updatePreferences(context);

    }

    @Override
    public boolean execute() {
        if (isProcessingInProgress(context)) {
            return false;
        }
        displayMessage(context, "Full backup started", false);
        processingInProgress = true;

        File outputFile = Common.getBackupFile("sandow-gym", ".xlsx");
        //fileName = "sandow-gym-20210305-172135.xlsx";
        //for tests
//                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//                outputFile = new File(exportDir, "trainings.xlsx");
        try {
            if (outputFile.createNewFile()) {
            } else {
            }
            ExportToFileTask exportToFileTask = new ExportToFileTask(context, outputFile, 0, 0);

            FtpUploadTask ftpUploadTask = new FtpUploadTask(context, outputFile);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, Constants.mOptionBackupDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(context, outputFile, client);

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
            Common.saveException(context, e);
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
