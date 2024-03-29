package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.convertStringToDate;

public class WeightChangeCalendar extends AbstractEntityMultiUser implements Saveble, Deletable {
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

        return convertMillisToString(day);
    }

    public void setDay(long day) {
        this.day = day;
    }

    public void setDayString(String day) {

        this.day = convertStringToDate(day).getTime();

    }

    @Override
    public void save(SQLiteDatabaseManager db) {

        if (db.containsWeightChangeCalendar(this.getId())) {
            db.updateWeightChangeCalendar(this);
        } else {
            db.addWeightChangeCalendar(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {

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
            return new WeightChangeCalendar(this);
        }

    }
}
