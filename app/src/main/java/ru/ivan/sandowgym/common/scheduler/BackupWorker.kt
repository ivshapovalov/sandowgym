package ru.ivan.sandowgym.common.scheduler

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.ivan.sandowgym.common.Common
import ru.ivan.sandowgym.common.tasks.backgroundTasks.ComplexBackupTask
import java.text.SimpleDateFormat
import java.util.*

class BackupWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        var message = "Backup worker started";
        //System.out.println(message)
        Common.displayMessage(applicationContext, message, false)

        Scheduler.cancelAllWorkers(applicationContext)
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
            val complexBackupTask = ComplexBackupTask(applicationContext, false)
            return complexBackupTask.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(Date())
    }
}
