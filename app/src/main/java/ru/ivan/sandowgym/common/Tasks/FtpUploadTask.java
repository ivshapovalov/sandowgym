package ru.ivan.sandowgym.common.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.ivan.sandowgym.activities.ActivityMain;

public class FtpUploadTask implements BackgroundTask {

    private FTPClient ftpClient;

    private SharedPreferences settings;
    private File file;

    private String mFtpHost;
    private String mFtpLogin;
    private String mFtpPassword;

    public FtpUploadTask(SharedPreferences settings, File file) {
        this.settings=settings;
        this.file = file;
    }

    @Override
    public boolean execute() {
        getPreferencesFromFile();

        ftpClient = new FTPClient();
        int reply;
        ftpClient.enterLocalPassiveMode();
        InputStream inputStream=null;
        try {
            ftpClient.connect(mFtpHost);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String firstRemoteFile = file.getName();
            inputStream = new FileInputStream(file);
            ftpClient.enterLocalPassiveMode();
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);

            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            disconnect();
        }
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

    @Override
    public String executeAndMessage() {
        if (execute()) {
            return String.format("File '%s' has been successfully uploaded to FTP!", file.getName());
        } else {
            return String.format("An error occured while processing the upload file '%s' to FTP", file.getName());
        }
    }
}

