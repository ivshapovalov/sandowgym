package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class TrainingContent extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private int id_exercise;
    private int id_training;
    private String comment="";
    private String volume="";
    private int weight=0; //кг

    private TrainingContent(Builder builder) {

        this.id = builder.id;
        this.id_exercise=builder.id_exercise;
        this.id_training=builder.id_training;
        this.comment=builder.comment;
        this.volume=builder.volume;
        this.weight=builder.weight;
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

    public int getIdExercise() {
        return id_exercise;
    }

    public int getIdTraining() {
        return id_training;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public void dbSave(DatabaseManager db) {

        try {
            db.getTrainingContent(this.getID());
            db.updateTrainingContent((TrainingContent) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addTrainingContent((TrainingContent) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

            try {
                db.getTrainingContent(this.getID());
                db.deleteTrainingContent((TrainingContent) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }

    }

    public static class Builder extends AbstractEntity {

        private int id_exercise;
        private int id_training;
        private String comment="";
        private String volume="";
        private int weight=0; //кг

        public Builder(int id) {
            this.id = id;
        }

        public Builder(DatabaseManager DB) {
            this.id=DB.getTrainingContentMaxNumber() + 1;
        }

        public Builder addExerciseId(int idExercise) {
            this.id_exercise = idExercise;
            return this;
        }
        public Builder addTrainingId(int idTraining) {
            this.id_training = idTraining;
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
