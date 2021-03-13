package ru.ivan.sandowgym.common.scheduler;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.stringToCalendar;
import static ru.ivan.sandowgym.common.Constants.mOptionBackupScheduleDateTimeHour;
import static ru.ivan.sandowgym.common.Constants.mOptionBackupScheduleDateTimeMinutes;

public class Scheduler {

    public static final String TAG_BACKUP = "backup";
    public static final String TAG_BACKUP_AT = "backupAt";

    public static List<String> getActiveWorks(Context context) throws ParseException {
        List<String> backups = new ArrayList<>();
        WorkManager instance = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(Scheduler.TAG_BACKUP);
        try {
            List<WorkInfo> workInfoList = statuses.get();
            outer:
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    String time = "";
                    for (String tag : workInfo.getTags()) {
                        if (tag.startsWith(Scheduler.TAG_BACKUP_AT)) {
                            time = tag.substring(tag.indexOf(":") + 1).trim();
                            System.out.println(time);
                            Calendar workDateTime = stringToCalendar(time, "yyyy-MM-dd HH:mm:ss");
                            Calendar currentDateTime = Calendar.getInstance();
                            if (workDateTime.after(currentDateTime)) {
                                backups.add(time + ":" + state.toString());
                            } else {
                                instance.getWorkInfosByTag(tag).cancel(false);
                            }
                        }
                    }
                }
            }
            Collections.sort(backups);
            return backups;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return backups;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return backups;
        }
    }

    public static void cancelAllWorks(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> backup = workManager.getWorkInfosByTag(TAG_BACKUP);
        workManager.cancelAllWorkByTag(TAG_BACKUP);
        System.out.println(backup);
    }

    private static String getCurrentDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    //TESTS
    public static void scheduleBackupTask(Context context, int intervalMinutes) {
        // int mBackupScheduleTimeHour=14;
        //int mBackupScheduleTimeMinutes=57;
        //int intervalMinutes = 00;

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();

        Calendar currentTime = Calendar.getInstance();
        Calendar backupTime = Calendar.getInstance();
        //backupTime.set(Calendar.HOUR_OF_DAY, mBackupScheduleTimeHour);
        backupTime.add(Calendar.MINUTE, intervalMinutes);
        //backupTime.set(Calendar.MINUTE, mBackupScheduleTimeMinutes);
        //backupTime.set(Calendar.SECOND, 0);

//        if (backupTime.before(currentTime)) {
//            backupTime.add(Calendar.HOUR_OF_DAY, 24);
//        }
        long millisDiff = backupTime.getTimeInMillis() - currentTime.getTimeInMillis();

        String message = "Scheduler: next backup on " + getCurrentDateTime(backupTime.getTime());
        displayMessage(context, message, false);
//        WorkRequest backupRequest =
//                new OneTimeWorkRequest.Builder(BackupWorker.class)
//                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
//                        .setConstraints(constraints)
//                        .addTag("backup")
//                        .build();

        WorkRequest backupRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .addTag(TAG_BACKUP)
                        .addTag(TAG_BACKUP_AT + ":" + getCurrentDateTime(backupTime.getTime()))
                        .build();

        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
        ;
    }

    public static void scheduleBackupTask(Context context) {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();

        Calendar currentTime = Calendar.getInstance();
        Calendar backupTime = Calendar.getInstance();
        backupTime.set(Calendar.HOUR_OF_DAY, mOptionBackupScheduleDateTimeHour);
        backupTime.set(Calendar.MINUTE, mOptionBackupScheduleDateTimeMinutes);
        backupTime.set(Calendar.SECOND, 0);

        if (backupTime.before(currentTime)) {
            backupTime.add(Calendar.HOUR_OF_DAY, 24);
        }
        long millisDiff = backupTime.getTimeInMillis() - currentTime.getTimeInMillis();

        String message = "Scheduler: next backup on " + getCurrentDateTime(backupTime.getTime());
        displayMessage(context, message, false);
        WorkRequest backupRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .addTag(TAG_BACKUP)
                        .addTag(TAG_BACKUP_AT + ":" + getCurrentDateTime(backupTime.getTime()))
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
    }
}
