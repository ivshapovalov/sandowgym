package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import java.io.File;

import ru.ivan.sandowgym.common.Common;

public class FtpUploadTask extends FtpTask implements BackgroundTask {

    public FtpUploadTask(Context context, File file) {
        super(context, file);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                ftpClient.upload(file);
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
        return "FTP upload task";
    }

}

