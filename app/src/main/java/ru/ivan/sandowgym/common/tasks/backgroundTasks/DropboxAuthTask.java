package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import com.dropbox.core.v2.DbxClientV2;

public class DropboxAuthTask implements BackgroundTask {
    private DbxClientV2 client;

    public DropboxAuthTask(DbxClientV2 client) {
        this.client = client;
    }

    @Override
    public boolean execute() {
        try {
            String displayName = client.users().getCurrentAccount().getName().getDisplayName();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "Dropbox auth task";
    }

}
