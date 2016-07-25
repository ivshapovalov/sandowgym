package ru.brainworkout.sandowgym.database.entities;

import ru.brainworkout.sandowgym.database.interfaces.DeleteFromDb;
import ru.brainworkout.sandowgym.database.interfaces.SaveToDB;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;

public class TrainingContent extends AbstractEntityMultiUser implements SaveToDB,DeleteFromDb {

    private int _id_exercise;
    private int _id_training;
    private String _comment="";
    private String _volume="";
    private int _weight=0; //кг

    private TrainingContent(TrainingContentBuilder builder) {

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

    public static class TrainingContentBuilder extends AbstractEntity {

        private int _id_exercise;
        private int _id_training;
        private String _comment="";
        private String _volume="";
        private int _weight=0; //кг

        public TrainingContentBuilder(int id) {
            this._id = id;
        }

        public TrainingContentBuilder addExerciseId(int idExercise) {
            this._id_exercise = idExercise;
            return this;
        }
        public TrainingContentBuilder addTrainingId(int idTraining) {
            this._id_training = idTraining;
            return this;
        }
        public TrainingContentBuilder addComment(String comment) {
            this._comment = comment;
            return this;
        }
        public TrainingContentBuilder addVolume(String volume) {
            this._volume = volume;
            return this;
        }
        public TrainingContentBuilder addWeight(int weight) {
            this._weight = weight;
            return this;
        }

        public TrainingContent build() {
            TrainingContent trainingContent = new TrainingContent(this);
            return trainingContent;
        }



    }
}
