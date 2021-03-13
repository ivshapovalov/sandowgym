package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import java.io.File;

import ru.ivan.sandowgym.common.Common;

public class FtpAuthTask extends FtpTask implements BackgroundTask {

    public FtpAuthTask(Context context, File file) {
        super(context, file);
    }

    public FtpAuthTask(Context context) {
        super(context);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                return true;
            } else return false;
        } catch (Exception e) {
            Common.saveException(context, e);
            return false;
        } finally {
            disconnect();
        }
    }

    @Override
    public String getName() {
        return "FTP auth task";
    }
}

