package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeleteFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SaveToDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class Exercise extends AbstractEntityMultiUser implements SaveToDB,DeleteFromDb {

    private int _is_active=1;
    private String _name="";
    private String _explanation="";
    private String _volume_default="";
    private String _picture="--";

    private Exercise(ExerciseBuilder builder) {

        this._id = builder._id;
        this._name = builder._name;
        this._explanation = builder._explanation;
        this._volume_default=builder._volume_default;
        this._picture=builder._picture;

    }

    public String getPicture() {
        return _picture;
    }

    public void setPicture(String _picture) {
        this._picture = _picture;
    }


    public int getIsActive() {
        return _is_active;
    }

    public void setIsActive(int _is_active) {
        this._is_active = _is_active;
    }


    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getExplanation() {
        return this._explanation;
    }

    public void setExplanation(String explanation) {
        this._explanation = explanation;
    }

    public String getVolumeDefault() {
        return _volume_default;
    }

    public void setVolumeDefault(String _volume_default) {
        this._volume_default = _volume_default;
    }

    @Override
    public void dbSave(DatabaseManager db) {

        try {
            db.getExercise(this.getID());
            db.updateExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addExercise((Exercise) this);
        }

    }

    @Override
    public void dbDelete(DatabaseManager db) {


        try {
            db.getExercise(this.getID());
            db.deleteExercise((Exercise) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }

    }

    public static class ExerciseBuilder extends AbstractEntity {

        private int _is_active=1;
        private String _name="";
        private String _explanation="";
        private String _volume_default="";
        private String _picture="--";

        public ExerciseBuilder(int id) {
            this._id = id;
        }


        public ExerciseBuilder addIsActive(int is_active) {
            this._is_active = is_active;
            return this;
        }

        public ExerciseBuilder addName(String name) {
            this._name = name;
            return this;
        }

        public ExerciseBuilder addExplanation(String explanation) {
            this._explanation = explanation;
            return this;
        }

        public ExerciseBuilder addVolumeDefault(String volumeDefault) {
            this._volume_default = volumeDefault;
            return this;
        }

        public ExerciseBuilder addPicture(String picture) {
            this._picture = picture;
            return this;
        }

        public Exercise build() {
            Exercise exercise = new Exercise(this);
            return exercise;
        }



    }


}

