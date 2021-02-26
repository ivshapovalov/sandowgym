package ru.ivan.sandowgym.common.Tasks;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.AsyncTask;

import java.util.List;

import ru.ivan.sandowgym.common.Tasks.BackgroundTasks.BackgroundTask;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class BackgroundTaskExecutor extends AsyncTask<Void, Long, Boolean> {

    private Activity activity;
    private Class clazz;
    private NotificationManager notificationManager;
    private List<BackgroundTask> tasks;
    private String message;

    public BackgroundTaskExecutor(Activity activity, Class clazz, NotificationManager notificationManager, List<BackgroundTask> tasks) {
        this.activity = activity;
        this.clazz = clazz;
        this.notificationManager = notificationManager;
        this.tasks = tasks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        for (BackgroundTask task : tasks) {
            message = task.executeAndMessage();
            publishProgress(1L);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        //Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(context, "Background tasks begin!", Toast.LENGTH_LONG).show();
        //displayMessage(activity, clazz, "Backup", "Background task begin!");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        processingInProgress = false;
        //Toast.makeText(context, "Background tasks finished!", Toast.LENGTH_LONG).show();
        displayMessage(activity, clazz, notificationManager, "Backup", message);
    }
}
