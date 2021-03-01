package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.Deletable;
import ru.ivan.sandowgym.database.interfaces.Saveble;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class Exercise extends AbstractEntityMultiUser implements Saveble, Deletable {

    private int is_active = 1;
    private String name = "";
    private String explanation = "";
    private int amount_default;
    private String picture = "--";

    private Exercise(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.explanation = builder.explanation;
        this.amount_default = builder.amount_default;
        this.picture = builder.picture;
        this.is_active=builder.is_active;

    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getIsActive() {
        return is_active;
    }

    public void setIsActive(int is_active) {
        this.is_active = is_active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getAmountDefault() {
        return amount_default;
    }

    public void setAmountDefault(int amount_default) {
        this.amount_default = amount_default;
    }

    @Override
    public void save(SQLiteDatabaseManager db) {
        if (db.containsExercise(this.getId())) {
            db.updateExercise(this);
        } else {
            db.addExercise(this);
        }
    }

    @Override
    public void delete(SQLiteDatabaseManager db) {
        if (db.containsExercise(this.getId())) {
            db.deleteExercise(this);
        }
    }

    public static class Builder extends AbstractEntity {

        private int is_active = 1;
        private String name = "";
        private String explanation = "";
        private int amount_default = 0;
        private String picture = "--";

        public Builder(int id) {
            this.id = id;
        }

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getExerciseMaxNumber() + 1;
        }

        public Builder addIsActive(int is_active) {
            this.is_active = is_active;
            return this;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Builder addAmountDefault(int amountDefault) {
            this.amount_default = amountDefault;
            return this;
        }

        public Builder addPicture(String picture) {
            this.picture = picture;
            return this;
        }

        public Exercise build() {
            Exercise exercise = new Exercise(this);
            return exercise;
        }
    }

    public static Exercise getExerciseFromDB(SQLiteDatabaseManager DB, int id) {
        return DB.getExercise(id);
    }

}

