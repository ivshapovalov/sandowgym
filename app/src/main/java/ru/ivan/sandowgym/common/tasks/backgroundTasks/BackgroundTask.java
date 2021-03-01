package ru.ivan.sandowgym.common.tasks.backgroundTasks;

public interface BackgroundTask {

    boolean execute();

    String getName();

    default boolean isCritical() {
        return false;
    }
}