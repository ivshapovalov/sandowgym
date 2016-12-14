package ru.brainworkout.sandowgym.database.entities;

import static ru.brainworkout.sandowgym.common.Common.*;
import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

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

        try {
            db.getWeightChangeCalendar(this.getId());
            db.updateWeightChangeCalendar((WeightChangeCalendar) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addWeightCalendarChange((WeightChangeCalendar) this);
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        try {
            db.getWeightChangeCalendar(this.getId());
            db.deleteWeightChangeCalendar((WeightChangeCalendar) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
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
