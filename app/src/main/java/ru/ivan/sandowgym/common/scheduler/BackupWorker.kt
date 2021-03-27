package ru.ivan.sandowgym.common.scheduler

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.ivan.sandowgym.common.Common
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FullBackupTask
import ru.ivan.sandowgym.database.entities.ScheduledTask
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager
import java.util.*

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Common.displayMessage(applicationContext, "Background worker started", false)

        val database = SQLiteDatabaseManager.getInstance(applicationContext)

        var taskId = 0
        outer@ for (tag in this.tags) {
            if (tag.startsWith(Scheduler.TAG_BACKUP_ID)) {
                taskId = Integer.valueOf(tag.substring(tag.indexOf(":") + 1).trim { it <= ' ' })
                break@outer
            }
        }
        val task: ScheduledTask = database.getScheduledTask(taskId)
        task.datetimeFact = Calendar.getInstance().timeInMillis
        task.status = ScheduledTask.Status.RUNNING
        database.updateScheduledTask(task)

        Scheduler.cancelAllWorks(applicationContext, true, taskId)
        Scheduler.scheduleNewDailyBackupTask(applicationContext)

        val done = doBackup()
        task.isPerformed = true
        return if (done) {
            task.status = ScheduledTask.Status.SUCCEEDED
            database.updateScheduledTask(task)
            Common.displayMessage(applicationContext, "Background worker finished successfully", false)
            Result.success()
        } else {
            task.status = ScheduledTask.Status.FAILED
            database.updateScheduledTask(task)
            Common.displayMessage(applicationContext, "Background worker failed", false)
            Result.failure()
        }
    }

    private fun doBackup(): Boolean {
        return try {
            val fullBackupTask = FullBackupTask(applicationContext, false)
            return fullBackupTask.execute()
//            Common.displayMessage(applicationContext, "TASK EXECUTED", false)
//            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
