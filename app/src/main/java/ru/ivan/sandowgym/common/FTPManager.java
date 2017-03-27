package ru.ivan.sandowgym.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.ivan.sandowgym.activities.ActivityMain;

public class FTPManager extends AsyncTask<Void, Void, Void> {

    private FTPClient ftpClient;

    private SharedPreferences settings;
    private String localPath;
    private String fileName;

    private String mFtpHost;
    private String mFtpLogin;
    private String mFtpPassword;

    public FTPManager(SharedPreferences settings, String localPath, String fileName) {
        this.settings=settings;
        this.localPath = localPath;
        this.fileName = fileName;
    }

    @Override
    protected Void doInBackground(Void... params) {

        getPreferencesFromFile();

        ftpClient = new FTPClient();
        int reply;
        ftpClient.enterLocalPassiveMode();
        try {
            ftpClient.connect(mFtpHost);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File firstLocalFile = new File(localPath);
            String firstRemoteFile = fileName;
            InputStream inputStream = new FileInputStream(firstLocalFile);
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        disconnect();
        return null;
    }

    public void disconnect() {
        if (this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            } catch (IOException f) {
                // do nothing as file is already downloaded from FTP server
            }
        }
    }

    private void getPreferencesFromFile() {

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

