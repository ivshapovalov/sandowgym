package ru.ivan.sandowgym.common.scheduler

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.ivan.sandowgym.common.Common
import ru.ivan.sandowgym.common.tasks.backgroundTasks.FullBackupTask

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        var message = "Backup worker started";
        Common.displayMessage(applicationContext, message, false)

        Scheduler.cancelAllWorks(applicationContext)
        Scheduler.scheduleBackupTask(applicationContext)

        var done = doBackup()
        if (done) {
            return Result.success()
        } else {
            return Result.failure();
        }
    }

    private fun doBackup(): Boolean {
        try {
            val fullBackupTask = FullBackupTask(applicationContext, false)
            return fullBackupTask.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
