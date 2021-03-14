package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class Log extends AbstractEntity implements Saveble, Deletable {

    private int id;
    private long datetime;
    private String text;

    private Log(Builder builder) {

        this.id = builder.id;
        this.text = builder.text;
        this.datetime = builder.datetime;

    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    @Override
    public void save(SQLiteDatabaseManager db) {
        if (db.containsLog(this.getId())) {
            db.updateLog(this);
        } else {
            db.addLog(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {
        if (db.containsLog(this.getId())) {
            db.deleteLog(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log user = (Log) o;
        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class Builder extends AbstractEntity {

        private String text;
        private long datetime;

        public Builder(int id) {
            this.id = id;
        }

        public Builder addText(String text) {
            this.text = text;
            return this;
        }

        public Builder addDatetime(long datetime) {
            this.datetime = datetime;
            return this;
        }

        public Log build() {
            return new Log(this);
        }
    }
}
