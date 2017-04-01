package ru.ivan.sandowgym.common.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
            for (Metadata file : metadatas
                    ) {
                fileNames.add(file.getName());
            }
            Collections.sort(fileNames);
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
