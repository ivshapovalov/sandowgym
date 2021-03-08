package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ru.ivan.sandowgym.common.Common;

public class DropboxAuthTask implements BackgroundTask {
    Context context;
    private DbxClientV2 client;

    public DropboxAuthTask(Context context, DbxClientV2 client) {
        this.context = context;
        this.client = client;
    }

    @Override
    public boolean execute() {
        try {
            String displayName = client.users().getCurrentAccount().getName().getDisplayName();
            return true;
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "Dropbox auth task";
    }

}
