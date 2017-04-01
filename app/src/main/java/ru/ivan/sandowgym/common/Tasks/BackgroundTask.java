package ru.ivan.sandowgym.common.Tasks;

public interface BackgroundTask {
    String executeAndMessage();

    boolean execute();

}