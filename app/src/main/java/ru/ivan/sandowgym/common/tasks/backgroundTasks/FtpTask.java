package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import ru.ivan.sandowgym.activities.ActivityMain;
import ru.ivan.sandowgym.common.Common;

public abstract class FtpTask {
    Context context;

    protected FTPClient ftpClient;

    protected SharedPreferences settings;
    protected File file;

    protected String mFtpHost;
    protected String mFtpLogin;
    protected String mFtpPassword;

    protected FtpTask(Context context, SharedPreferences settings, File file) {
        this(context);
        this.settings = settings;
        this.file = file;
    }

    public FtpTask(Context context, SharedPreferences settings) {
        this(context);
        this.settings = settings;
    }

    public FtpTask(Context context) {

        this.context = context;
        ftpClient = new FTPClient();
    }

    public boolean connect() {
        getPreferencesFromFile();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            return true;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
            //e.printStackTrace();
            return false;
        }
    }

    public boolean connect(FTPClient ftpClient) {
        getPreferencesFromFile();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            return true;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
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
                Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
                e.printStackTrace();
            }
        }
    }

    protected void getPreferencesFromFile() {

        if (settings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST)) {
            mFtpHost = settings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_HOST, "");
        } else {
            mFtpHost = "";
        }

        if (settings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN)) {
            mFtpLogin = settings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_LOGIN, "");
        } else {
            mFtpLogin = "";
        }

        if (settings.contains(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD)) {
            mFtpPassword = settings.getString(ActivityMain.APP_PREFERENCES_BACKUP_FTP_PASSWORD, "");
        } else {
            mFtpPassword = "";
        }
    }
}
