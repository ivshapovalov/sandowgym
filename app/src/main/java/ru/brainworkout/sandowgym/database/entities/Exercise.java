package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Exercise extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private int is_active=1;
    private String name="";
    private String explanation="";
    private String volume_default="";
    private String picture="--";

    private Exercise(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.explanation = builder.explanation;
        this.volume_default=builder.volume_default;
        this.picture=builder.picture;

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

    public String getVolumeDefault() {
        return volume_default;
    }

    public void setVolumeDefault(String volume_default) {
        this.volume_default = volume_default;
    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {

        try {
            db.getExercise(this.getId());
            db.updateExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addExercise((Exercise) this);
        }

    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        try {
            db.getExercise(this.getId());
            db.deleteExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }

    }

    public static class Builder extends AbstractEntity {

        private int is_active=1;
        private String name="";
        private String explanation="";
        private String volume_default="";
        private String picture="--";

        public Builder(int id) {
            this.id = id;
        }

        public Builder(SQLiteDatabaseManager DB) {
            this.id=DB.getExerciseMaxNumber() + 1;
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

        public Builder addVolumeDefault(String volumeDefault) {
            this.volume_default = volumeDefault;
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

    public static Exercise getExerciseFromDB (SQLiteDatabaseManager DB, int id) {
        return DB.getExercise(id);
    }

}

