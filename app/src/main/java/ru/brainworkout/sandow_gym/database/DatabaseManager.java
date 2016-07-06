package ru.brainworkout.sandow_gym.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.commons.Exercise;
import ru.brainworkout.sandow_gym.commons.Training;
import ru.brainworkout.sandow_gym.commons.TrainingContent;

public class DatabaseManager extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "trainingCalendar";

    // Tables names
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_TRAININGS = "trainings";
    private static final String TABLE_TRAINING_CONTENT = "training_content";

    // Exercise AbstractDatabaseEntity Columns names
    private static final String KEY_EXERCISE_ID = "exercise_id";
    private static final String KEY_EXERCISE_IS_ACTIVE = "exercise_is_active";
    private static final String KEY_EXERCISE_NAME = "exercise_name";
    private static final String KEY_EXERCISE_EXPLANATION = "exercise_explanation";
    private static final String KEY_EXERCISE_VOLUME_DEFAULT = "exercise_volume_default";
    private static final String KEY_EXERCISE_PICTURE_NAME = "exercise_picture_name";

    //  Training AbstractDatabaseEntity Columns names
    private static final String KEY_TRAINING_ID = "training_id";
    private static final String KEY_TRAINING_DAY = "training_day";
    private static final String KEY_TRAINING_WEIGHT = "training_weight";

    //  Training content AbstractDatabaseEntity Columns names
    private static final String KEY_TRAINING_CONTENT_ID = "training_content_id";
    private static final String KEY_TRAINING_CONTENT_VOLUME = "training_volume";
    private static final String KEY_TRAINING_CONTENT_ID_EXERCISE = "training_content_id_exercise";
    private static final String KEY_TRAINING_CONTENT_ID_TRAINING = "training_content_id_training";
    private static final String KEY_TRAINING_CONTENT_COMMENT = "training_content_comment";


    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //упражнения
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_EXERCISE_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL," + KEY_EXERCISE_IS_ACTIVE + " INTEGER, "
                + KEY_EXERCISE_NAME + " TEXT," + KEY_EXERCISE_EXPLANATION + " TEXT,"
                + KEY_EXERCISE_VOLUME_DEFAULT + " TEXT," + KEY_EXERCISE_PICTURE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_EXERCISES_TABLE);


        //тренировки
        String CREATE_TRAININGS_TABLE = "CREATE TABLE " + TABLE_TRAININGS + "("
                + KEY_TRAINING_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL," + KEY_TRAINING_DAY + " STRING," + KEY_TRAINING_WEIGHT + " INTEGER" + ")";
        db.execSQL(CREATE_TRAININGS_TABLE);

        String CREATE_TRAININGS_INDEX_TRAINING_DAY_ASC = "CREATE INDEX TRAINING_DAY_IDX_ASC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_DAY + " ASC)";
        db.execSQL(CREATE_TRAININGS_INDEX_TRAINING_DAY_ASC);

        String CREATE_TRAININGS_INDEX_TRAINING_DAY_DESC = "CREATE INDEX TRAINING_DAY_IDX_DESC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_DAY + " DESC)";
        db.execSQL(CREATE_TRAININGS_INDEX_TRAINING_DAY_DESC);

        String CREATE_TRAINING_CONTENT_TABLE = "CREATE TABLE " + TABLE_TRAINING_CONTENT + "("
                + KEY_TRAINING_CONTENT_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL," + KEY_TRAINING_CONTENT_VOLUME + " TEXT," +
                KEY_TRAINING_CONTENT_ID_EXERCISE + " INTEGER,"
                + KEY_TRAINING_CONTENT_ID_TRAINING + " INTEGER,"
                + KEY_TRAINING_CONTENT_COMMENT + " TEXT,"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_TRAINING + ") REFERENCES " + TABLE_TRAININGS + "(" + KEY_TRAINING_ID + "),"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_EXERCISE + ") REFERENCES " + TABLE_EXERCISES + "(" + KEY_EXERCISE_ID + ")"
                + ")";
        db.execSQL(CREATE_TRAINING_CONTENT_TABLE);

        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE = "CREATE INDEX EXERCISE_IDX ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + ")";
        db.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE);

        String CREATE_TRAINING_CONTENT_INDEX_TRAINING = "CREATE INDEX TRAINING_IDX ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_TRAINING + ")";
        db.execSQL(CREATE_TRAINING_CONTENT_INDEX_TRAINING);

        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_ASC = "CREATE INDEX EXERCISE_TRAINING_IDX_ASC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " ASC, " + KEY_TRAINING_CONTENT_ID_TRAINING + " ASC)";
        db.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_ASC);
        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_DESC = "CREATE INDEX EXERCISE_TRAINING_IDX_DESC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " DESC, " + KEY_TRAINING_CONTENT_ID_TRAINING + " DESC)";
        db.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_DESC);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_CONTENT);

        // Create tables again
        onCreate(db);
    }

    public void DeleteDB(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_CONTENT);
    }


    public void addExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_ID, exercise.getID());
        values.put(KEY_EXERCISE_IS_ACTIVE, exercise.getIsActive());
        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_EXERCISE_EXPLANATION, exercise.getExplanation());
        values.put(KEY_EXERCISE_VOLUME_DEFAULT, exercise.getVolumeDefault());
        values.put(KEY_EXERCISE_PICTURE_NAME, exercise.getPicture());

        // Inserting Row
        db.insert(TABLE_EXERCISES, null, values);
        db.close(); // Closing database connection
    }

    public void addTraining(Training training) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_ID, training.getID());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate;
        try {
            sDate = dateformat.format(training.getDay());
        } catch (Exception e) {
            sDate = null;
        }

        values.put(KEY_TRAINING_DAY, sDate);
        values.put(KEY_TRAINING_WEIGHT, training.getWeight());
        // Inserting Row
        db.insert(TABLE_TRAININGS, null, values);
        db.close(); // Closing database connection

    }

    public void addTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_CONTENT_ID, trainingContent.getID());
        values.put(KEY_TRAINING_CONTENT_VOLUME, trainingContent.getVolume());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE, trainingContent.getIdExercise());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING, trainingContent.getIdTraining());
        values.put(KEY_TRAINING_CONTENT_COMMENT, trainingContent.getComment());
        // Inserting Row
        db.insert(TABLE_TRAINING_CONTENT, null, values);
        db.close(); // Closing database connection
    }

    public Exercise getExercise(int id) throws TableDoesNotContainElementException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXERCISES, new String[]{KEY_EXERCISE_ID, KEY_EXERCISE_IS_ACTIVE, KEY_EXERCISE_NAME,
                        KEY_EXERCISE_EXPLANATION, KEY_EXERCISE_VOLUME_DEFAULT, KEY_EXERCISE_PICTURE_NAME}, KEY_EXERCISE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Exercise exercise = null;
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no Exercise with id - " + id);
        } else {
            try {
                exercise = new Exercise(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5));
            } catch (NullPointerException e) {
                exercise = new Exercise(Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5));
            }
            cursor.close();
            return exercise;
        }
    }

    public Training getTraining(int id) throws TableDoesNotContainElementException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAININGS, new String[]{KEY_TRAINING_ID, KEY_TRAINING_DAY, KEY_TRAINING_WEIGHT}, KEY_TRAINING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no Training with id - " + id);
        } else {
            Training training = new Training(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
            cursor.close();
            return training;
        }


    }

    public TrainingContent getTrainingContent(int id) throws TableDoesNotContainElementException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no TrainingContent with id - " + id);
        } else {
            TrainingContent trainingContent = new TrainingContent(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4));
            cursor.close();
            return trainingContent;
        }
    }

    public TrainingContent getTrainingContent(int id, int exercise_id, int training_id) throws TableDoesNotContainElementException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID + "=? AND " + KEY_TRAINING_CONTENT_ID_EXERCISE + "=? AND " + KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(id), String.valueOf(exercise_id), String.valueOf(training_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no TrainingContent with id - " + id);
        } else {
            TrainingContent trainingContent = new TrainingContent(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4));

            cursor.close();
            return trainingContent;
        }
    }

    public TrainingContent getTrainingContent(int exercise_id, int training_id) throws TableDoesNotContainElementException {
        SQLiteDatabase db = this.getReadableDatabase();

        TrainingContent trainingContent;
        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID_EXERCISE + "=? AND " + KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(exercise_id), String.valueOf(training_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                throw new TableDoesNotContainElementException("There is no TrainingContent with Exercise_id - " + exercise_id+ " and Training_id " + training_id);
            } else {
                trainingContent = new TrainingContent(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), cursor.getString(4));

                cursor.close();
                return trainingContent;
            }
    }


    public void deleteAllExercises() {

        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES, null, null);


    }

    public void deleteAllTrainings() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS, null, null);

    }

    public void deleteAllTrainingContent() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, null, null);

    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(cursor.getInt(0));
                exercise.setIsActive(cursor.getInt(1));
                exercise.setName(cursor.getString(2));
                exercise.setExplanation(cursor.getString(3));
                exercise.setVolumeDefault(cursor.getString(4));
                exercise.setPicture(cursor.getString(5));

                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getAllActiveExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_IS_ACTIVE + " = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(cursor.getInt(0));
                exercise.setIsActive(cursor.getInt(1));
                exercise.setName(cursor.getString(2));
                exercise.setExplanation(cursor.getString(3));
                exercise.setVolumeDefault(cursor.getString(4));
                exercise.setPicture(cursor.getString(5));


                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getExercisesByDates(String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_NAME + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_VOLUME_DEFAULT + " FROM "
                + TABLE_TRAININGS + "," + TABLE_EXERCISES + "," + TABLE_TRAINING_CONTENT
                + " WHERE " + TABLE_EXERCISES + "." + KEY_EXERCISE_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE
                + " AND " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "=" + TABLE_TRAININGS + "." + KEY_TRAINING_ID
                + " AND " + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" GROUP BY (" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + ")" + " ORDER BY " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(cursor.getInt(0));
                exercise.setName(cursor.getString(1));
                exercise.setVolumeDefault(cursor.getString(2));

                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Training> getAllTrainings() {
        List<Training> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS + " ORDER BY " + KEY_TRAINING_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Training training = new Training();
                training.setID(cursor.getInt(0));
                training.setDayString(cursor.getString(1));
                training.setWeight(cursor.getInt(2));
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Training> getTrainingsByDates(String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Training> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_WEIGHT + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" ORDER BY " + KEY_TRAINING_ID;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Training training = new Training();
                training.setID(cursor.getInt(0));
                training.setDayString(cursor.getString(1));
                training.setWeight(cursor.getInt(2));
                // Adding contact to list
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Training> getLastTrainingsByDates(String mDateTo) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Training> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_WEIGHT + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " IN (SELECT MAX(" + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ") FROM " + TABLE_TRAININGS
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\" ) ORDER BY " + KEY_TRAINING_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Training training = new Training();
                training.setID(cursor.getInt(0));
                training.setDayString(cursor.getString(1));
                training.setWeight(cursor.getInt(2));

                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<TrainingContent> getLastExerciseNotNullVolume(String mDateTo, int exercise_id) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<TrainingContent> trainingsContentList = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME
                + " FROM (SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID
                + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " FROM " + TABLE_TRAINING_CONTENT + " WHERE " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"0\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " = " + exercise_id + ") AS " + TABLE_TRAINING_CONTENT
                + " LEFT JOIN (SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " FROM "
                + TABLE_TRAININGS + " ) AS " + TABLE_TRAININGS
                + " ON " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\"" + " ORDER BY " + KEY_TRAINING_DAY + " desc limit 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(1) != 0 & cursor.getString(2) != null) {
                    TrainingContent trainingContent = new TrainingContent(cursor.getInt(1), cursor.getString(2));

                    trainingsContentList.add(trainingContent);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsContentList;
    }


    public List<TrainingContent> getAllTrainingContentOfTraining(int training_id) {
        List<TrainingContent> trainingContentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT + " WHERE " + KEY_TRAINING_CONTENT_ID_TRAINING + "=" + training_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TrainingContent trainingContent = new TrainingContent();
                trainingContent.setID(cursor.getInt(0));
                trainingContent.setVolume(cursor.getString(1));
                trainingContent.setIdExercise(cursor.getInt(2));
                trainingContent.setIdTraining(cursor.getInt(3));
                trainingContent.setComment(cursor.getString(4));

                trainingContentList.add(trainingContent);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingContentList;
    }

    public int getExercisesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int max = cursor.getCount();
        cursor.close();

        // return count
        return max;
    }

    public int getTrainingsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRAININGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int max = cursor.getCount();
        cursor.close();

        // return count
        return max;
    }


    public int getTrainingContentCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int max = cursor.getCount();
        cursor.close();

        // return count
        return max;
    }

    public int getExerciseMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_EXERCISE_ID + ") FROM " + TABLE_EXERCISES + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTrainingMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_TRAINING_ID + ") FROM " + TABLE_TRAININGS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTrainingContentMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_TRAINING_CONTENT_ID + ") FROM " + TABLE_TRAINING_CONTENT + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_IS_ACTIVE, exercise.getIsActive());
        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_EXERCISE_EXPLANATION, exercise.getExplanation());
        values.put(KEY_EXERCISE_VOLUME_DEFAULT, exercise.getVolumeDefault());
        values.put(KEY_EXERCISE_PICTURE_NAME, exercise.getPicture());

        // updating row
        return db.update(TABLE_EXERCISES, values, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getID())});
    }

    public int updateTraining(Training training) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = "";
        if (training.getDay() != null) {
            sDate = dateformat.format(training.getDay());
        }
        values.put(KEY_TRAINING_DAY, sDate);
        values.put(KEY_TRAINING_WEIGHT, training.getWeight());

        // updating row
        return db.update(TABLE_TRAININGS, values, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(training.getID())});
    }

    public int updateTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_TRAINING_CONTENT_VOLUME, trainingContent.getVolume());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE, trainingContent.getIdExercise());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING, trainingContent.getIdTraining());
        values.put(KEY_TRAINING_CONTENT_COMMENT, trainingContent.getComment());

        // updating row
        return db.update(TABLE_TRAINING_CONTENT, values, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getID())});
    }


    public void deleteExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getID())});
        db.close();
    }

    public void deleteTraining(Training training) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(training.getID())});
        db.close();
    }

    public void deleteTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getID())});
        db.close();
    }

    public void deleteTrainingContentOfTraining(int id_traning) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID_TRAINING + " = ?",
                new String[]{String.valueOf(id_traning)});
        db.close();
    }


    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (Exception sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;

        }


    }
}
