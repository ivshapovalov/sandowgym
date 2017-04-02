package ru.ivan.sandowgym.common.Tasks.BackgroundTasks;

import android.content.Context;

import java.io.File;

import ru.ivan.sandowgym.common.FileExportImport;

public class ExportToFileTask implements BackgroundTask {

    private Context context;
    private File file;
    private long mDateFrom;
    private long mDateTo;

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
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String executeAndMessage() {
        if (execute()) {
            return String.format("Trainings has been successfully export to file '%s'!", file.getName());
        } else {
            return String.format("An error occured while processing the export to file '%s'!", file.getName());
        }
    }
}
