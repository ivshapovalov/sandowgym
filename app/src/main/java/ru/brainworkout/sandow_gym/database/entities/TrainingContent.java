package ru.brainworkout.sandow_gym.database.entities;

import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class TrainingContent extends AbstractEntityMultiUser {


    private int _id_exercise;
    private int _id_training;
    private String _comment="";
    private String _volume="";
    private int _weight=0; //кг

    public TrainingContent() {

    }

    public TrainingContent(int _id) {

        this._id = _id;
    }

    public TrainingContent(String _volume) {

        this._volume = _volume;
    }

    public TrainingContent(String _volume, String _comment) {

        this(_volume);
        this._comment = _comment;
    }

    public TrainingContent(String _volume, int _weight, String _comment) {

        this(_volume, _comment);
        this._weight = _weight;
    }

    public TrainingContent(int _id, String _volume) {

        this(_id);
        this._volume = _volume;
    }

    public TrainingContent(int _id, String _volume, int _weight) {

        this(_id, _volume);
        this._weight = _weight;
    }

    public TrainingContent(int _id, String _volume, String _comment) {

        this(_id, _volume);
        this._comment = _comment;
    }

    public TrainingContent(int _id, String _volume, int _weight, String _comment) {

        this(_id, _volume, _weight);
        this._comment = _comment;
    }

    public TrainingContent(int _id, int _id_exercise, int _id_training, String _volume) {
        this(_id, _volume);
        this._id_exercise = _id_exercise;
        this._id_training = _id_training;
    }

    public TrainingContent(int _id, int _id_exercise, int _id_training, String _volume, int _weight) {
        this(_id, _id_exercise, _id_training, _volume);
        this._weight = _weight;
    }

    public TrainingContent(int _id, int _id_exercise, int _id_training, String _volume, String _comment) {
        this(_id, _id_exercise, _id_training, _volume);
        this._comment = _comment;
    }

    public TrainingContent(int _id, int _id_exercise, int _id_training, String _volume, int _weight, String _comment) {
        this(_id, _id_exercise, _id_training, _volume, _weight);
        this._comment = _comment;
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

    public void setIdExercise(int _id_exercise) {
        this._id_exercise = _id_exercise;
    }

    public int getIdTraining() {
        return _id_training;
    }

    public void setIdTraining(int _id_training) {
        this._id_training = _id_training;
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
}
