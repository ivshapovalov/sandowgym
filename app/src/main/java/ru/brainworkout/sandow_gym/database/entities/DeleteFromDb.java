package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;

/**
 * Created by Ivan on 11.07.2016.
 */
public interface DeleteFromDb {

    void dbDelete(DatabaseManager db);
}
