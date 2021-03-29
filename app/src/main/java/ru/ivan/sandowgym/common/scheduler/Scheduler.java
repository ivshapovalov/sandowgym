package ru.ivan.sandowgym.common.scheduler;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.database.entities.ScheduledTask;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.saveException;
import static ru.ivan.sandowgym.common.Common.stringToCalendar;
import static ru.ivan.sandowgym.common.Constants.mOptionBackupScheduleDateTimeHour;
import static ru.ivan.sandowgym.common.Constants.mOptionBackupScheduleDateTimeMinutes;

public class Scheduler {

    public static final String TAG_BACKUP_DAILY = "backup_daily";
    public static final String TAG_BACKUP_MANUAL = "backup_manual";
    public static final String TAG_BACKUP_ID = "backupId";
    public static final String TAG_BACKUP_AT = "backupAt";

    public static List<String> getActiveWorks(Context context) {
        List<String> backups = new ArrayList<>();
        WorkManager instance = WorkManager.getInstance(context);
        try {
            for (WorkInfo workInfo : instance.getWorkInfosByTag(Scheduler.TAG_BACKUP_DAILY).get()) {
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
                                backups.add(time + ":" + state.toString() + " DAILY");
                            } else {
                                //instance.getWorkInfosByTag(tag).cancel(false);
                            }
                        }
                    }
                }
            }
            for (WorkInfo workInfo : instance.getWorkInfosByTag(Scheduler.TAG_BACKUP_MANUAL).get()) {
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
                                backups.add(time + ":" + state.toString() + " MANUAL");
                            } else {
                                //instance.getWorkInfosByTag(tag).cancel(false);
                            }
                        }
                    }
                }
            }
            Collections.sort(backups);
            return backups;
        } catch (Exception e) {
            e.printStackTrace();
            saveException(context, e);
            return backups;
        }
    }

    public static Map<String, String> getActiveWorkByDatetime(Context context, String searchingDatetime) throws ParseException {
        Map<String, String> backups = new HashMap<>();
        WorkManager instance = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(Scheduler.TAG_BACKUP_DAILY);
        try {
            List<WorkInfo> workInfoList = statuses.get();
            outer:
            for (WorkInfo workInfo : workInfoList) {
                String planningTaskDateTime = "";
                for (String tag : workInfo.getTags()) {
                    if (tag.startsWith(Scheduler.TAG_BACKUP_AT)) {
                        planningTaskDateTime = tag.substring(tag.indexOf(":") + 1).trim();
                        Calendar workDateTime = stringToCalendar(planningTaskDateTime, "yyyy-MM-dd HH:mm:ss");
                        backups.put(planningTaskDateTime, workInfo.getState().toString());
                    }
                }
            }
            return backups;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return backups;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return backups;
        }
    }

    public static void cancelOverdueTasks(Context context) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);
        WorkManager workManager = WorkManager.getInstance(context);

        Map<String, List<String>> params = new HashMap<>();
        params.put("status", new ArrayList(Arrays.asList(ScheduledTask.Status.ENQUEUED)));
        List<ScheduledTask> allScheduledTasksFromDb = database.getScheduledTasksByParams(params);
        allScheduledTasksFromDb
                .forEach(handlingTask -> {
                    String tag = TAG_BACKUP_ID + ":" + handlingTask.getId();
                    Calendar backupTimePlan = Calendar.getInstance();
                    backupTimePlan.setTimeInMillis(handlingTask.getDatetimePlan());
                    Calendar currentTime = Calendar.getInstance();

                    if (backupTimePlan.before(currentTime)) {
                        workManager.cancelAllWorkByTag(tag);
                        handlingTask.setStatus(ScheduledTask.Status.OVERDUE);
                        database.updateScheduledTask(handlingTask);
                    }
                });
    }

    public static List<ScheduledTask> getActiveDailyWorks(Context context) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);
        Map<String, List<String>> params = new HashMap<>();
        params.put("status", new ArrayList(Arrays.asList(ScheduledTask.Status.ENQUEUED)));
        params.put("type", new ArrayList(Arrays.asList(ScheduledTask.Type.DAILY)));
        List<ScheduledTask> allScheduledTasksFromDb = database.getScheduledTasksByParams(params);
        List<ScheduledTask> actualScheduledTasks = new ArrayList<>();
        allScheduledTasksFromDb
                .forEach(handlingTask -> {
                    Calendar backupTimePlan = Calendar.getInstance();
                    backupTimePlan.setTimeInMillis(handlingTask.getDatetimePlan());
                    Calendar currentTime = Calendar.getInstance();
                    if (backupTimePlan.after(currentTime)) {
                        actualScheduledTasks.add(handlingTask);
                    }
                });
        return actualScheduledTasks;
    }

    public static void cancelAllWorks(Context context, Map<String, List<String>> params, boolean filter, int excludeId) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);
        WorkManager workManager = WorkManager.getInstance(context);
        List<ScheduledTask> scheduledTasksFromDb = database.getScheduledTasksByParams(params);
        scheduledTasksFromDb.stream()
                .filter(handlingTask -> !filter || handlingTask.getId() != excludeId)
                .forEach(handlingTask -> {
                    String tag = TAG_BACKUP_ID + ":" + handlingTask.getId();
                    //ListenableFuture<List<WorkInfo>> backupsByTag = workManager.getWorkInfosByTag(tag);
                    workManager.cancelAllWorkByTag(tag);
                    handlingTask.setStatus(ScheduledTask.Status.CANCELLED);
                    database.updateScheduledTask(handlingTask);
                });
        //TODO For what?
        //workManager.cancelAllWorkByTag(Scheduler.TAG_BACKUP);

        ListenableFuture<List<WorkInfo>> scheduledTasksFromScheduler = workManager.getWorkInfosByTag(Scheduler.TAG_BACKUP_DAILY);
        List<WorkInfo> workInfoList = null;
        try {
            workInfoList = scheduledTasksFromScheduler.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    int taskId = 0;
                    outer:
                    for (String tag : workInfo.getTags()) {
                        if (tag.startsWith(Scheduler.TAG_BACKUP_ID)) {
                            taskId = Integer.valueOf(tag.substring(tag.indexOf(":") + 1).trim());
                            if (filter && taskId == excludeId) {
                                break;
                            }
                            boolean done = workManager.getWorkInfosByTag(tag).cancel(false);
                            if (done) {
                                try {
                                    ScheduledTask task = database.getScheduledTask(taskId);
                                    task.setStatus(ScheduledTask.Status.CANCELLED);
                                    database.updateScheduledTask(task);
                                } catch (TableDoesNotContainElementException e) {

                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveException(context, e);
        }
    }

    public static void cancelAllWorks(Context context, Map<String, List<String>> params) {
        cancelAllWorks(context, params, false, 0);
//        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);
//        WorkManager workManager = WorkManager.getInstance(context);
//
//        ArrayList handlingStatuses = new ArrayList(Arrays.asList(ScheduledTask.Status.ENQUEUED));
//        List<ScheduledTask> scheduledTasksFromDb = database.getScheduledTasksByStatus(handlingStatuses);
//        scheduledTasksFromDb.stream()
//                .forEach(handlingTask -> {
//                    String tag = TAG_BACKUP_ID + ":" + handlingTask.getId();
//                    //ListenableFuture<List<WorkInfo>> backupsByTag = workManager.getWorkInfosByTag(tag);
//                    workManager.cancelAllWorkByTag(tag);
//                    handlingTask.setStatus(ScheduledTask.Status.CANCELLED);
//                    database.updateScheduledTask(handlingTask);
//                });
//        workManager.cancelAllWorkByTag(Scheduler.TAG_BACKUP);
//
//        ListenableFuture<List<WorkInfo>> scheduledTasksFromScheduler = workManager.getWorkInfosByTag(Scheduler.TAG_BACKUP);
//        List<WorkInfo> workInfoList = null;
//        try {
//            workInfoList = scheduledTasksFromScheduler.get();
//            for (WorkInfo workInfo : workInfoList) {
//                WorkInfo.State state = workInfo.getState();
//                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
//                    int taskId = 0;
//                    outer:
//                    for (String tag : workInfo.getTags()) {
//                        if (tag.startsWith(Scheduler.TAG_BACKUP_ID)) {
//                            taskId = Integer.valueOf(tag.substring(tag.indexOf(":") + 1).trim());
//                            boolean done = workManager.getWorkInfosByTag(tag).cancel(false);
//                            if (done) {
//                                ScheduledTask task = database.getScheduledTask(taskId);
//                                task.setStatus(ScheduledTask.Status.CANCELLED);
//                                database.updateScheduledTask(task);
//                            }
//                            break outer;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            saveException(context, e);
//        }
    }

    public static void cancelWork(Context context, int interruptedTaskId) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);
        WorkManager workManager = WorkManager.getInstance(context);

        String tag = TAG_BACKUP_ID + ":" + interruptedTaskId;
        //workManager.cancelAllWorkByTag(tag);
        ScheduledTask cancelledTask;
        try {
            cancelledTask = database.getScheduledTask(interruptedTaskId);
            cancelledTask.setStatus(ScheduledTask.Status.CANCELLED);
            database.updateScheduledTask(cancelledTask);
        } catch (TableDoesNotContainElementException e) {

        }

        ListenableFuture<List<WorkInfo>> workInfosByTag = workManager.getWorkInfosByTag(tag);
        try {
            for (WorkInfo workInfo : workInfosByTag.get()) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    Operation done = workManager.cancelWorkById(workInfo.getId());
                    if (done.getResult().isDone()) {
                        try {
                            cancelledTask = database.getScheduledTask(interruptedTaskId);
                            cancelledTask.setStatus(ScheduledTask.Status.CANCELLED);
                            database.updateScheduledTask(cancelledTask);
                        } catch (TableDoesNotContainElementException e) {

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            saveException(context, e);
        }
    }

    public static void scheduleNewDailyBackupTask(Context context) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);

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
        backupTime.set(Calendar.MILLISECOND, 0);

        if (backupTime.before(currentTime)) {
            backupTime.add(Calendar.HOUR_OF_DAY, 24);
        }
        long millisDiff = backupTime.getTimeInMillis() - currentTime.getTimeInMillis();

        int newTaskId = database.addScheduledTask(new ScheduledTask.Builder(database.getScheduledTaskMaxNumber() + 1)
                .addDatetimePlan(backupTime.getTimeInMillis())
                .addStatus(ScheduledTask.Status.ENQUEUED)
                .addType(ScheduledTask.Type.DAILY)
                .setPerformed(false)
                .build());

        String message = "Scheduler: next daily backup on " + Common.getDate(backupTime.getTimeInMillis());
        displayMessage(context, message, false);
        WorkRequest backupRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .addTag(TAG_BACKUP_DAILY)
                        .addTag(TAG_BACKUP_ID + ":" + newTaskId)
                        .addTag(TAG_BACKUP_AT + ":" + Common.getDateTime(backupTime.getTime()))
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
    }

    public static void scheduleNewManualBackupTask(Context context, ScheduledTask task) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();

        Calendar currentTime = Calendar.getInstance();
        Calendar backupTime = Calendar.getInstance();
        backupTime.setTimeInMillis(task.getDatetimePlan());

        if (backupTime.before(currentTime)) {
            backupTime.add(Calendar.HOUR_OF_DAY, 24);
        }
        long millisDiff = backupTime.getTimeInMillis() - currentTime.getTimeInMillis();

        int newTaskId = database.addScheduledTask(new ScheduledTask.Builder(database.getScheduledTaskMaxNumber() + 1)
                .addDatetimePlan(backupTime.getTimeInMillis())
                .addStatus(ScheduledTask.Status.ENQUEUED)
                .addType(ScheduledTask.Type.MANUAL)
                .setPerformed(false)
                .build());

        String message = "Scheduler: next manual backup on " + Common.getDate(backupTime.getTimeInMillis());
        displayMessage(context, message, false);
        WorkRequest backupRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .addTag(TAG_BACKUP_MANUAL)
                        .addTag(TAG_BACKUP_ID + ":" + newTaskId)
                        .addTag(TAG_BACKUP_AT + ":" + Common.getDateTime(backupTime.getTime()))
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
    }

    public static void scheduleNewBackupTask(Context context, int intervalMinutes) {
        SQLiteDatabaseManager database = SQLiteDatabaseManager.getInstance(context);

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

        int newTaskId = database.addScheduledTask(new ScheduledTask.Builder(database.getScheduledTaskMaxNumber() + 1)
                .addDatetimePlan(backupTime.getTimeInMillis())
                //.addDatetimeFact(backupTime.getTimeInMillis())
                .addStatus(ScheduledTask.Status.ENQUEUED)
                .addType(ScheduledTask.Type.DAILY)
                .setPerformed(false)
                .build());

        //String message = "Scheduler: next backup on " + getCurrentDateTime(backupTime.getTime());
        //displayMessage(context, message, false);
        WorkRequest backupRequest =
                new OneTimeWorkRequest.Builder(BackupWorker.class)
                        .setInitialDelay(millisDiff, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .addTag(TAG_BACKUP_DAILY)
                        .addTag(TAG_BACKUP_ID + ":" + newTaskId)
                        .addTag(TAG_BACKUP_AT + ":" + Common.getDateTime(backupTime.getTime()))
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(backupRequest);
    }

}
