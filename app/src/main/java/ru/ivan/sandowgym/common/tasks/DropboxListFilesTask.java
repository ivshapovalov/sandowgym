package ru.ivan.sandowgym.common.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.ivan.sandowgym.common.Common;

import static ru.ivan.sandowgym.common.Common.processingInProgress;

public class DropboxListFilesTask extends AsyncTask<Void, Long, ArrayList<String>> {
    private Context context;
    private DbxClientV2 client;

    public DropboxListFilesTask(Context context, DbxClientV2 client) {
        this.context = context;
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
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> metadatas) {
        processingInProgress = false;
    }
}
