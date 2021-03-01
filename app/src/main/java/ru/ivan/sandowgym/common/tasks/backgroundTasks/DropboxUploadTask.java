package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DropboxUploadTask implements BackgroundTask {
    private Context context;
    private File file;
    private DbxClientV2 client;

    public DropboxUploadTask(File file, DbxClientV2 client) {
        this.file = file;
        this.client = client;
    }

    @Override
    public boolean execute() {
        InputStream in = null;
        try {
            in = new FileInputStream(file.getPath());

            FileMetadata metadata = client.files().uploadBuilder("/"
                    + file.getName()).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "Dropbox upload task";
    }

}
