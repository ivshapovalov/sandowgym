package ru.ivan.sandowgym.common.Tasks;

import static ru.ivan.sandowgym.common.Common.processingInProgress;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import 	it.sauronsoftware.ftp4j.FTPClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import ru.ivan.sandowgym.activities.ActivityMain;

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
        try {
            ftpClient.connect(mFtpHost);
            ftpClient.login(mFtpLogin, mFtpPassword);
            ftpClient.setType(FTPClient.TYPE_BINARY);
            FTPFile[] files = ftpClient.list("*.xls");
            ArrayList<String> fileNames = new ArrayList<>();
            for (FTPFile file : files
                    ) {
                fileNames.add(file.getName());
            }
            Collections.sort(fileNames, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
                }
            });
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

