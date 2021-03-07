package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import ru.ivan.sandowgym.common.Common;

public class FtpAuthTask extends FtpTask implements BackgroundTask {

    public FtpAuthTask(Context context, SharedPreferences settings, File file) {
        super(context, settings, file);
    }

    public FtpAuthTask(Context context, SharedPreferences settings) {
        super(context, settings);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                return true;
            } else return false;
        } catch (Exception e) {
            Common.saveErrorMessage(context, e.getStackTrace().toString());
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

