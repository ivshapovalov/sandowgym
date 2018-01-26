package ru.ivan.sandowgym.database.entities;

import static ru.ivan.sandowgym.common.Common.*;
import ru.ivan.sandowgym.database.interfaces.DeletingFromDb;
import ru.ivan.sandowgym.database.interfaces.SavingIntoDB;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class WeightChangeCalendar extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {
    private long day;
    private int weight;

    private WeightChangeCalendar(Builder weightChangeCalendarBuilder) {
        this.id=weightChangeCalendarBuilder.id;
        this.day = weightChangeCalendarBuilder.day;
        this.weight = weightChangeCalendarBuilder.weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public void setDayString(String day) {

        this.day = convertStringToDate(day).getTime();

    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {

        if (db.containsWeightChangeCalendar(this.getId())) {
            db.updateWeightChangeCalendar(this);
        } else {
            db.addWeightChangeCalendar(this);
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        if (db.containsWeightChangeCalendar(this.getId())) {
            db.deleteWeightChangeCalendar(this);
        }
    }

    public static class Builder extends AbstractEntity {

        private long day;
        private int weight;

        public Builder(int id) {
            this.id = id;
        }

        public Builder addDay(long day) {
            this.day = day;
            return this;
        }
        public Builder addDayString(String day) {
            this.day = convertStringToDate(day).getTime();
            return this;
        }

        public Builder addWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public WeightChangeCalendar build() {
            WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar(this);
            return weightChangeCalendar;
        }

    }
}