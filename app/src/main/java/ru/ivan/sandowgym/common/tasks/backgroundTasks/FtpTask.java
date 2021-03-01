package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.SharedPreferences;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import ru.ivan.sandowgym.activities.ActivityMain;

public abstract class FtpTask {

    protected FTPClient ftpClient;

    protected SharedPreferences settings;
    protected File file;

    protected String mFtpHost;
    protected String mFtpLogin;
    protected String mFtpPassword;

    protected FtpTask(SharedPreferences settings, File file) {
        this();
        this.settings = settings;
        this.file = file;
    }

    public FtpTask(SharedPreferences settings) {
        this();
        this.settings = settings;
    }

    public FtpTask() {
        ftpClient = new FTPClient();
    }

    public boolean connect() {
        getPreferencesFromFile();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            return true;
        } catch (Exception e) {
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
