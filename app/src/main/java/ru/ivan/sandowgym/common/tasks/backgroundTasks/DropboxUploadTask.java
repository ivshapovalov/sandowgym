package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.ivan.sandowgym.common.Common;

public class DropboxUploadTask implements BackgroundTask {
    private Context context;
    private File file;
    private DbxClientV2 client;

    public DropboxUploadTask(Context context, File file, DbxClientV2 client) {
        this.context = context;
        this.file = file;
        this.client = client;
    }

    @Override
    public boolean execute() {
        InputStream in = null;
        try {

            FullAccount account = client.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());

            in = new FileInputStream(file.getPath());

            FileMetadata metadata = client.files().uploadBuilder("/"
                    + file.getName()).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (Exception e) {
            Common.saveException(context, e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Common.saveException(context, e);
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
