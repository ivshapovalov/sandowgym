package ru.ivan.sandowgym.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.*;

import static ru.ivan.sandowgym.common.Common.*;

import ru.ivan.sandowgym.common.Tasks.BackgroundTask;
import ru.ivan.sandowgym.common.Tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.Tasks.DropboxDownloadTask;
import ru.ivan.sandowgym.common.Tasks.DropboxListFilesTask;
import ru.ivan.sandowgym.common.Tasks.DropboxUploadTask;
import ru.ivan.sandowgym.common.Tasks.ExportToFileTask;
import ru.ivan.sandowgym.common.Tasks.FtpListFilesTask;
import ru.ivan.sandowgym.common.Tasks.FtpUploadTask;
import ru.ivan.sandowgym.common.Tasks.ImportFromFileTask;

public class ActivityFileExportImport extends ActivityAbstract {

    private SharedPreferences mSettings;
    private String mDropboxAccessToken;
    private long mDateFrom;
    private long mDateTo;
    private String downloadFile;
    private String downloadType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);
        getIntentParams();
        getPreferencesFromFile();
        updateScreen();
        setTitleOfActivity(this);
        if (downloadType != null && downloadType.equals("dropbox") && downloadFile != null && !"".equals(downloadFile)) {
            importFileFromDropbox(downloadFile);
        }
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        long mCurrentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        long mCurrentDateToInMillis = intent.getLongExtra("currentDateToInMillis", 0);
        downloadFile = intent.getStringExtra("downloadFile");
        downloadType = intent.getStringExtra("downloadType");
        mDateFrom = mCurrentDateInMillis;
        mDateTo = mCurrentDateToInMillis;
    }

    public void btClose_onClick(View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityFileExportImport.this, ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void tvDayFrom_onClick(View view) {

        blink(view, this);
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        blink(view, this);
        day_onClick(false);
    }

    private void day_onClick(boolean isBeginDate) {

        Intent intent = new Intent(ActivityFileExportImport.this, ActivityCalendarView.class);
        intent.putExtra("isBeginDate", isBeginDate);
        intent.putExtra("currentActivity", "ActivityFileExportImport");

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        intent.putExtra("currentDateInMillis", 0);
        intent.putExtra("currentDateToInMillis", "");
        if (tvDayFrom != null) {
            if (!"".equals(String.valueOf(tvDayFrom.getText()).trim())) {
                intent.putExtra("currentDateInMillis", convertStringToDate(String.valueOf(tvDayFrom.getText())).getTime());
            }
        }
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            if (!"".equals(String.valueOf(tvDayTo.getText()).trim())) {
                intent.putExtra("currentDateToInMillis", convertStringToDate(String.valueOf(tvDayTo.getText())).getTime());
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btImportFromFile_onClick(View view) {
        blink(view, this);
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        processingInProgress = true;
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (exportDir.exists()) {
                File file = new File(exportDir, "trainings.xls");
                if (file.exists()) {
                    try {
                        ImportFromFileTask importFromFileTask = new ImportFromFileTask(this.getApplicationContext(), file);
                        String message = importFromFileTask.executeAndMessage();
                        displayMessage(message);

                    } catch (Exception e) {
                        displayMessage("File didn't imported  " + file.getPath());
                    } finally {
                        processingInProgress = false;
                    }
                }
            }
        } catch (Exception e) {
            processingInProgress = false;
        }
    }

    public void btDayFromClear_onClick(final View view) {
        blink(view, this);
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }
    }

    public void btDayToClear_onClick(final View view) {
        blink(view, this);
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            tvDayTo.setText("");
        }
    }

    private void updateScreen() {

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView etDayFrom = (TextView) findViewById(mDayFromID);
        if (etDayFrom != null) {
            if (mDateFrom == 0) {
                etDayFrom.setText("");
            } else {
                etDayFrom.setText(ConvertMillisToString(mDateFrom));
            }
        }

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView etDayTo = (TextView) findViewById(mDayToID);
        if (etDayTo != null) {
            if (mDateTo == 0) {
                etDayTo.setText("");
            } else {
                etDayTo.setText(ConvertMillisToString(mDateTo));
            }
        }
    }

    public void btExportToFile_onClick(View view) {

        blink(view, this);
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        processingInProgress = true;
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "trainings.xls");
        try {
            if (file.createNewFile()) {
            } else {
            }
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), file, mDateFrom, mDateTo);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this.getApplicationContext(), tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();

        } catch (Exception e) {
            displayMessage("File didn't created  " + file.getPath());
            processingInProgress = false;
        }
    }

    public void btExportToFTP_onClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            File outputDir = getCacheDir();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String fileName = "sandow-gym-" + dateFormat.format(date) + ".xls";
            File outputFile = new File(outputDir, fileName);
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//            File outputFile = new File(exportDir, "trainings.xls");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, mDateFrom, mDateTo);

            FtpUploadTask ftpUploadTask = new FtpUploadTask(mSettings, outputFile);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(ftpUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this.getApplicationContext(), tasks);
            AsyncTask<Void, Long, Boolean> done = backgroundTaskExecutor.execute();
            displayMessage("FTP upload successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("FTP upload failed! " + e.getMessage());
            processingInProgress = false;
        }
    }

    public void btExportToDropbox_onClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            File outputDir = getCacheDir();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String fileName = "sandow-gym-" + dateFormat.format(date) + ".xls";
            File outputFile = new File(outputDir, fileName);
            //for tests
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//            File outputFile = new File(exportDir, "trainings.xls");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, mDateFrom, mDateTo);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(dropboxUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this.getApplicationContext(), tasks);
            AsyncTask<Void, Long, Boolean> doneUpload = backgroundTaskExecutor.execute();

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Tasks failed! " + e.getMessage());
            processingInProgress = false;
        }
    }

    public void btBackupBD_onClick(View view) {
        try {
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            File dbFile = getApplicationContext().getDatabasePath("trainingCalendar");
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/trainingCalendar_copy.db";

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
            String message = "The database is successfully copied to " + outFileName;
            displayMessage(message);
        } catch (Exception e) {
            String message = "Database copy failed!";
            displayMessage(message);
        }
        processingInProgress = false;
    }

    private void displayMessage(String message) {
        Toast toast = Toast.makeText(ActivityFileExportImport.this,
                message, Toast.LENGTH_SHORT);
        toast.show();
        int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
        TextView tvPath = (TextView) findViewById(mPath);
        if (tvPath != null) {
            tvPath.setText(message);
        }
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN)) {
            mDropboxAccessToken = mSettings.getString(ActivityMain.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN, "");
        } else {
            mDropboxAccessToken = "";
        }
    }

    public void btImportFromDropbox_onClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxListFilesTask dropboxListFilesTask = new DropboxListFilesTask(client);
            AsyncTask<Void, Long, ArrayList<String>> metadatas = dropboxListFilesTask.execute();
            ArrayList<String> fileNames = metadatas.get();
            Intent intent = new Intent(getApplicationContext(), ActivityFilesList.class);
            intent.putExtra("downloadType", "dropbox");
            intent.putStringArrayListExtra("downloadFiles", fileNames);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Tasks failed! " + e.getMessage());
            processingInProgress = false;
        }
    }

    public void importFileFromDropbox(String fileName) {
        try {
            File outputDir = getCacheDir();
            File outputFile = new File(outputDir, fileName);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, mDropboxAccessToken);
            DropboxDownloadTask dropboxDownloadTask = new DropboxDownloadTask(outputFile, client);
            ImportFromFileTask importFromFileTask = new ImportFromFileTask(this.getApplicationContext(), outputFile);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(dropboxDownloadTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(this.getApplicationContext(), tasks);
            AsyncTask<Void, Long, Boolean> doneUpload = backgroundTaskExecutor.execute();
            doneUpload.get();

            String message = importFromFileTask.executeAndMessage();
            displayMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Tasks failed! " + e.getMessage());
            processingInProgress = false;
        }
    }

    public void btImportFromFTP_onClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            FtpListFilesTask ftpListFilesTask = new FtpListFilesTask(mSettings);
            AsyncTask<Void, Long, ArrayList<String>> task = ftpListFilesTask.execute();
            ArrayList<String> fileNames = task.get();

            Collections.sort(fileNames);
            Intent intent = new Intent(getApplicationContext(), ActivityFilesList.class);
            intent.putExtra("downloadType", "ftp");
            intent.putStringArrayListExtra("downloadFiles", fileNames);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Tasks failed! " + e.getMessage());
            processingInProgress = false;
        }

    }
}

