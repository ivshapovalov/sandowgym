package ru.ivan.sandowgym.database.entities;

import java.util.Objects;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class ScheduledTask extends AbstractEntity implements Saveble, Deletable {

    private int id;
    private Type type;
    private long datetimePlan;
    private long datetimeFact;
    private boolean performed;
    private Status status;

    private ScheduledTask(Builder builder) {
        this.id = builder.id;
        this.datetimePlan = builder.datetimePlan;
        this.datetimeFact = builder.datetimeFact;
        this.performed = builder.performed;
        this.status = builder.status;
        this.type = builder.type;
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public long getDatetimePlan() {
        return datetimePlan;
    }

    public void setDatetimePlan(long datetimePlan) {
        this.datetimePlan = datetimePlan;
    }

    public long getDatetimeFact() {
        return datetimeFact;
    }

    public void setDatetimeFact(long datetimeFact) {
        this.datetimeFact = datetimeFact;
    }

    public boolean isPerformed() {
        return performed;
    }

    public void setPerformed(boolean performed) {
        this.performed = performed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void save(SQLiteDatabaseManager db) {
        if (db.containsScheduledTask(this.getId())) {
            db.updateScheduledTask(this);
        } else {
            db.addScheduledTask(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {
        if (db.containsScheduledTask(this.getId())) {
            db.deleteScheduledTask(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledTask that = (ScheduledTask) o;
        return id == that.id &&
                datetimePlan == that.datetimePlan &&
                datetimeFact == that.datetimeFact &&
                performed == that.performed &&
                type == that.type &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, datetimePlan, datetimeFact, performed, status);
    }

    public static class Builder extends AbstractEntity {

        private long datetimePlan;
        private long datetimeFact;
        private boolean performed;
        private Status status;
        private Type type;

        public Builder(int id) {
            this.id = id;
        }

        public Builder addDatetimePlan(long datetimePlan) {
            this.datetimePlan = datetimePlan;
            return this;
        }

        public Builder addDatetimeFact(long datetimeFact) {
            this.datetimeFact = datetimeFact;
            return this;
        }

        public Builder setPerformed(boolean performed) {
            this.performed = performed;
            return this;
        }

        public Builder addStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder addType(Type type) {
            this.type = type;
            return this;
        }

        public ScheduledTask build() {
            return new ScheduledTask(this);
        }
    }

    public enum Status {
        SUCCEEDED("SUCCEEDED"),
        RUNNING("RUNNING"),
        ENQUEUED("ENQUEUED"),
        FAILED("FAILED"),
        CANCELLED("CANCELLED"),
        OVERDUE("OVERDUE");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Type {
        MANUAL("MANUAL"),
        DAILY("DAILY");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
