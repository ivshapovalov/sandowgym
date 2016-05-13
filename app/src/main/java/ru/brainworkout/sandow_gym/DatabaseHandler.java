package ru.brainworkout.sandow_gym;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 13.05.2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "trainingManager";

    // Contacts table name
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_CALENDAR = "calendar";

    // Contacts Table Columns names
    private static final String KEY_ID_EXERCISE = "id_ex";
    private static final String KEY_NUMBER_EXERCISE = "number_ex";
    private static final String KEY_NAME_EXERCISE = "name_ex";
    private static final String KEY_ID_CALENDAR = "id_calendar";
    private static final String KEY_ID_CALENDAR_EX = "id_ex_calendar";
    private static final String KEY_COUNT_CALENDAR = "count_calendar";
    private static final String KEY_DAY_CALENDAR = "day_calendar";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //упражнения
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_ID_EXERCISE + " INTEGER PRIMARY KEY," + KEY_NUMBER_EXERCISE + " INTEGER," + KEY_NAME_EXERCISE + " TEXT" + ")";
        db.execSQL(CREATE_EXERCISES_TABLE);

        //календарь
        String CREATE_CALENDAR_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "("
                + KEY_ID_CALENDAR + " INTEGER PRIMARY KEY," + KEY_COUNT_CALENDAR + " INTEGER,"
                + KEY_DAY_CALENDAR + " TEXT,"+ KEY_ID_CALENDAR_EX + " INTEGER SECONDARY KEY"+ ")";
        db.execSQL(CREATE_CALENDAR_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);

        // Create tables again
        onCreate(db);
    }

    public void addContact(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER_EXERCISE, exercise.getNumber());
        values.put(KEY_NAME_EXERCISE, exercise.getName());

       // Inserting Row
        db.insert(TABLE_EXERCISES, null, values);
        db.close(); // Closing database connection
    }

    public Exercise getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXERCISES, new String[] { KEY_ID_EXERCISE,KEY_NUMBER_EXERCISE,
                        KEY_NAME_EXERCISE}, KEY_ID_EXERCISE + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Exercise exercise = new Exercise(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),
                cursor.getString(2));
        // return contact
        return exercise;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exerciseList = new ArrayList<Exercise>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setID(Integer.parseInt(cursor.getString(0)));
                exercise.setNumber(Integer.parseInt(cursor.getString(1)));
                exercise.setName(cursor.getString(2));

                // Adding contact to list
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        // return contact list
        return exerciseList;
    }

    public int getExercisesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXERCISES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER_EXERCISE, exercise.getNumber());
        values.put(KEY_NAME_EXERCISE, exercise.getName());

            // updating row
        return db.update(TABLE_EXERCISES, values, KEY_ID_EXERCISE + " = ?",
                new String[] { String.valueOf(exercise.getID()) });
    }

    public void deleteExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES, KEY_ID_EXERCISE + " = ?",
                new String[] { String.valueOf(exercise.getID()) });
        db.close();
    }
}
