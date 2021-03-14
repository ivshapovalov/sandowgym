package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class User extends AbstractEntity implements Saveble, Deletable {

    private int id;
    private String name;
    private int isCurrentUser;

    private User(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.isCurrentUser = builder.isCurrentUser;

    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isCurrentUser() {
        return isCurrentUser;
    }

    public void setIsCurrentUser(int isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    @Override
    public void save(SQLiteDatabaseManager db) {
        if (db.containsUser(this.getId())) {
            db.updateUser(this);
        } else {
            db.addUser(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {
        if (db.containsUser(this.getId())) {
            db.deleteUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class Builder extends AbstractEntity {

        private String name;
        private int isCurrentUser;

        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addIsCurrentUser(int isCurrentUser) {
            this.isCurrentUser = isCurrentUser;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
