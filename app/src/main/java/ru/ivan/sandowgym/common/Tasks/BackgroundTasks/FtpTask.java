package ru.ivan.sandowgym.common.Tasks.BackgroundTasks;

import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import ru.ivan.sandowgym.activities.ActivityMain;

public abstract class FtpTask {

    protected FTPClient ftpClient;

    protected SharedPreferences settings;
    protected File file;

    protected String mFtpHost;
    protected String mFtpLogin;
    protected String mFtpPassword;

    protected FtpTask(SharedPreferences settings, File file) {
        this.settings=settings;
        this.file = file;
    }

    public FtpTask() {
    }

    protected void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
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
