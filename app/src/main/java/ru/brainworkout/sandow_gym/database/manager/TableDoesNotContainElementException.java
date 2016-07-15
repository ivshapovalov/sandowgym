package ru.brainworkout.sandow_gym.database.manager;

public class TableDoesNotContainElementException extends RuntimeException{

    public TableDoesNotContainElementException(String detailMessage) {
        super(detailMessage);
    }

}
