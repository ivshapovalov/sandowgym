package ru.brainworkout.sandowgym.database.interfaces;

import ru.brainworkout.sandowgym.database.manager.DatabaseManager;

public interface DeletingFromDb {

    void dbDelete(DatabaseManager db);
}