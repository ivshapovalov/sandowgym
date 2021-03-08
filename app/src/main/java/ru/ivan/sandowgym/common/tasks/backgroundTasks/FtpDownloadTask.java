package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import ru.ivan.sandowgym.common.Common;

public class FtpDownloadTask extends FtpTask implements BackgroundTask {

    public FtpDownloadTask(Context context, SharedPreferences settings, File file) {
        super(context, settings, file);
    }

    @Override
    public boolean execute() {
        getPreferencesFromFile();

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.download(file.getName(), file);
            return true;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }

    @Override
    public String getName() {
        return "FTP download task";
    }

}

