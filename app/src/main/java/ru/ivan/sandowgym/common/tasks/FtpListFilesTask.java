package ru.ivan.sandowgym.common.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import ru.ivan.sandowgym.activities.ActivityMain;
import ru.ivan.sandowgym.common.Common;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class FtpListFilesTask extends AsyncTask<Void, Long, ArrayList<String>> {

    private Context context;
    private FTPClient ftpClient;
    private SharedPreferences settings;
    private String mFtpHost;
    private String mFtpLogin;
    private String mFtpPassword;

    public FtpListFilesTask(Context context, SharedPreferences settings) {
        this.context = context;
        this.settings = settings;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        getPreferencesFromFile();
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.setType(FTPClient.TYPE_BINARY);
            FTPFile[] files = ftpClient.list();
            ArrayList<String> fileNames = new ArrayList<>();
            for (FTPFile file : files) {
                fileNames.add(file.getName());
            }
            fileNames.remove(0);
            fileNames.remove(0);
            Collections.sort(fileNames, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
                }
            });
            return fileNames;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
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

