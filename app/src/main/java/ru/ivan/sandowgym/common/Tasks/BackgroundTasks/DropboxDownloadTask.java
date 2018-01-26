package ru.ivan.sandowgym.common.Tasks.BackgroundTasks;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DropboxDownloadTask implements BackgroundTask {
    private File file;
    private DbxClientV2 client;

    public DropboxDownloadTask(File file,
                               DbxClientV2 client
    ) {
        this.file = file;
        this.client = client;
    }

    @Override
    public boolean execute() {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());

            FileMetadata metadata = client.files().downloadBuilder("/" + file.getName())
                    .download(out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public String executeAndMessage() {
        if (execute()) {
            return String.format("File '%s' has been successfully download from Dropbox!", file.getName());
        } else {
            return String.format("An error occured while processing the download file '%s' from Dropbox", file.getName());
        }
    }
}
