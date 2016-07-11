package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;


public abstract class AbstractEntity {

    protected int _id;

    public AbstractEntity() {

    }

    protected abstract void dbSave(DatabaseManager db);
    protected abstract void dbDelete(DatabaseManager db);

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

}
