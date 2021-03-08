package ru.ivan.sandowgym.common.tasks;

import android.content.Context;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.FileExportImport;
import ru.ivan.sandowgym.common.tasks.backgroundTasks.BackgroundTask;

public class ImportFromFileTask implements BackgroundTask {

    private Context context;
    private File file;
    private StringBuilder message;

    @Override
    public String getName() {
        return "Import from File task";
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    public ImportFromFileTask(Context context, File file) {
        this.context = context;
        this.file = file;
        this.message = new StringBuilder();
    }

    public boolean execute() {

        try {
            message.append(new FileExportImport(context, file, 0, 0).importFromFile());
        } catch (Exception e) {
            Common.saveMessage(context, ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String executeAndMessage() {
        execute();
        return message.toString();
    }
}
