package ru.ivan.sandowgym.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.ivan.sandowgym.R;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;
import ru.ivan.sandowgym.common.scheduler.Scheduler;
import ru.ivan.sandowgym.common.tasks.BackgroundTaskExecutor;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.DropboxUploadTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.ExportToFileTask;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FullBackupTask;
import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.entities.ScheduledTask;

import static java.lang.Thread.sleep;
import static ru.ivan.sandowgym.common.Common.blink;
import static ru.ivan.sandowgym.common.Common.checkOverdueBackups;
import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.isProcessingInProgress;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;
import static ru.ivan.sandowgym.common.Constants.processingInProgress;

public class ActivityMain extends ActivityAbstract {

    private final int maxVerticalButtonCount = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showElementsOnScreen();
        Common.updatePreferences(this);
        setTitleOfActivity(this);
        setPermissions();
        //checkOverdueBackups(this);
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

    private Date getLastDateOfWeightChange() {
        return new Date();
    }

    private void showElementsOnScreen() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / maxVerticalButtonCount;
        for (int i = 0; i <= maxVerticalButtonCount; i++) {
            int btID = getResources().getIdentifier("btMain" + i, "id", getPackageName());
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
            list = database.getAllActiveExercisesOfUser(dbCurrentUser.getId());
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

    public void rowFullBackup_onClick(View view) {
        try {
            FullBackupTask fullBackupTask = new FullBackupTask(this, true);
            fullBackupTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btTest_onClick(View view) throws ParseException {
        try {
            blink(view, this);
            if (isProcessingInProgress(this.getApplicationContext())) {
                return;
            }
            displayMessage(ActivityMain.this, "Export to Dropbox started", false);
            processingInProgress = true;

            //for tests
            File exportDir = new File(Environment.getExternalStorageDirectory()+"/Download", "");
            File outputFile = new File(exportDir, "trainings.xlsx");
            ExportToFileTask exportToFileTask = new ExportToFileTask(this.getApplicationContext(), outputFile, 0, 0);

            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox-client").build();
            DbxClientV2 client = new DbxClientV2(config, Constants.APP_PREFERENCES_BACKUP_DROPBOX_ACCESS_TOKEN);
            DropboxUploadTask dropboxUploadTask = new DropboxUploadTask(this.getApplicationContext(), outputFile, client);

            List<BackgroundTask> tasks = new ArrayList<>();
            tasks.add(exportToFileTask);
            tasks.add(dropboxUploadTask);
            BackgroundTaskExecutor backgroundTaskExecutor = new BackgroundTaskExecutor(ActivityMain.this, tasks);
            backgroundTaskExecutor.execute();
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(ActivityMain.this, "Export to Dropbox failed! " + e.getMessage(), false);
            processingInProgress = false;
        }
        System.out.println("End");
//        long time = Calendar.getInstance().getTimeInMillis();
//        database.addScheduledTask(new ScheduledTask.Builder(database.getScheduledTaskMaxNumber() + 1)
//                .addDatetimePlan(time)
//                .addDatetimeFact(time)
//                .addStatus(ScheduledTask.Status.RUNNING)
//                .setPerformed(true)
//                .build());
//        Calendar backup=Calendar.getInstance();
//        backup.add(Calendar.MINUTE,-1);
//        ScheduledTask manualTask=new ScheduledTask.Builder(database.getScheduledTaskMaxNumber()+1)
//                .addType(ScheduledTask.Type.MANUAL)
//                .addStatus(ScheduledTask.Status.ENQUEUED)
//                .addDatetimePlan(backup.getTimeInMillis())
//                .build();
//        Scheduler.scheduleNewManualBackupTask(this, manualTask);
//        try {
//            sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Scheduler.scheduleNewDailyBackupTask(this, -30);


//        checkOverdueBackups(this);
    }

    public void btTestClear_onClick(View view) {
        //Scheduler.cancelAllWorks(this);
//        if (mBackupScheduleEnabled) {
//            try {
//                List<String> backups = Scheduler.activeWorkExists(this);
//                if (backups.size()>0) {
//                    //            scheduleWork(Scheduler.TAG_BACKUP); // schedule your work
//                    String srt="SCHEDULED BACKUPS: "+System.getProperty("line.separator")+
//                            backups.stream().map(Object::toString)
//                            .collect(Collectors.joining(System.getProperty("line.separator")));
//                    // srt= "SCHEDULED BACKUPS: "+backups.toString().replaceAll("\\[|\\]|,",System.getProperty("line.separator"));
//                    displayMessage(this,srt , true);
//                } else {
//                    Scheduler.scheduleBackupTask(this);
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

    }


}
