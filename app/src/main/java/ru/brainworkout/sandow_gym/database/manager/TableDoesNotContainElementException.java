package ru.brainworkout.sandow_gym.database.manager;

public class TableDoesNotContainElementException extends Exception{

    public TableDoesNotContainElementException(String detailMessage) {
        super(detailMessage);
    }

}
