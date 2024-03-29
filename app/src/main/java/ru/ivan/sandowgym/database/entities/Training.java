package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.convertStringToDate;

public class Training extends AbstractEntityMultiUser implements Saveble, Deletable {

    private long day;

    private Training(Builder builder) {

        this.id = builder.id;
        this.day = builder.day;
    }

    public long getDay() {

        return day;
    }

    public String getDayString() {

        return convertMillisToString(day);

    }

    public void setDay(long day) {
        this.day = day;
    }


    public void setDayString(String _day) {

        this.day = convertStringToDate(_day).getTime();

    }

    @Override
    public void save(SQLiteDatabaseManager db) {
        if (db.containsTraining(this.getId())) {
            db.updateTraining(this);
        } else {
            db.addTraining(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {

        if (db.containsTraining(this.getId())) {
            db.deleteTraining(this);
        }
    }

    public static Training getTrainingFromDB(SQLiteDatabaseManager DB, int id) {
        return DB.getTraining(id);
    }

    public static class Builder extends AbstractEntity {

        private long day;

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getTrainingMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addDay(long day) {
            this.day = day;
            return this;
        }

        public Training build() {
            return new Training(this);
        }
    }
}

