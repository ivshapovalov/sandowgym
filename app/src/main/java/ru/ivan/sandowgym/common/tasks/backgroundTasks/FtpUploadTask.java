package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.SharedPreferences;

import java.io.File;

public class FtpUploadTask extends FtpTask implements BackgroundTask {

    public FtpUploadTask(SharedPreferences settings, File file) {
        super(settings, file);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                ftpClient.upload(file);
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
        return "FTP upload task";
    }

}

