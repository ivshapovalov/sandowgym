package ru.ivan.sandowgym.common.scheduler;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.mBackupScheduleTimeHour;
import static ru.ivan.sandowgym.common.Common.mBackupScheduleTimeMinutes;

public class Scheduler {

    public static void cancelAllWorkers(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> backup = workManager.getWorkInfosByTag("backup");
        workManager.cancelAllWorkByTag("backup");
        System.out.println(backup);
    }

    private static String getCurrentDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static void scheduleBackupTask(Context context) {
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
        backupTime.set(Calendar.HOUR_OF_DAY, mBackupScheduleTimeHour);
        //backupTime.add(Calendar.MINUTE, intervalMinutes);
        backupTime.set(Calendar.MINUTE, mBackupScheduleTimeMinutes);
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
                        .addTag("backup")
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
    }
}
