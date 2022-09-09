package ru.ivan.sandowgym.common.scheduler

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.ivan.sandowgym.common.Common
import ru.ivan.sandowgym.common.Constants
import ru.ivan.sandowgym.common.Constants.processingInProgress
import ru.ivan.sandowgym.database.entities.ScheduledTask
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager
import java.lang.Thread.sleep
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Common.displayMessage(applicationContext, "Worker '" + this.id + "' started", false)

        val database = SQLiteDatabaseManager.getInstance(applicationContext)

        var taskId = 0
        var type = ScheduledTask.Type.DAILY
        outer@ for (tag in this.tags) {
            if (tag.startsWith(Scheduler.TAG_BACKUP_ID)) {
                taskId = Integer.valueOf(tag.substring(tag.indexOf(":") + 1).trim { it <= ' ' })
//                break@outer
            } else if (tag.startsWith(Scheduler.TAG_BACKUP_DAILY)) {
                type = ScheduledTask.Type.DAILY
            } else if (tag.startsWith(Scheduler.TAG_BACKUP_MANUAL)) {
                type = ScheduledTask.Type.MANUAL
            }
        }
        Common.displayMessage(applicationContext, "Worker '" + this.id + "' try task '" + taskId + "'", false)

        val task: ScheduledTask;
        task = database.getScheduledTask(taskId)
        println("Worker '" + this.id + "' task " + taskId + " before check")
        if (processingInProgress) {
            println("Worker '" + this.id + "' task " + taskId + " in check")

            val params = hashMapOf("status" to Arrays.asList(ScheduledTask.Status.RUNNING.name))
            val alreadyRunningTasks: List<ScheduledTask> = database.getScheduledTasksByParams(params)
//            val params = hashMapOf("status" to Arrays.asList(ScheduledTask.Status.RUNNING.name),
//                    "type" to Arrays.asList(ScheduledTask.Type.DAILY.name, ScheduledTask.Type.MANUAL.name),
//                    "excluded" to Arrays.asList(taskId.toString()))
//            val alreadyRunningTasks = Scheduler.getWorks(applicationContext, params)
            val handledTasks: MutableList<ScheduledTask> = ArrayList()
            alreadyRunningTasks.stream()
                    .filter(Predicate { handlingTask: ScheduledTask -> handlingTask.id != taskId })
                    .forEach(Consumer { handlingTask: ScheduledTask ->
                        handledTasks.add(handlingTask)
                    })
            if (handledTasks.size > 0) {
                val message = "Failed. Tasks " + alreadyRunningTasks.stream()
                        .map { task -> task.getId().toString() }
                        .collect(Collectors.joining(", ", "{", "}")) + " already running";
                val output: Data = Data.Builder()
                        .putString("message", message)
                        .build()
                Common.displayMessage(applicationContext, "Worker '" + this.id + "' failed with task '" + taskId + "'. " + message, false)
                task.status = ScheduledTask.Status.FAILED
                database.updateScheduledTask(task)
                if (task.type.equals(ScheduledTask.Type.DAILY)) {
                    val actualDailyScheduledTasks = Scheduler.getActualDailyWorks(applicationContext)
                    if (Constants.mOptionBackupScheduleEnabled && actualDailyScheduledTasks.size == 0) {
                        Scheduler.scheduleNewDailyBackupTask(applicationContext)
                    }
                }

                return Result.failure(output)
            }
        } else {
            processingInProgress = true;

        }
        println("Worker '" + this.id + "' task " + taskId + " after check")

        task.datetimeFact = Calendar.getInstance().timeInMillis
        task.status = ScheduledTask.Status.RUNNING
        database.updateScheduledTask(task)

        if (type == ScheduledTask.Type.DAILY) {
            val params = hashMapOf(
                    "status" to Arrays.asList(ScheduledTask.Status.ENQUEUED.name),
                    "type" to Arrays.asList(ScheduledTask.Type.DAILY.name))
            Scheduler.cancelAllWorks(applicationContext, params, true, taskId)
            Scheduler.scheduleNewDailyBackupTask(applicationContext)
        }
        val done = doBackup()
        processingInProgress = false;
        task.isPerformed = true
        return if (done) {
            println("Worker '" + this.id + "' task " + taskId + " done true")

            task.status = ScheduledTask.Status.SUCCEEDED
            database.updateScheduledTask(task)
            Common.displayMessage(applicationContext, "Worker '" + this.id + "' have finished task '" + taskId + "' successfully", false)
            Result.success()
        } else {
            println("Worker '" + this.id + "' task " + taskId + " done false")

            task.status = ScheduledTask.Status.FAILED
            database.updateScheduledTask(task)
            Common.displayMessage(applicationContext, "Worker '" + this.id + "' failed with task '" + taskId + "'", false)
            Result.failure()
        }
    }

    private fun doBackup(): Boolean {
        return try {
            sleep(30000)
            return true;
//            val fullBackupTask = FullBackupTask(applicationContext, false)
            //          return fullBackupTask.execute()
        } catch (e: Exception) {
            Common.saveException(applicationContext, e)
            e.printStackTrace()
            false
        }
    }
}

