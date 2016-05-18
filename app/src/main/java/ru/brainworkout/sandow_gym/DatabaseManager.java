package ru.brainworkout.sandow_gym;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // Exercise Table Columns names
    private static final String KEY_EXERCISE_ID = "exercise_id";
    private static final String KEY_EXERCISE_IS_ACTIVE = "exercise_is_active";
    private static final String KEY_EXERCISE_NAME = "exercise_name";
    private static final String KEY_EXERCISE_EXPLANATION = "exercise_explanation";
    private static final String KEY_EXERCISE_VOLUME_DEFAULT = "exercise_volume_default";
    private static final String KEY_EXERCISE_PICTURE_NAME = "exercise_picture_name";

    //  Training days Table Columns names
    private static final String KEY_TRAINING_ID = "training_id";
    private static final String KEY_TRAINING_DAY = "training_day";

    //  Training content Table Columns names
    private static final String KEY_TRAINING_CONTENT_ID = "training_content_id";
    private static final String KEY_TRAINING_CONTENT_ID_EXERCISE = "training_content_id_exercise";
    private static final String KEY_TRAINING_CONTENT_ID_TRAINING = "training_content_id_training";
    private static final String KEY_TRAINING_CONTENT_VOLUME = "training_volume";


    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //упражнения
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_EXERCISE_ID + " INTEGER UNIQUE PRIMARY KEY," + KEY_EXERCISE_IS_ACTIVE + " INTEGER, "
                + KEY_EXERCISE_NAME + " TEXT,"+KEY_EXERCISE_EXPLANATION + " TEXT,"
                + KEY_EXERCISE_VOLUME_DEFAULT + " TEXT,"+ KEY_EXERCISE_PICTURE_NAME + " TEXT"+")";
        db.execSQL(CREATE_EXERCISES_TABLE);

        //тренировки
        String CREATE_TRAININGS_TABLE = "CREATE TABLE " + TABLE_TRAININGS + "("
                + KEY_TRAINING_ID + " INTEGER UNIQUE PRIMARY KEY," + KEY_TRAINING_DAY + " STRING" + ")";
        db.execSQL(CREATE_TRAININGS_TABLE);

        String CREATE_TRAINING_CONTENT_TABLE = "CREATE TABLE " + TABLE_TRAINING_CONTENT + "("
                + KEY_TRAINING_CONTENT_ID + " INTEGER UNIQUE PRIMARY KEY," + KEY_TRAINING_CONTENT_ID_EXERCISE + " INTEGER,"
                + KEY_TRAINING_CONTENT_ID_TRAINING + " INTEGER," + KEY_TRAINING_CONTENT_VOLUME + " TEXT,"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_TRAINING + ") REFERENCES " + TABLE_TRAININGS + "(" + KEY_TRAINING_ID + "),"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_EXERCISE + ") REFERENCES " + TABLE_EXERCISES + "(" + KEY_EXERCISE_ID + ")"
                + ")";
        db.execSQL(CREATE_TRAINING_CONTENT_TABLE);
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
             sDate=null;
        }

        values.put(KEY_TRAINING_DAY, sDate);
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
        // Inserting Row
        db.insert(TABLE_TRAINING_CONTENT, null, values);
        db.close(); // Closing database connection
    }

    public Exercise getExercise(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXERCISES, new String[]{KEY_EXERCISE_ID,KEY_EXERCISE_IS_ACTIVE, KEY_EXERCISE_NAME,
                        KEY_EXERCISE_EXPLANATION,KEY_EXERCISE_VOLUME_DEFAULT,KEY_EXERCISE_PICTURE_NAME}, KEY_EXERCISE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Exercise exercise=null;
        try {
            exercise = new Exercise(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
        } catch (NullPointerException e) {
            exercise = new Exercise(Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
        }
        // return contact
        return exercise;
    }

    public Training getTraining(int id)  {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAININGS, new String[]{KEY_TRAINING_ID,KEY_TRAINING_DAY}, KEY_TRAINING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Training training=new Training(Integer.parseInt(cursor.getString(0)),cursor.getString(1) );

        // return contact
        return training;
    }

    public TrainingContent getTrainingContent(int id)  {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID,KEY_TRAINING_CONTENT_VOLUME,KEY_TRAINING_CONTENT_ID_EXERCISE,KEY_TRAINING_CONTENT_ID_TRAINING}, KEY_TRAINING_CONTENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        TrainingContent trainingContent=new TrainingContent(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));

        // return contact
        return trainingContent;
    }

    public TrainingContent getTrainingContent(int id,int exercise_id, int training_id)  {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID,KEY_TRAINING_CONTENT_VOLUME,KEY_TRAINING_CONTENT_ID_EXERCISE,KEY_TRAINING_CONTENT_ID_TRAINING}, KEY_TRAINING_CONTENT_ID + "=?,"+KEY_TRAINING_CONTENT_ID_EXERCISE + "=?,"+KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(id),String.valueOf(exercise_id),String.valueOf(training_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        TrainingContent trainingContent=new TrainingContent(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));

        // return contact
        return trainingContent;
    }


    public void deleteAllExercises() {

            // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES,null,null);


    }

    public void deleteAllTrainings() {

        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS,null,null);

    }

    public void deleteAllTrainingContent() {

        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT,null,null);

    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(cursor.getInt(0));
                exercise.setIsActive(cursor.getInt(1));
                exercise.setName(cursor.getString(2));
                exercise.setExplanation(cursor.getString(3));
                exercise.setVolumeDefault(cursor.getString(4));
                exercise.setPicture(cursor.getString(5));

                // Adding contact to list
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        // return contact list
        return exerciseList;
    }

    public List<Exercise> getAllActiveExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES+" WHERE "+KEY_EXERCISE_IS_ACTIVE+" = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(cursor.getInt(0));
                exercise.setIsActive(cursor.getInt(1));
                exercise.setName(cursor.getString(2));
                exercise.setExplanation(cursor.getString(3));
                exercise.setVolumeDefault(cursor.getString(4));
                exercise.setPicture(cursor.getString(5));

                // Adding contact to list
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        // return contact list
        return exerciseList;
    }

    public List<Training> getAllTrainings() {
        List<Training> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS+" ORDER BY " +KEY_TRAINING_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Training training= new Training();
                training.setID(cursor.getInt(0));
               training.setDayString(cursor.getString(1));
                // Adding contact to list
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        // return contact list
        return trainingsList;
    }

    public List<TrainingContent> getAllTrainingContentOfTraining(int training_id) {
        List<TrainingContent> trainingContentList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT +" WHERE " + KEY_TRAINING_CONTENT_ID_TRAINING +"="+training_id;

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


                // Adding contact to list
                trainingContentList.add(trainingContent);
            } while (cursor.moveToNext());
        }

        // return contact list
        return trainingContentList;
    }

    public int getExercisesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getTrainingsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRAININGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    public int getTrainingContentCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
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
        String sDate="";
        if (training.getDay()!=null) {
            sDate = dateformat.format(training.getDay());
        }
        values.put(KEY_TRAINING_DAY,sDate);

        // updating row
        return db.update(TABLE_TRAININGS, values, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(training.getID())});
    }

    public int updateTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_TRAINING_CONTENT_VOLUME,trainingContent.getVolume());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE,trainingContent.getIdExercise());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING,trainingContent.getIdTraining());

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
