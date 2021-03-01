package ru.ivan.sandowgym.database.interfaces;

import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public interface Deletable {
    void delete(SQLiteDatabaseManager db);
}
