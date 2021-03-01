package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.SharedPreferences;

import java.io.File;

public class FtpAuthTask extends FtpTask implements BackgroundTask {

    public FtpAuthTask(SharedPreferences settings, File file) {
        super(settings, file);
    }

    public FtpAuthTask(SharedPreferences settings) {
        super(settings);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                return true;
            } else return false;
        } catch (Exception e) {
            //e.printStackTrace();
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

