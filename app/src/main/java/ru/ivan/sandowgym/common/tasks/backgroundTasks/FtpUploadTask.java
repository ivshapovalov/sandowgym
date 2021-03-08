package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;

import ru.ivan.sandowgym.common.Common;

public class FtpUploadTask extends FtpTask implements BackgroundTask {

    public FtpUploadTask(Context context, SharedPreferences settings, File file) {
        super(context, settings, file);
    }

    @Override
    public boolean execute() {
        try {
            if (connect()) {
                ftpClient.upload(file);
                return true;
            } else return false;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
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

