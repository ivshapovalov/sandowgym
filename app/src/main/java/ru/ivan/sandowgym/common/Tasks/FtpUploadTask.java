package ru.ivan.sandowgym.common.Tasks;

import android.content.SharedPreferences;

import java.io.File;
import it.sauronsoftware.ftp4j.FTPClient;

public class FtpUploadTask  extends FtpTask implements BackgroundTask  {

    public FtpUploadTask(SharedPreferences settings, File file) {
        super(settings, file);
    }

    @Override
    public boolean execute() {
        getPreferencesFromFile();
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.upload(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
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

