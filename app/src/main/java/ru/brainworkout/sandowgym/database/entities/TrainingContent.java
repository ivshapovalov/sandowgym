package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class TrainingContent extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private Exercise exercise;
    private Training training;
    private String comment = "";
    private String volume = "";
    private int weight = 0; //кг

    private TrainingContent(Builder builder) {

        this.id = builder.id;
        this.exercise = builder.exercise;
        this.training = builder.training;
        this.comment = builder.comment;
        this.volume = builder.volume;
        this.weight = builder.weight;
    }

    public String getVolume() {
        return volume;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Exercise getExercise() {
        return exercise;
    }
    public int getExerciseId() {
        if (exercise != null) {
            return exercise.getId();
        } else {
            return -1;
        }
    }

    public Training getTraining() {
        return training;
    }

    public int getTrainingId() {
        if (training != null) {
            return training.getId();
        } else {
            return -1;
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {

        try {
            db.getTrainingContent(this.getId());
            db.updateTrainingContent((TrainingContent) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addTrainingContent((TrainingContent) this);
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        try {
            db.getTrainingContent(this.getId());
            db.deleteTrainingContent((TrainingContent) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }
    }

    public static class Builder extends AbstractEntity {

        private Exercise exercise;
        private Training training;
        private String comment = "";
        private String volume = "";
        private int weight = 0; //кг

        public Builder(int id) {
            this.id = id;
        }

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getTrainingContentMaxNumber() + 1;
        }

        public Builder addExercise(Exercise exercise) {
            this.exercise = exercise;
            return this;
        }

        public Builder addTraining(Training training) {
            this.training = training;
            return this;
        }

        public Builder addComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder addVolume(String volume) {
            this.volume = volume;
            return this;
        }

        public Builder addWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public TrainingContent build() {
            TrainingContent trainingContent = new TrainingContent(this);
            return trainingContent;
        }


    }
}
