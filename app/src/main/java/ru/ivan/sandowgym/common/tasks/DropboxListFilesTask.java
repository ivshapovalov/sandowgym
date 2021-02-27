package ru.ivan.sandowgym.common.tasks;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class DropboxListFilesTask extends AsyncTask<Void, Long, ArrayList<String>> {
    private DbxClientV2 client;

    public DropboxListFilesTask(DbxClientV2 client) {
        this.client = client;
    }
    @Override
    protected ArrayList<String> doInBackground(Void... params) {

        try {
            List<Metadata> metadatas= client.files().listFolder("").getEntries();
            ArrayList<String> fileNames = new ArrayList<>();
            for (Metadata file : metadatas) {
                fileNames.add(file.getName());
            }
            Collections.sort(fileNames, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
                }
            });
            return fileNames;
        } catch (DbxException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> metadatas) {
        processingInProgress = false;
    }
}
