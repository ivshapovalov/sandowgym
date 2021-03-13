package ru.ivan.sandowgym.common.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;
import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.Constants;

import static ru.ivan.sandowgym.common.Constants.processingInProgress;

public class FtpListFilesTask extends AsyncTask<Void, Long, ArrayList<String>> {

    private Context context;
    private FTPClient ftpClient;

    public FtpListFilesTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(Constants.mOptionBackupFtpHost);
            ftpClient.login(Constants.mOptionBackupFtpLogin, Constants.mOptionBackupFtpPassword);
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
            Common.saveException(context, e);
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
            } catch (Exception e) {
                Common.saveException(context, e);
            }
        }
    }
}

