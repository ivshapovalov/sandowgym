package ru.brainworkout.sandow_gym.database.interfaces;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;

public interface DeleteFromDb {

    void dbDelete(DatabaseManager db);
}
