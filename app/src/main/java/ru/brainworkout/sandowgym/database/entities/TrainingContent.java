package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeletingFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SavingIntoDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class TrainingContent extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private int _id_exercise;
    private int _id_training;
    private String _comment="";
    private String _volume="";
    private int _weight=0; //кг

    private TrainingContent(Builder builder) {

        this._id = builder._id;
        this._id_exercise=builder._id_exercise;
        this._id_training=builder._id_training;
        this._comment=builder._comment;
        this._volume=builder._volume;
        this._weight=builder._weight;
    }

    public String getVolume() {
        return _volume;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String _comment) {
        this._comment = _comment;
    }

    public void setVolume(String _volume) {
        this._volume = _volume;
    }

    public int getIdExercise() {
        return _id_exercise;
    }

    public int getIdTraining() {
        return _id_training;
    }


    public int getWeight() {
        return _weight;
    }

    public void setWeight(int _weight) {
        this._weight = _weight;
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

        private int _id_exercise;
        private int _id_training;
        private String _comment="";
        private String _volume="";
        private int _weight=0; //кг

        public Builder(int id) {
            this._id = id;
        }

        public Builder(DatabaseManager DB) {
            this._id=DB.getTrainingContentMaxNumber() + 1;
        }

        public Builder addExerciseId(int idExercise) {
            this._id_exercise = idExercise;
            return this;
        }
        public Builder addTrainingId(int idTraining) {
            this._id_training = idTraining;
            return this;
        }
        public Builder addComment(String comment) {
            this._comment = comment;
            return this;
        }
        public Builder addVolume(String volume) {
            this._volume = volume;
            return this;
        }
        public Builder addWeight(int weight) {
            this._weight = weight;
            return this;
        }

        public TrainingContent build() {
            TrainingContent trainingContent = new TrainingContent(this);
            return trainingContent;
        }



    }
}
