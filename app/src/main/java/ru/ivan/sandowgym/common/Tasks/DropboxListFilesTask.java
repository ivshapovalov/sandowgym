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
import java.util.List;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class DropboxListFilesTask extends AsyncTask<Void, Long, List<Metadata>> {
    private Context context;
    private File file;
    private DbxClientV2 client;

    public DropboxListFilesTask(DbxClientV2 client) {
        this.client = client;
    }
    @Override
    protected List<Metadata> doInBackground(Void... params) {

        try {
            return client.files().listFolder("").getEntries();
        } catch (DbxException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Metadata> metadatas) {
        processingInProgress = false;
    }
}
