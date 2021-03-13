package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;

public abstract class FtpTask {
    Context context;

    protected FTPClient ftpClient;

    protected File file;

    protected FtpTask(Context context, File file) {
        this(context);
        this.file = file;
    }

    public FtpTask(Context context) {

        this.context = context;
        ftpClient = new FTPClient();
    }

    public boolean connect() {
        try {
            ftpClient.connect(Constants.mOptionBackupFtpHost);
            ftpClient.login(Constants.mOptionBackupFtpLogin, Constants.mOptionBackupFtpPassword);
            return true;
        } catch (Exception e) {
            Common.saveException(context, e);
            //e.printStackTrace();
            return false;
        }
    }

    public boolean connect(FTPClient ftpClient) {
        try {
            ftpClient.connect(Constants.mOptionBackupFtpHost);
            ftpClient.login(Constants.mOptionBackupFtpLogin, Constants.mOptionBackupFtpPassword);
            return true;
        } catch (Exception e) {
            Common.saveException(context, e);
            //e.printStackTrace();
            return false;
        }
    }

    protected void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect(true);
            } catch (Exception e) {
                Common.saveException(context, e);
                e.printStackTrace();
            }
        }
    }
}
