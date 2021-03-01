package ru.ivan.sandowgym.database.interfaces;

import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public interface Saveble {
    void save(SQLiteDatabaseManager db);
}
