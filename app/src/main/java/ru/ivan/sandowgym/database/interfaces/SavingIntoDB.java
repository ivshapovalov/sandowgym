package ru.ivan.sandowgym.database.interfaces;

import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public interface SavingIntoDB {
    void dbSave(SQLiteDatabaseManager db);
}
