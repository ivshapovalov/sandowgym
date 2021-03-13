package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ru.ivan.sandowgym.common.Common;

public class DropboxDownloadTask implements BackgroundTask {
    Context context;
    private File file;
    private DbxClientV2 client;

    public DropboxDownloadTask(Context context, File file,
                               DbxClientV2 client
    ) {
        this.context = context;
        this.file = file;
        this.client = client;
    }

    @Override
    public String getName() {
        return "Dropbox download task";
    }

    @Override
    public boolean execute() {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());

            FileMetadata metadata = client.files().downloadBuilder("/" + file.getName())
                    .download(out);
        } catch (Exception e) {
            Common.saveException(context, e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Common.saveException(context, e);
                e.printStackTrace();
            }
        }
        return true;
    }
}
