package ru.brainworkout.sandow_gym.database;

/**
 * Created by Ivan on 05.07.2016.
 */
public class TableDoesNotContainElementException extends Exception{
    public TableDoesNotContainElementException(String detailMessage) {
        super(detailMessage);
    }
}
