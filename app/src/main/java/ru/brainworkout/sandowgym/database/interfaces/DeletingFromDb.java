package ru.brainworkout.sandowgym.database.interfaces;

import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;

public interface DeletingFromDb {
    void dbDelete(SQLiteDatabaseManager db);
}
