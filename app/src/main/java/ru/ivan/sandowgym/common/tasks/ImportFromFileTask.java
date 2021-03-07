package ru.ivan.sandowgym.common.tasks;

import android.content.Context;

import java.io.File;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.FileExportImport;

public class ImportFromFileTask  {

    private Context context;
    private File file;
    private StringBuilder message;

    public ImportFromFileTask(Context context, File file) {
        this.context = context;
        this.file = file;
        this.message=new StringBuilder();
    }

    public boolean execute() {

        try {
            message.append(new FileExportImport(context, file, 0, 0).importFromFile());
        } catch (Exception e) {
            Common.saveErrorMessage(context, e.getStackTrace().toString());
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
