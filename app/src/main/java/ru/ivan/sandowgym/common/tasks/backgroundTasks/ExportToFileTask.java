package ru.ivan.sandowgym.common.tasks.backgroundTasks;

import android.content.Context;

import java.io.File;

import ru.ivan.sandowgym.common.Common;
import ru.ivan.sandowgym.common.FileExportImport;

public class ExportToFileTask implements BackgroundTask {

    private boolean critical = true;
    private Context context;
    private File file;
    private long mDateFrom;
    private long mDateTo;

    @Override
    public boolean isCritical() {
        return true;
    }

    @Override
    public String getName() {
        return "Export to file task";
    }

    public ExportToFileTask(Context context,File file, long mDateFrom, long mDateTo
    ) {
        this.context=context;
        this.file = file;
        this.mDateFrom = mDateFrom;
        this.mDateTo = mDateTo;
    }

    @Override
    public boolean execute() {
        try {
            new FileExportImport(context,file,mDateFrom,mDateTo).exportToFile();
        } catch (Exception e) {
            Common.saveErrorMessage(context, e.getStackTrace().toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
