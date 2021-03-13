package ru.ivan.sandowgym.common.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;

import static ru.ivan.sandowgym.common.Common.displayMessage;
import static ru.ivan.sandowgym.common.Constants.processingInProgress;

public class BackgroundTaskExecutor extends AsyncTask<Void, Long, Boolean> {

    private Context context;
    private List<BackgroundTask> tasks;

    public BackgroundTaskExecutor(Context context, List<BackgroundTask> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        long num = 0;
        for (BackgroundTask task : tasks) {
            publishProgress(num, 1L);
            boolean done = task.execute();
            if (!done) {
                publishProgress(num, -1L);
                if (task.isCritical()) {
                    return false;
                }
            } else {
                publishProgress(num, 0L);
            }
            num++;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        BackgroundTask currentTask = tasks.get(values[0].intValue());
        String message = "";
        switch (values[1].intValue()) {
            case 1:
                message = currentTask.getName() + " started";
                break;
            case 0:
                message = currentTask.getName() + " finished successfully";
                break;
            case -1:
                message = currentTask.getName() + " failed";
                break;
        }
        displayMessage(context, message, false);
    }

    @Override
    protected void onPreExecute() {
        displayMessage(context, "Background tasks started", false);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        processingInProgress = false;
        displayMessage(context, "Background tasks finished", false);
    }
}
