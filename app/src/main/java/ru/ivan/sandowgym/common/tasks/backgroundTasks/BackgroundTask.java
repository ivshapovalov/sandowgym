package ru.ivan.sandowgym.common.tasks.backgroundTasks;

public interface BackgroundTask {

    String executeAndMessage();

    boolean execute();

    String getName();

    default boolean isCritical() {
        return false;
    }
}