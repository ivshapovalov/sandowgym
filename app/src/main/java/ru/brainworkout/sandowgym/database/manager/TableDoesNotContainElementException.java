package ru.brainworkout.sandowgym.database.manager;

public class TableDoesNotContainElementException extends RuntimeException{

    public TableDoesNotContainElementException(String detailMessage) {
        super(detailMessage);
    }

}
