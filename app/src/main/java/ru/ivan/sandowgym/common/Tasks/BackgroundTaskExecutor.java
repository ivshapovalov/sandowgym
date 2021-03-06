package ru.ivan.sandowgym.common.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import ru.ivan.sandowgym.common.Tasks.BackgroundTasks.BackgroundTask;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class BackgroundTaskExecutor extends AsyncTask<Void, Long, Boolean> {

    private Context context;
    private List<BackgroundTask> tasks;
    private String message;

    public BackgroundTaskExecutor(Context context, List<BackgroundTask> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        for (BackgroundTask task : tasks) {
            message=task.executeAndMessage();
            publishProgress(1L);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, "Background tasks begin!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        processingInProgress = false;
        Toast.makeText(context, "Background tasks finished!", Toast.LENGTH_LONG).show();

    }
}
