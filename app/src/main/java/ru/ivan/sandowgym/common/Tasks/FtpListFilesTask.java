package ru.ivan.sandowgym.common.Tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.Metadata;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.ivan.sandowgym.activities.ActivityMain;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class FtpListFilesTask extends AsyncTask<Void, Long, ArrayList<String>> {

    private FTPClient ftpClient;

    private SharedPreferences settings;

    private String mFtpHost;
    private String mFtpLogin;
    private String mFtpPassword;

    public FtpListFilesTask(SharedPreferences settings) {
        this.settings = settings;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
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
            String[] files = ftpClient.listNames();
            ArrayList<String> fileNames= new ArrayList<>(Arrays.asList(files));
            Collections.sort(fileNames);
            return fileNames;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            disconnect();
        }

    }

    @Override
    protected void onPostExecute(ArrayList<String> fileNames) {
        processingInProgress = false;
    }


    public void disconnect() {
        if (this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            } catch (IOException f) {
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

