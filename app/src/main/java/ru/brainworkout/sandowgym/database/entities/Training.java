package ru.brainworkout.sandowgym.database.entities;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Training extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private long day;

    private Training(Builder builder) {

        this.id = builder.id;
        this.day = builder.day;
    }

    public long getDay() {

        return day;
    }

    public String getDayString() {

        return ConvertMillisToString(day);

    }

    public void setDay(long day) {
        this.day = day;
    }


    public void setDayString(String _day) {

        this.day = convertStringToDate(_day).getTime();

    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {
        if (db.containsTraining(this.getId())) {
            db.updateTraining(this);
        } else {
            db.addTraining(this);
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

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
            Training training = new Training(this);
            return training;
        }
    }
}

