package ru.ivan.sandowgym.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.rosuh.filepicker.bean.FileItemBeanImpl;
import me.rosuh.filepicker.config.AbstractFileFilter;
import me.rosuh.filepicker.config.FilePickerConfig;
import me.rosuh.filepicker.config.FilePickerManager;
import me.rosuh.filepicker.filetype.DataBaseFileType;
import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.filetypes.DataSheetsFileType;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.DropboxListFilesTask;
import ru.ivan.sandowgym.common.tasks.FtpListFilesTask;
import ru.ivan.sandowgym.common.tasks.ImportFromFileTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxDownloadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxUploadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.ExportToFileTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpDownloadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FtpUploadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FullBackupTask;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.convertStringToDate;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.isProcessingInProgress;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.processingInProgress;

public class ActivityFileExportImport extends ActivityAbstract {

    private static final String DB_FILETYPE = "db";
    private static final String EXCEL_FILETYPE = "xlsx";

    private long mDateFrom;
    private long mDateTo;
    private String importFile;
    private String importType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);
        Common.updatePreferences(this);
        getIntentParams();
        updateScreen();
        setTitleOfActivity(this);

        if (importType != null && importFile != null && !"".equals(importFile)) {
            importDataFromFile();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (importType != null && importFile != null && !"".equals(importFile)) {
            importDataFromFile();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {

        if (requestCode == FilePickerManager.INSTANCE.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                List<String> files = FilePickerManager.INSTANCE.obtainData();
                if (files != null && files.size() == 1) {
                    String fileName = files.get(0).trim();
                    importFile = fileName;
                    if (fileName.endsWith(DB_FILETYPE)) {
                        importType = "db";
                    } else if (fileName.endsWith(EXCEL_FILETYPE)) {
                        importType = "excel";
                    }
                }
            } else {
                processingInProgress = false;
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                importFile = intent.getStringExtra("downloadFile");
                importType = intent.getStringExtra("downloadType");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                processingInProgress = false;
                //Write your code if there's no result
            }
        }
    }

    void importDataFromFile() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final String fileName = importFile;
        if (importType.equals("db")) {
            displayMessage(ActivityFileExportImport.this, "Restore from db file started", true);
            Runnable r = () -> restoreDbFromLocalFile(fileName);
            handler.postDelayed(r, 1000);
        } else if (importType.equals("excel")) {
            displayMessage(ActivityFileExportImport.this, "Import from local file started", true);
            Runnable r = () -> importDataFromLocalFile(fileName);
            handler.postDelayed(r, 1000);
        } else if (importType.equals("dropbox")) {
            displayMessage(ActivityFileExportImport.this, "Import from Dropbox started", true);
            Runnable r = () -> importDataFromDropboxFile(fileName);
            handler.postDelayed(r, 1000);
        } else if (importType.equals("ftp")) {
            displayMessage(ActivityFileExportImport.this, "Import from FTP started", true);
            Runnable r = () -> importDataFromFtpFile(fileName);
            handler.postDelayed(r, 1000);
        }

        importFile = null;
        importType = null;
    }

    private void restoreDbFromLocalFile(String fileName) {
        try {
            File dbFile = getApplicationContext().getDatabasePath(SQLiteDatabaseManager.DATABASE_NAME);
            File backupFile = new File(fileName);
            if (dbFile.exists() && backupFile.exists()) {
                InputStream input = new FileInputStream(backupFile);
                FileOutputStream output = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                input.close();
                String message = "The database is successfully restored from " + fileName + "\n Reboot application";
                displayMessage(ActivityFileExportImport.this, message, true);
            }
        } catch (Exception e) {
            String message = "Database restore failed!";
            displayMessage(ActivityFileExportImport.this, message, true);
        } finally {
            processingInProgress = false;
        }
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        long mCurrentDateInMillis = intent.getLongExtra("currentDateInMillis", 0);
        long mCurrentDateToInMillis = intent.getLongExtra("currentDateToInMillis", 0);
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
        intent.putExtra("currentActivity", getClass().getName());

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = findViewById(mDayFromID);
        intent.putExtra("currentDateInMillis", 0);
        intent.putExtra("currentDateToInMillis", "");
        if (tvDayFrom != null) {
            if (!"".equals(String.valueOf(tvDayFrom.getText()).trim())) {
                intent.putExtra("currentDateInMillis", convertStringToDate(String.valueOf(tvDayFrom.getText())).getTime());
            }
        }
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = findViewById(mDayToID);
        if (tvDayTo != null) {
            if (!"".equals(String.valueOf(tvDayTo.getText()).trim())) {
                intent.putExtra("currentDateToInMillis", convertStringToDate(String.valueOf(tvDayTo.getText())).getTime());
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDayFromClear_onClick(final View view) {

        blink(view, this);
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }

    }

    public void btDayToClear_onClick(final View view) {

        blink(view, this);
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = findViewById(mDayToID);
        if (tvDayTo != null) {
            tvDayTo.setText("");
        }

    }

    private void updateScreen() {

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView etDayFrom = findViewById(mDayFromID);
        if (etDayFrom != null) {
            if (mDateFrom == 0) {
                etDayFrom.setText("");
            } else {
                etDayFrom.setText(convertMillisToString(mDateFrom));
            }
        }

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView etDayTo = findViewById(mDayToID);
        if (etDayTo != null) {
            if (mDateTo == 0) {
                etDayTo.setText("");
            } else {
                etDayTo.setText(convertMillisToString(mDateTo));
            }
        }
    }

    public void btFullBackup_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to full backup data to 'xlsx' file (local, ftp, dropbox)?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fullBackupToFile_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void fullBackupToFile_onYesClick(View view) {
        try {
            FullBackupTask fullBackupTask = new FullBackupTask(this, true);
            fullBackupTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btExportToLocalFile_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to export data to 'xlsx' file?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exportToLocalFile_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void exportToLocalFile_onYesClick(View view) {

        blink(view, this);
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }

        displayMessage(ActivityFileExportImport.this, "Export to File started", false);
        processingInProgress = true;

        File outputFile = Common.getBackupFile("sandow-gym", ".xlsx");
        try {
            if (outputFile.createNewFile()) {
            }
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, mDateFrom, mDateTo);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
            backgroundTaskExecutor.execute();
        } catch (Exception e) {
            displayMessage(ActivityFileExportImport.this, "File didn't created  " + outputFile.getPath(), false);
            processingInProgress = false;
        }
    }

    public void btExportToFTP_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to export data to 'xlsx' file on FTP?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exportToFTP_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void exportToFTP_onYesClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            displayMessage(ActivityFileExportImport.this, "Export to FTP started", false);
            processingInProgress = true;
            File outputFile = Common.getBackupFile("sandow-gym", ".xlsx");
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//            outputFile = new File(exportDir, "trainings.xlsx");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, mDateFrom, mDateTo);

            FtpUploadTask ftpUploadTask = new FtpUploadTask(this.getApplicationContext(), outputFile);
            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(ftpUploadTask);

            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
            backgroundTaskExecutor.execute();
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "FTP upload failed! " + e.getMessage(), false);
            processingInProgress = false;
        }
    }

    public void btExportToDropbox_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to export data to 'xlsx' file on Dropbox?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exportToDropbox_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void exportToDropbox_onYesClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            displayMessage(ActivityFileExportImport.this, "Export to Dropbox started", false);
            processingInProgress = true;
            File outputFile = Common.getBackupFile("sandow-gym", ".xlsx");
            //for tests
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//            outputFile = new File(exportDir, "trainings.xlsx");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, mDateFrom, mDateTo);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, Constants.mOptionBackupDropboxAccessToken);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(this.getApplicationContext(), outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(dropboxUploadTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
            backgroundTaskExecutor.execute();
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "Export to Dropbox failed! " + e.getMessage(), false);
            processingInProgress = false;
        }
    }

    public void btBackupBD_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to backup DB to storage?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        backupBD_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void backupBD_onYesClick(View view) {
        try {
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            File dbFile = getApplicationContext().getDatabasePath(SQLiteDatabaseManager.DATABASE_NAME);
            FileInputStream fis = new FileInputStream(dbFile);

            File outputFile = Common.getBackupFile(SQLiteDatabaseManager.DATABASE_NAME, ".db");

            OutputStream output = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
            String message = "The database is successfully backed up to " + outputFile.getAbsolutePath();
            displayMessage(ActivityFileExportImport.this, message, true);
        } catch (Exception e) {
            String message = "Database backup failed!";
            displayMessage(ActivityFileExportImport.this, message, true);
        }
        processingInProgress = false;
    }

    public void btRestoreBD_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to restore DB from storage?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restoreBD_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void restoreBD_onYesClick(View view) {
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        processingInProgress = true;

        FilePickerManager.INSTANCE
                .from(this).enableSingleChoice()
                .setTheme(R.style.FilePickerThemeReply)
                .showHiddenFiles(true)
                .filter(new AbstractFileFilter() {
                    @NotNull
                    @Override
                    public ArrayList<FileItemBeanImpl> doFilter(@NotNull ArrayList<FileItemBeanImpl> arrayList) {
                        return new ArrayList<>(arrayList.stream()
                                .filter(item -> {
                                    if (item.isDir() || item.getFileType() instanceof DataBaseFileType) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                })
                                .collect(Collectors.toList()));
                    }
                })
                .forResult(FilePickerManager.REQUEST_CODE);

    }

    public void btImportFromLocalFile_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to import data from 'xlsx' file?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importFromLocalFile_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void importFromLocalFile_onYesClick(View view) {
        blink(view, this);
        if (isProcessingInProgress(this.getApplicationContext())) {
            return;
        }
        processingInProgress = true;
        FilePickerManager.INSTANCE
                .from(this).enableSingleChoice()
                .setTheme(R.style.FilePickerThemeReply)
                .showHiddenFiles(true)
                .storageType("Download", FilePickerConfig.STORAGE_CUSTOM_ROOT_PATH)
                .setCustomRootPath(Common.getBackupFolder().toString())
                .registerFileType(Arrays.asList(new DataSheetsFileType()), true)
                .forResult(FilePickerManager.REQUEST_CODE);
    }

    private void importDataFromLocalFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    ImportFromFileTask importFromFileTask = new ImportFromFileTask(this.getApplicationContext(), file);
                    List<BackgroundTask> tasks = new ArrayList<>();
                    tasks.add(importFromFileTask);
                    BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
                    AsyncTask<Void, Long, Boolean> doneImport = backgroundTaskExecutor.execute();
                    doneImport.get();
                } catch (Exception e) {
                    displayMessage(ActivityFileExportImport.this, "File didn't imported  " + file.getPath(), true);
                } finally {
                    processingInProgress = false;
                }
            }
        } finally {
            processingInProgress = false;
        }
    }

    public void btImportFileFromDropbox_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to import data from Dropbox?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importFileFromDropbox_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void importFileFromDropbox_onYesClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, Constants.mOptionBackupDropboxAccessToken);
            DropboxListFilesTask dropboxListFilesTask = new DropboxListFilesTask(this.getApplicationContext(), client);
            AsyncTask<Void, Long, ArrayList<String>> metadatas = dropboxListFilesTask.execute();
            ArrayList<String> fileNames = metadatas.get();
            Intent intent = new Intent(getApplicationContext(), ActivityFilesList.class);
            intent.putExtra("downloadType", "dropbox");
            intent.putStringArrayListExtra("downloadFiles", fileNames);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "Import from Dropbox tasks failed! " + e.getMessage(), true);
            processingInProgress = false;
        }
    }

    private void importDataFromDropboxFile(String fileName) {
        try {
            File outputDir = getCacheDir();
            File outputFile = new File(outputDir, fileName);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, Constants.mOptionBackupDropboxAccessToken);
            DropboxDownloadTask dropboxDownloadTask = new DropboxDownloadTask(this.getApplicationContext(), outputFile, client);
            ImportFromFileTask importFromFileTask = new ImportFromFileTask(this.getApplicationContext(), outputFile);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(dropboxDownloadTask);
            tasks.add(importFromFileTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
            AsyncTask<Void, Long, Boolean> doneImport = backgroundTaskExecutor.execute();
            doneImport.get();
            //String message = importFromFileTask.executeAndMessage();
            //displayMessage(ActivityFileExportImport.this, message, true);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "Import from Dropbox task failed! " + e.getMessage(), true);
        } finally {
            processingInProgress = false;
        }
    }

    public void btImportFileFromFTP_onClick(final View view) {
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to import data from FTP?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importFileFromFTP_onYesClick(view);
                    }
                }).setNegativeButton("No", null).show();
    }

    private void importFileFromFTP_onYesClick(View view) {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            processingInProgress = true;
            FtpListFilesTask ftpListFilesTask = new FtpListFilesTask(this.getApplicationContext());
            AsyncTask<Void, Long, ArrayList<String>> task = ftpListFilesTask.execute();
            ArrayList<String> fileNames = task.get();

            Intent intent = new Intent(getApplicationContext(), ActivityFilesList.class);
            intent.putExtra("downloadType", "ftp");
            intent.putStringArrayListExtra("downloadFiles", fileNames);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "Import From FTP task failed! " + e.getMessage(), true);
            processingInProgress = false;
        }
    }

    private void importDataFromFtpFile(String fileName) {
        try {
            File outputDir = getCacheDir();
            File outputFile = new File(outputDir, fileName);
            FtpDownloadTask ftpDownloadTask = new FtpDownloadTask(this.getApplicationContext(), outputFile);
            ImportFromFileTask importFromFileTask = new ImportFromFileTask(this.getApplicationContext(), outputFile);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(ftpDownloadTask);
            tasks.add(importFromFileTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityFileExportImport.this, tasks);
            AsyncTask<Void, Long, Boolean> doneImport = backgroundTaskExecutor.execute();
            doneImport.get();
            //String message = importFromFileTask.executeAndMessage();
            //displayMessage(ActivityFileExportImport.this, message, true);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityFileExportImport.this, "Tasks failed! " + e.getMessage(), true);
        } finally {
            processingInProgress = false;
        }
    }

}

