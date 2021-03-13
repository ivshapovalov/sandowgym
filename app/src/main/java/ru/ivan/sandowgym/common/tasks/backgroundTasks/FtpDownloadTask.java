package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;

public class FtpDownloadTask extends FtpTask implements BackgroundTask {

    public FtpDownloadTask(Context context, File file) {
        super(context, file);
    }

    @Override
    public boolean execute() {

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(Constants.mOptionBackupFtpHost);
            ftpClient.login(Constants.mOptionBackupFtpLogin, Constants.mOptionBackupFtpPassword);
            ftpClient.download(file.getName(), file);
            return true;
        } catch (Exception e) {
            Common.saveException(context, e);
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

