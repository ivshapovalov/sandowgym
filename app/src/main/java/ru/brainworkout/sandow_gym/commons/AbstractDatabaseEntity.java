package ru.brainworkout.sandow_gym.commons;

import ru.brainworkout.sandow_gym.database.DatabaseManager;
import ru.brainworkout.sandow_gym.database.TableDoesNotContainElementException;

public abstract class AbstractDatabaseEntity {

    protected int _id;

    public AbstractDatabaseEntity() {
    }

    public void dbSave(DatabaseManager db) {

        if (this instanceof Exercise) {

            try {
                db.getExercise(this.getID());
                db.updateExercise((Exercise) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addExercise((Exercise) this);
            }
        } else  if (this instanceof Training) {

            try {
                db.getTraining(this.getID());
                db.updateTraining((Training) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addTraining((Training) this);
            }
        } else  if (this instanceof TrainingContent) {

            try {
                db.getTrainingContent(this.getID());
                db.updateTrainingContent((TrainingContent) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
                db.addTrainingContent((TrainingContent) this);
            }
        }

    }

    public void dbDelete(DatabaseManager db) {

        if (this instanceof Exercise) {

            try {
                db.getExercise(this.getID());
                db.deleteExercise((Exercise) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }
        } else  if (this instanceof Training) {

            try {
                db.getTraining(this.getID());
                db.deleteTraining((Training) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого

            }
        } else  if (this instanceof TrainingContent) {

            try {
                db.getTrainingContent(this.getID());
                db.deleteTrainingContent((TrainingContent) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого
            }
        }

    }


    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

}
