package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.SharedPreferences;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;

public class FtpDownloadTask extends FtpTask implements BackgroundTask {

    public FtpDownloadTask(SharedPreferences settings, File file) {
        super(settings, file);
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

