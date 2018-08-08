package ru.ivan.sandowgym.database.entities;

import ru.ivan.sandowgym.database.interfaces.DeletingFromDb;
import ru.ivan.sandowgym.database.interfaces.SavingIntoDB;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;

public class TrainingContent extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private Exercise exercise;
    private Training training;
    private String comment = "";
    private int amount;
    private int weight; //кг

    private TrainingContent(Builder builder) {

        this.id = builder.id;
        this.exercise = builder.exercise;
        this.training = builder.training;
        this.comment = builder.comment;
        this.amount = builder.amount;
        this.weight = builder.weight;
    }

    public int getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

        if (db.containsTrainingContent(this.getId())) {
            db.updateTrainingContent(this);
        } else {
            db.addTrainingContent(this);
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {
        if (db.containsTrainingContent(this.getId())) {
            db.deleteTrainingContent(this);
        }
    }

    public static class Builder extends AbstractEntity {

        private Exercise exercise;
        private Training training;
        private String comment = "";
        private int amount;
        private int weight; //кг

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

        public Builder addAmount(int amount) {
            this.amount = amount;
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
