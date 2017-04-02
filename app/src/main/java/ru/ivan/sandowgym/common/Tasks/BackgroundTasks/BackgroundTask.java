package ru.ivan.sandowgym.common.Tasks.BackgroundTasks;

public interface BackgroundTask {

    String executeAndMessage();

    boolean execute();

}