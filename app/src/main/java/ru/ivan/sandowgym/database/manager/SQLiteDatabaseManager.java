package ru.ivan.sandowgym.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.entities.Log;
import ru.ivan.sandowgym.database.entities.Training;
import ru.ivan.sandowgym.database.entities.TrainingContent;
import ru.ivan.sandowgym.database.entities.User;
import ru.ivan.sandowgym.database.entities.WeightChangeCalendar;


public class SQLiteDatabaseManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "trainingCalendar";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_TRAININGS = "trainings";
    private static final String TABLE_TRAINING_CONTENT = "training_content";
    private static final String TABLE_WEIGHT_CHANGE_CALENDAR = "weight_change_calendar";
    private static final String TABLE_LOGS = "logs";

    private static final String KEY_EXERCISE_ID = "exercise_id";
    private static final String KEY_EXERCISE_ID_USER = "exercise_id_user";
    private static final String KEY_EXERCISE_IS_ACTIVE = "exercise_is_active";
    private static final String KEY_EXERCISE_NAME = "exercise_name";
    private static final String KEY_EXERCISE_EXPLANATION = "exercise_explanation";
    private static final String KEY_EXERCISE_AMOUNT_DEFAULT = "exercise_amount_default";
    private static final String KEY_EXERCISE_PICTURE_NAME = "exercise_picture_name";

    //  Training
    private static final String KEY_TRAINING_ID = "training_id";
    private static final String KEY_TRAINING_ID_USER = "training_id_user";
    private static final String KEY_TRAINING_DAY = "training_day";

    //  Training content
    private static final String KEY_TRAINING_CONTENT_ID = "training_content_id";
    private static final String KEY_TRAINING_CONTENT_ID_USER = "training_content_id_user";
    private static final String KEY_TRAINING_CONTENT_ID_EXERCISE = "training_content_id_exercise";
    private static final String KEY_TRAINING_CONTENT_ID_TRAINING = "training_content_id_training";
    private static final String KEY_TRAINING_CONTENT_AMOUNT = "training_content_amount";
    private static final String KEY_TRAINING_CONTENT_WEIGHT = "training_content_weight";
    private static final String KEY_TRAINING_CONTENT_COMMENT = "training_content_comment";

    //  WeightChangeCalendar
    private static final String KEY_WEIGHT_CHANGE_CALENDAR_ID = "weight_change_calendar_id";
    private static final String KEY_WEIGHT_CHANGE_CALENDAR_ID_USER = "weight_change_calendar_id_user";
    private static final String KEY_WEIGHT_CHANGE_CALENDAR_DAY = "weight_change_calendar_day";
    private static final String KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT = "weight_change_calendar_weight";

    //  Users
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_IS_CURRENT = "user_is_current";

    //logs
    private static final String KEY_LOG_ID = "log_id";
    private static final String KEY_LOG_DATETIME = "log_datetime";
    private static final String KEY_LOG_TEXT = "log_text";

    private static SQLiteDatabaseManager instance;
    private SQLiteDatabase mDatabase;
    private int mOpenCounter;

    public static synchronized SQLiteDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteDatabaseManager(context);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = this.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            // Closing database
            closeDatabase();
        }
    }


    public SQLiteDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {

        //users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_USER_NAME + " TEXT," + KEY_USER_IS_CURRENT + " INTEGER)";
        mDatabase.execSQL(CREATE_USERS_TABLE);

        //weight change calendar
        String CREATE_WEIGHT_CHANGE_CALENDAR_TABLE = "CREATE TABLE " + TABLE_WEIGHT_CHANGE_CALENDAR + "("
                + KEY_WEIGHT_CHANGE_CALENDAR_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + " INTEGER, "
                + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " INTEGER," + KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT + " INTEGER,"
                + " FOREIGN KEY(" + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + " ON DELETE CASCADE ON UPDATE CASCADE )";
        mDatabase.execSQL(CREATE_WEIGHT_CHANGE_CALENDAR_TABLE);

        String CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_DAY_ASC = "CREATE INDEX WEIGHT_CHANGE_CALENDAR_DAY_IDX_ASC ON " + TABLE_WEIGHT_CHANGE_CALENDAR + " (" + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " ASC)";
        mDatabase.execSQL(CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_DAY_ASC);
        String CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_DAY_DESC = "CREATE INDEX WEIGHT_CHANGE_CALENDAR_DAY_IDX_DESC ON " + TABLE_WEIGHT_CHANGE_CALENDAR + " (" + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " DESC)";
        mDatabase.execSQL(CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_DAY_DESC);

        String CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_USER_ASC = "CREATE INDEX WEIGHT_CHANGE_CALENDAR_USER_IDX_ASC ON " + TABLE_WEIGHT_CHANGE_CALENDAR + " (" + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + " ASC)";
        mDatabase.execSQL(CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_USER_ASC);
        String CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_USER_DESC = "CREATE INDEX WEIGHT_CHANGE_CALENDAR_USER_IDX_DESC ON " + TABLE_WEIGHT_CHANGE_CALENDAR + " (" + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + " DESC)";
        mDatabase.execSQL(CREATE_WEIGHT_CHANGE_CALENDAR_INDEX_USER_DESC);

        //exercises
        String CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_EXERCISE_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_EXERCISE_ID_USER + " INTEGER, "
                + KEY_EXERCISE_IS_ACTIVE + " INTEGER, "
                + KEY_EXERCISE_NAME + " TEXT," + KEY_EXERCISE_EXPLANATION + " TEXT,"
                + KEY_EXERCISE_AMOUNT_DEFAULT + " INTEGER," + KEY_EXERCISE_PICTURE_NAME + " TEXT, "
                + " FOREIGN KEY(" + KEY_EXERCISE_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" +
                " ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        mDatabase.execSQL(CREATE_EXERCISES_TABLE);

        String CREATE_EXERCISES_INDEX_USER_ASC = "CREATE INDEX EXERCISES_USER_IDX_ASC ON " + TABLE_EXERCISES + " (" + KEY_EXERCISE_ID_USER + " ASC)";
        mDatabase.execSQL(CREATE_EXERCISES_INDEX_USER_ASC);
        String CREATE_EXERCISES_INDEX_USER_DESC = "CREATE INDEX EXERCISES_USER_IDX_DESC ON " + TABLE_EXERCISES + " (" + KEY_EXERCISE_ID_USER + " DESC)";
        mDatabase.execSQL(CREATE_EXERCISES_INDEX_USER_DESC);

        //trainings
        String CREATE_TRAININGS_TABLE = "CREATE TABLE " + TABLE_TRAININGS + "("
                + KEY_TRAINING_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_TRAINING_ID_USER + " INTEGER, "
                + KEY_TRAINING_DAY + " INTEGER,"
                + "FOREIGN KEY(" + KEY_TRAINING_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") " +
                " ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        mDatabase.execSQL(CREATE_TRAININGS_TABLE);

        String CREATE_TRAININGS_INDEX_USER_ASC = "CREATE INDEX TRAININGS_USER_IDX_ASC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_ID_USER + " ASC)";
        mDatabase.execSQL(CREATE_TRAININGS_INDEX_USER_ASC);
        String CREATE_TRAININGS_INDEX_USER_DESC = "CREATE INDEX TRAININGS_USER_IDX_DESC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_ID_USER + " DESC)";
        mDatabase.execSQL(CREATE_TRAININGS_INDEX_USER_DESC);

        String CREATE_TRAININGS_INDEX_TRAINING_DAY_ASC = "CREATE INDEX TRAINING_DAY_IDX_ASC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_DAY + " ASC)";
        mDatabase.execSQL(CREATE_TRAININGS_INDEX_TRAINING_DAY_ASC);

        String CREATE_TRAININGS_INDEX_TRAINING_DAY_DESC = "CREATE INDEX TRAINING_DAY_IDX_DESC ON " + TABLE_TRAININGS + " (" + KEY_TRAINING_DAY + " DESC)";
        mDatabase.execSQL(CREATE_TRAININGS_INDEX_TRAINING_DAY_DESC);

        String CREATE_TRAINING_CONTENT_TABLE = "CREATE TABLE " + TABLE_TRAINING_CONTENT + "("
                + KEY_TRAINING_CONTENT_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_TRAINING_CONTENT_ID_USER + " INTEGER, "
                + KEY_TRAINING_CONTENT_ID_EXERCISE + " INTEGER,"
                + KEY_TRAINING_CONTENT_ID_TRAINING + " INTEGER,"
                + KEY_TRAINING_CONTENT_AMOUNT + " INTEGER,"
                + KEY_TRAINING_CONTENT_WEIGHT + " INTEGER, "
                + KEY_TRAINING_CONTENT_COMMENT + " TEXT,"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_TRAINING + ") REFERENCES " + TABLE_TRAININGS
                + "(" + KEY_TRAINING_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_EXERCISE + ") REFERENCES "
                + TABLE_EXERCISES + "(" + KEY_EXERCISE_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + KEY_TRAINING_CONTENT_ID_USER + ") REFERENCES " +
                TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_TABLE);

        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_ASC = "CREATE INDEX EXERCISE_IDX_ASC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " ASC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_ASC);

        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_DESC = "CREATE INDEX EXERCISE_IDX_DESC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " DESC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_DESC);

        String CREATE_TRAINING_CONTENT_INDEX_TRAINING_ASC = "CREATE INDEX TRAINING_IDX_ASC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_TRAINING + " ASC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_TRAINING_ASC);

        String CREATE_TRAINING_CONTENT_INDEX_TRAINING_DESC = "CREATE INDEX TRAINING_IDX_DESC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_TRAINING + " DESC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_TRAINING_DESC);

        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_ASC = "CREATE INDEX EXERCISE_TRAINING_IDX_ASC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " ASC, " + KEY_TRAINING_CONTENT_ID_TRAINING + " ASC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_ASC);
        String CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_DESC = "CREATE INDEX EXERCISE_TRAINING_IDX_DESC ON " + TABLE_TRAINING_CONTENT + " (" + KEY_TRAINING_CONTENT_ID_EXERCISE + " DESC, " + KEY_TRAINING_CONTENT_ID_TRAINING + " DESC)";
        mDatabase.execSQL(CREATE_TRAINING_CONTENT_INDEX_EXERCISE_AND_TRAINING_DESC);

        //logs
        String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_LOGS + "("
                + KEY_LOG_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT  NOT NULL,"
                + KEY_LOG_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + KEY_LOG_TEXT + " TEXT)";
        mDatabase.execSQL(CREATE_LOGS_TABLE);

        String CREATE_LOGS_INDEX_DATETIME_ASC = "CREATE INDEX LOGS_DATETIME_IDX_ASC ON " + TABLE_LOGS + " (" + KEY_LOG_DATETIME + " ASC)";
        mDatabase.execSQL(CREATE_LOGS_INDEX_DATETIME_ASC);
        String CREATE_LOGS_INDEX_DATETIME_DESC = "CREATE INDEX LOGS_DATETIME_IDX_DEC ON " + TABLE_LOGS + " (" + KEY_LOG_DATETIME + " DESC)";
        mDatabase.execSQL(CREATE_LOGS_INDEX_DATETIME_DESC);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            //logs
//            String DROP_LOGS_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOGS;
//            mDatabase.execSQL(DROP_LOGS_TABLE);
//
//            String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_LOGS + "("
//                    + KEY_LOG_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT  NOT NULL,"
//                    + KEY_LOG_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + KEY_LOG_TEXT + " TEXT)";
//            mDatabase.execSQL(CREATE_LOGS_TABLE);
//
//            String CREATE_LOGS_INDEX_DATETIME_ASC = "CREATE INDEX LOGS_DATETIME_IDX_ASC ON " + TABLE_LOGS + " (" + KEY_LOG_DATETIME + " ASC)";
//            mDatabase.execSQL(CREATE_LOGS_INDEX_DATETIME_ASC);
//            String CREATE_LOGS_INDEX_DATETIME_DESC = "CREATE INDEX LOGS_DATETIME_IDX_DEC ON " + TABLE_LOGS + " (" + KEY_LOG_DATETIME + " DESC)";
//            mDatabase.execSQL(CREATE_LOGS_INDEX_DATETIME_DESC);
        }
    }

    public synchronized void clearDB(SQLiteDatabase db) {
        mDatabase.execSQL("Delete from " + TABLE_WEIGHT_CHANGE_CALENDAR);
        mDatabase.execSQL("Delete from " + TABLE_TRAINING_CONTENT);
        mDatabase.execSQL("Delete from " + TABLE_EXERCISES);
        mDatabase.execSQL("Delete from " + TABLE_TRAININGS);
        mDatabase.execSQL("Delete from  " + TABLE_USERS);
        mDatabase.execSQL("Delete from  " + TABLE_LOGS);
    }

    public synchronized void dropDB(SQLiteDatabase db) {

        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_CONTENT);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT_CHANGE_CALENDAR);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mDatabase.execSQL("PRAGMA foreign_keys=ON");
    }

    public synchronized void addUser(User user) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());

        mDatabase.insert(TABLE_USERS, null, values);
        closeDatabase();
    }

    public synchronized void addWeightChangeCalendar(WeightChangeCalendar weightChangeCalendar) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_ID, weightChangeCalendar.getId());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_ID_USER, weightChangeCalendar.getUserId());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_DAY, weightChangeCalendar.getDay());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT, weightChangeCalendar.getWeight());

        mDatabase.insert(TABLE_WEIGHT_CHANGE_CALENDAR, null, values);
        closeDatabase();
    }


    public synchronized void addExercise(Exercise exercise) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_ID, exercise.getId());
        values.put(KEY_EXERCISE_ID_USER, exercise.getUserId());
        values.put(KEY_EXERCISE_IS_ACTIVE, exercise.getIsActive());
        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_EXERCISE_EXPLANATION, exercise.getExplanation());
        values.put(KEY_EXERCISE_AMOUNT_DEFAULT, exercise.getAmountDefault());
        values.put(KEY_EXERCISE_PICTURE_NAME, exercise.getPicture());

        mDatabase.insert(TABLE_EXERCISES, null, values);
        closeDatabase();
    }

    public synchronized void addTraining(Training training) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_ID, training.getId());
        values.put(KEY_TRAINING_ID_USER, training.getUserId());
        values.put(KEY_TRAINING_DAY, training.getDay());
        mDatabase.insert(TABLE_TRAININGS, null, values);
        closeDatabase();
    }

    public synchronized void addTrainingContent(TrainingContent trainingContent) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_CONTENT_ID, trainingContent.getId());
        values.put(KEY_TRAINING_CONTENT_ID_USER, trainingContent.getUserId());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE, trainingContent.getExerciseId());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING, trainingContent.getTrainingId());
        values.put(KEY_TRAINING_CONTENT_AMOUNT, trainingContent.getAmount());
        values.put(KEY_TRAINING_CONTENT_WEIGHT, trainingContent.getWeight());
        values.put(KEY_TRAINING_CONTENT_COMMENT, trainingContent.getComment());

        mDatabase.insert(TABLE_TRAINING_CONTENT, null, values);
        closeDatabase();
    }

    public synchronized void addLog(Log log) {
        openDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_LOG_ID, log.getId());
        values.put(KEY_LOG_DATETIME, log.getDatetime());
        values.put(KEY_LOG_TEXT, log.getText());

        mDatabase.insert(TABLE_LOGS, null, values);
        closeDatabase();
    }

    public synchronized boolean containsEntity(int id, String tableName, String keyColumn) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(tableName, new String[]{keyColumn}, keyColumn + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            closeDatabase();
            return false;
        } else {
            cursor.close();
            closeDatabase();
            return true;
        }
    }

    public synchronized boolean containsUser(int id) {
        return containsEntity(id, TABLE_USERS, KEY_USER_ID);
    }

    public synchronized User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(TABLE_USERS, new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_IS_CURRENT}, KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        User user = null;
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException("There is no User with id - " + id);
        } else {
            user = new User.Builder(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)))
                    .addName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)))
                    .addIsCurrentUser(cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_CURRENT)))
                    .build();
            cursor.close();
            closeDatabase();
            return user;
        }
    }

    public synchronized boolean containsWeightChangeCalendar(int id) {
        return containsEntity(id, TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID);
    }

    public synchronized WeightChangeCalendar getWeightChangeCalendar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(TABLE_WEIGHT_CHANGE_CALENDAR, new String[]{KEY_WEIGHT_CHANGE_CALENDAR_ID,
                        KEY_WEIGHT_CHANGE_CALENDAR_ID_USER, KEY_WEIGHT_CHANGE_CALENDAR_DAY, KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT},
                KEY_WEIGHT_CHANGE_CALENDAR_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        WeightChangeCalendar weightChangeCalendar = null;
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException("There is no Weight change calendat with id - " + id);
        } else {
            weightChangeCalendar = new WeightChangeCalendar.Builder(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_ID)))
                    .addDay(cursor.getLong(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_DAY)))
                    .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT)))
                    .build();

            cursor.close();
            closeDatabase();
            return weightChangeCalendar;
        }
    }

    public synchronized boolean containsExercise(int id) {
        return containsEntity(id, TABLE_EXERCISES, KEY_EXERCISE_ID);
    }

    public synchronized Exercise getExercise(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = mDatabase.query(TABLE_EXERCISES, new String[]{KEY_EXERCISE_ID, KEY_EXERCISE_ID_USER, KEY_EXERCISE_IS_ACTIVE, KEY_EXERCISE_NAME,
                        KEY_EXERCISE_EXPLANATION, KEY_EXERCISE_AMOUNT_DEFAULT, KEY_EXERCISE_PICTURE_NAME}, KEY_EXERCISE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Exercise exercise = null;
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException("There is no Exercise with id - " + id);
        } else {
            exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_ID)))
                    .addIsActive(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_IS_ACTIVE)))
                    .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                    .addExplanation(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_EXPLANATION)))
                    .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                    .addPicture(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_PICTURE_NAME)))
                    .build();

            cursor.close();
            closeDatabase();
            return exercise;
        }
    }

    public synchronized boolean containsTraining(int id) {
        return containsEntity(id, TABLE_TRAININGS, KEY_TRAINING_ID);
    }

    public synchronized boolean containsLog(int id) {
        return containsEntity(id, TABLE_LOGS, KEY_LOG_ID);
    }

    public synchronized Training getTraining(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(TABLE_TRAININGS, new String[]{KEY_TRAINING_ID, KEY_TRAINING_DAY}, KEY_TRAINING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException("There is no Training with id - " + id);
        } else {
            Training training = new Training
                    .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                    .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                    .build();
            cursor.close();
            closeDatabase();
            return training;
        }
    }

    public synchronized boolean containsTrainingContent(int id) {
        return containsEntity(id, TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID);
    }

    public synchronized TrainingContent getTrainingContent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_AMOUNT, KEY_TRAINING_CONTENT_WEIGHT, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException("There is no TrainingContent with id - " + id);
        } else {
            Exercise exercise = null;
            int exerciseId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_EXERCISE));
            if (containsExercise(exerciseId)) {
                exercise = getExercise(exerciseId);
            }
            Training training = null;
            int trainingId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_TRAINING));
            if (containsTraining(trainingId)) {
                training = getTraining(trainingId);
            }
            TrainingContent trainingContent = new TrainingContent
                    .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID)))
                    .addExercise(exercise)
                    .addTraining(training)
                    .addAmount(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_AMOUNT)))
                    .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_WEIGHT)))
                    .addComment(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_CONTENT_COMMENT)))
                    .build();

            cursor.close();
            closeDatabase();
            return trainingContent;
        }
    }

    public synchronized TrainingContent getTrainingContent(int idExercise, int idTraining) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = mDatabase.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID,
                        KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_AMOUNT,
                        KEY_TRAINING_CONTENT_WEIGHT, KEY_TRAINING_CONTENT_COMMENT},
                KEY_TRAINING_CONTENT_ID_EXERCISE + "=? AND " +
                        KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(idExercise), String.valueOf(idTraining)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            closeDatabase();
            throw new TableDoesNotContainElementException(
                    String.format("There is no TrainingContent with trainingId = '%s' and exerciseId='%s'", idTraining, idExercise));
        } else {
            Exercise exercise = null;
            int exerciseId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_EXERCISE));
            if (containsExercise(exerciseId)) {
                exercise = getExercise(exerciseId);
            }
            Training training = null;
            int trainingId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_TRAINING));
            if (containsTraining(trainingId)) {
                training = getTraining(trainingId);
            }
            TrainingContent trainingContent = new TrainingContent
                    .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID)))
                    .addExercise(exercise)
                    .addTraining(training)
                    .addAmount(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_AMOUNT)))
                    .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_WEIGHT)))
                    .addComment(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_CONTENT_COMMENT)))
                    .build();
            cursor.close();
            closeDatabase();
            return trainingContent;
        }
    }

    public synchronized Log getLog(int id) {
        openDatabase();
        try {
            Cursor cursor = mDatabase.query(TABLE_LOGS, new String[]{KEY_LOG_ID, KEY_LOG_DATETIME, KEY_LOG_TEXT}, KEY_LOG_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                closeDatabase();
                throw new TableDoesNotContainElementException("There is no LOG with id - " + id);
            } else {
                Log log = new Log
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_LOG_ID)))
                        .addDatetime(cursor.getInt(cursor.getColumnIndex(KEY_LOG_DATETIME)))
                        .addText(cursor.getString(cursor.getColumnIndex(KEY_LOG_TEXT)))
                        .build();
                cursor.close();
                return log;
            }
        } finally {
            closeDatabase();
        }
    }

    public synchronized void deleteAllUsers() {
        openDatabase();
        mDatabase.delete(TABLE_USERS, null, null);
        closeDatabase();
    }

    public synchronized void deleteAllWeightChangeCalendar() {
        openDatabase();
        mDatabase.delete(TABLE_WEIGHT_CHANGE_CALENDAR, null, null);
        closeDatabase();
    }

    public synchronized void deleteAllWeightChangeCalendarOfUser(int user_id) {
        openDatabase();
        mDatabase.delete(TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + "=?", new String[]{String.valueOf(user_id)});
        closeDatabase();
    }

    public synchronized void deleteAllExercises() {
        openDatabase();
        mDatabase.delete(TABLE_EXERCISES, null, null);
        closeDatabase();
    }

    public synchronized void deleteAllExercisesOfUser(int user_id) {
        openDatabase();
        mDatabase.delete(TABLE_EXERCISES, KEY_EXERCISE_ID_USER + "=?", new String[]{String.valueOf(user_id)});
        closeDatabase();
    }

    public synchronized void deleteAllTrainings() {
        openDatabase();
        mDatabase.delete(TABLE_TRAININGS, null, null);
        closeDatabase();
    }

    public synchronized void deleteAllTrainingsOfUser(int user_id) {
        openDatabase();
        String whereArgs[] = {String.valueOf(user_id)};
        mDatabase.delete(TABLE_TRAININGS, KEY_TRAINING_ID_USER + "=?", new String[]{String.valueOf(user_id)});
        closeDatabase();
    }

    public synchronized void deleteAllTrainingContent() {
        openDatabase();
        mDatabase.delete(TABLE_TRAINING_CONTENT, null, null);
    }

    public synchronized void deleteAllTrainingContentOfUser(int user_id) {
        openDatabase();
        mDatabase.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID_USER + "=?", new String[]{String.valueOf(user_id)});
    }

    public synchronized void deleteAllLogs() {
        openDatabase();
        mDatabase.delete(TABLE_LOGS, null, null);
        closeDatabase();
    }

    public synchronized List<User> getAllUsers() {
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<User> userList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        ;
        if (cursor.moveToFirst()) {
            do {
                User user = new User.Builder(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)))
                        .addIsCurrentUser(cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_CURRENT)))
                        .build();
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return userList;
    }

    public synchronized List<WeightChangeCalendar> getAllWeightChangeCalendar() {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEIGHT_CHANGE_CALENDAR;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_DAY)))
                        .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT)))
                        .build();
                weightChangeCalendarList.add(weightChangeCalendar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return weightChangeCalendarList;
    }

    public synchronized List<WeightChangeCalendar> getAllWeightChangeCalendarOfUser(int user_id) {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEIGHT_CHANGE_CALENDAR + " WHERE "
                + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + "=" + user_id + " ORDER BY " + KEY_WEIGHT_CHANGE_CALENDAR_DAY;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_DAY)))
                        .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT)))
                        .build();
                weightChangeCalendarList.add(weightChangeCalendar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return weightChangeCalendarList;
    }

    public synchronized List<Exercise> getAllExercises() {
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);

        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_ID)))
                        .addIsActive(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_IS_ACTIVE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addExplanation(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_EXPLANATION)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .addPicture(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_PICTURE_NAME)))
                        .build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Log> getAllLogs() {
        String selectQuery = "SELECT  * FROM " + TABLE_LOGS;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<Log> logs = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        ;
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log.Builder(cursor.getInt(cursor.getColumnIndex(KEY_LOG_ID)))
                        .addDatetime(cursor.getLong(cursor.getColumnIndex(KEY_LOG_DATETIME)))
                        .addText(cursor.getString(cursor.getColumnIndex(KEY_LOG_TEXT)))
                        .build();
                logs.add(log);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return logs;
    }

    public synchronized List<Exercise> getAllExercisesOfUser(int user_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_ID_USER + "="
                + user_id + " ORDER BY " + KEY_EXERCISE_ID;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_ID)))
                        .addIsActive(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_IS_ACTIVE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addExplanation(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_EXPLANATION)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .addPicture(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_PICTURE_NAME)))
                        .build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Exercise> getAllActiveExercises() {
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_IS_ACTIVE + " = 1" + " ORDER BY " + KEY_EXERCISE_ID;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_ID)))
                        .addIsActive(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_IS_ACTIVE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addExplanation(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_EXPLANATION)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .addPicture(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_PICTURE_NAME)))
                        .build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Exercise> getAllActiveExercisesOfUser(int user_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_IS_ACTIVE + " = 1 AND "
                + KEY_EXERCISE_ID_USER + "=" + user_id + " ORDER BY " + KEY_EXERCISE_ID;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_ID)))
                        .addIsActive(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_IS_ACTIVE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addExplanation(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_EXPLANATION)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .addPicture(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_PICTURE_NAME)))
                        .build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Exercise> getExercisesByDates(long mDateFrom, long mDateTo) {

        mDateFrom = mDateFrom == 0 ? Long.MIN_VALUE : mDateFrom;
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT " +
                TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " AS " + KEY_TRAINING_CONTENT_ID_EXERCISE + ","
                + TABLE_EXERCISES + "." + KEY_EXERCISE_NAME + " AS " + KEY_EXERCISE_NAME + " ,"
                + TABLE_EXERCISES + "." + KEY_EXERCISE_AMOUNT_DEFAULT + " AS " + KEY_EXERCISE_AMOUNT_DEFAULT + " FROM "
                + TABLE_TRAININGS + "," + TABLE_EXERCISES + "," + TABLE_TRAINING_CONTENT
                + " WHERE " + TABLE_EXERCISES + "." + KEY_EXERCISE_ID
                + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE
                + " AND " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + "=" + TABLE_TRAININGS + "." + KEY_TRAINING_ID
                + " AND " + KEY_TRAINING_DAY + ">= " + mDateFrom + " AND " + KEY_TRAINING_DAY + "<=" + mDateTo
                + " GROUP BY (" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + ")"
                + " ORDER BY " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_EXERCISE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Exercise> getExercisesOfUserByDates(int user_id, long mDateFrom, long mDateTo) {

        mDateFrom = mDateFrom == 0 ? Long.MIN_VALUE : mDateFrom;
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " AS " + KEY_TRAINING_CONTENT_ID_EXERCISE + ","
                + TABLE_EXERCISES + "." + KEY_EXERCISE_NAME + " AS " + KEY_EXERCISE_NAME + ","
                + TABLE_EXERCISES + "." + KEY_EXERCISE_AMOUNT_DEFAULT + " AS " + KEY_EXERCISE_AMOUNT_DEFAULT + " FROM "
                + TABLE_TRAININGS + "," + TABLE_EXERCISES + "," + TABLE_TRAINING_CONTENT
                + " WHERE " + KEY_EXERCISE_ID_USER + "=" + user_id + " AND "
                + TABLE_EXERCISES + "." + KEY_EXERCISE_ID
                + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE
                + " AND " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + "=" + TABLE_TRAININGS + "." + KEY_TRAINING_ID
                + " AND " + KEY_TRAINING_DAY + ">= " + mDateFrom + " AND " + KEY_TRAINING_DAY + "<=" + mDateTo
                + " GROUP BY (" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + ")"
                + " ORDER BY " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Exercise> exerciseList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_EXERCISE)))
                        .addName(cursor.getString(cursor.getColumnIndex(KEY_EXERCISE_NAME)))
                        .addAmountDefault(cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE_AMOUNT_DEFAULT)))
                        .build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return exerciseList;
    }

    public synchronized List<Training> getAllTrainings() {
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS + " ORDER BY " + KEY_TRAINING_DAY;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<Training> getAllTrainingsOfUser(int user_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS + " WHERE " + KEY_TRAINING_ID_USER + "=" + user_id + " ORDER BY " + KEY_TRAINING_DAY + " DESC";
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<Training> getTrainingsByDates(long mDateFrom, long mDateTo) {
        mDateFrom = mDateFrom == 0 ? Long.MAX_VALUE : mDateFrom;
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT  "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " AS " + KEY_TRAINING_ID + ","
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY
                + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ">= " + mDateFrom
                + " AND " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<=" + mDateTo
                + " ORDER BY " + KEY_TRAINING_DAY;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<Training> getTrainingsOfUserByDates(int user_id, long mDateFrom, long mDateTo) {
        mDateFrom = mDateFrom == 0 ? Long.MAX_VALUE : mDateFrom;
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT  "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " AS " + KEY_TRAINING_ID + ","
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY
                + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id + " AND "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ">= " + mDateFrom + " AND "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<=" + mDateTo
                + " ORDER BY " + KEY_TRAINING_ID;// +" LIMIT 10";
        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<Training> getLastTrainingsByDates(long mDateTo) {
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT  "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " AS " + KEY_TRAINING_ID + ","
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY
                + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY +
                " IN (SELECT MAX(" + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ") FROM " + TABLE_TRAININGS
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<" + mDateTo + " ) ORDER BY " + KEY_TRAINING_ID;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<Training> getLastTrainingsOfUserByDates(int user_id, long mDateTo) {
        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;
        String selectQuery = "SELECT  "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " AS " + KEY_TRAINING_ID + ","
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY
                + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " IN (SELECT MAX(" + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ") FROM " + TABLE_TRAININGS
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<" + mDateTo + " ) ORDER BY " + KEY_TRAINING_ID;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<Training> trainingsList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Training training = new Training
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                        .addDay(cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY)))
                        .build();
                trainingsList.add(training);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return trainingsList;
    }

    public synchronized List<TrainingContent> getLastExerciseNotNullAmount(long mDateTo, int exercise_id) {

        mDateTo = mDateTo == 0 ? Long.MAX_VALUE : mDateTo;

        String selectQuery = "SELECT "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID + " AS " + KEY_TRAINING_CONTENT_ID + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " AS " + KEY_TRAINING_CONTENT_AMOUNT + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " AS " + KEY_TRAINING_CONTENT_WEIGHT
                + " FROM (SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID
                + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " FROM "
                + TABLE_TRAINING_CONTENT + " WHERE " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " <>\"\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " <>\"0\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " = " + exercise_id + ") AS " + TABLE_TRAINING_CONTENT
                + " LEFT JOIN (SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " FROM "
                + TABLE_TRAININGS + " ) AS " + TABLE_TRAININGS
                + " ON " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<" + mDateTo + " ORDER BY " + KEY_TRAINING_DAY + " desc limit 1";

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<TrainingContent> trainingsContentList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                long day = cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY));
                int amount = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_AMOUNT));
                if (amount != 0 && day != 0) {
                    TrainingContent trainingContent = new TrainingContent
                            .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ID)))
                            .addAmount(amount)
                            .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_WEIGHT)))
                            .build();

                    trainingsContentList.add(trainingContent);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsContentList;
    }

    public synchronized List<TrainingContent> getLastExerciseNotNullAmountAndWeightOfUser(int user_id, long mDateTo, int exercise_id) {

        mDateTo = "".equals(0.0) ? Long.MAX_VALUE : mDateTo;

        String selectQuery = "SELECT "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " AS " + KEY_TRAINING_DAY + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID + " AS " + KEY_TRAINING_CONTENT_ID + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " AS " + KEY_TRAINING_CONTENT_AMOUNT + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " AS " + KEY_TRAINING_CONTENT_WEIGHT
                + " FROM (SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID
                + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " FROM "
                + TABLE_TRAINING_CONTENT + " WHERE " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " <>\"\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_AMOUNT + " <>\"0\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " = " + exercise_id + ") AS " + TABLE_TRAINING_CONTENT
                + " LEFT JOIN (SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " FROM "
                + TABLE_TRAININGS + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id + ") AS " + TABLE_TRAININGS
                + " ON " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<" + mDateTo + "" + " ORDER BY " + KEY_TRAINING_DAY + " desc limit 1";

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<TrainingContent> trainingsContentList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                long day = cursor.getLong(cursor.getColumnIndex(KEY_TRAINING_DAY));
                int amount = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_AMOUNT));
                if (amount != 0 && day != 0) {
                    TrainingContent trainingContent = new TrainingContent
                            .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID)))
                            .addAmount(amount)
                            .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_WEIGHT)))
                            .build();
                    trainingsContentList.add(trainingContent);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDatabase();
        return trainingsContentList;
    }

    public synchronized List<WeightChangeCalendar> getWeightOfUserFromWeightCalendar(int user_id, long mDateTo) {

        mDateTo = "".equals(0.0) ? Long.MAX_VALUE : mDateTo;

        String selectQuery = "SELECT "
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID + " AS " + KEY_WEIGHT_CHANGE_CALENDAR_ID + ","
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " AS " + KEY_WEIGHT_CHANGE_CALENDAR_DAY + ","
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT + " AS " + KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT
                + " FROM " + TABLE_WEIGHT_CHANGE_CALENDAR + " WHERE "
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + " =" + user_id
                + " AND " + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + "<=" + mDateTo
                + " ORDER BY " + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " desc limit 1";

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                int weight = cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT));
                long day = cursor.getLong(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_DAY));
                if (weight != 0 & day != 0) {

                    WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar
                            .Builder(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT_CHANGE_CALENDAR_ID)))
                            .addDay(day)
                            .addWeight(weight)
                            .build();
                    weightChangeCalendarList.add(weightChangeCalendar);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return weightChangeCalendarList;
    }


    public synchronized List<TrainingContent> getAllTrainingContentOfTraining(int training_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT + " WHERE " + KEY_TRAINING_CONTENT_ID_TRAINING
                + "=" + training_id + " ORDER BY " + KEY_TRAINING_CONTENT_ID;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<TrainingContent> trainingContentList = new ArrayList<>(cursor.moveToFirst() ? cursor.getCount() : 0);
        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = null;
                int exerciseId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_EXERCISE));
                if (containsExercise(exerciseId)) {
                    exercise = getExercise(exerciseId);
                }
                Training training = null;
                int trainingId = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID_TRAINING));
                if (containsTraining(trainingId)) {
                    training = getTraining(trainingId);
                }
                TrainingContent trainingContent = new TrainingContent
                        .Builder(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_ID)))
                        .addExercise(exercise)
                        .addTraining(training)
                        .addAmount(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_AMOUNT)))
                        .addWeight(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_CONTENT_WEIGHT)))
                        .addComment(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_CONTENT_COMMENT)))
                        .build();

                trainingContentList.add(trainingContent);
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();
        return trainingContentList;
    }

    public synchronized int getEntityMaxNumber(String tableName, String keyColumn) {
        String countQuery = "SELECT  MAX(" + keyColumn + ") FROM " + tableName + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery(countQuery, null);

        cursor.moveToFirst();
        int size = 0;
        if (cursor.getCount() != 0) {
            size = cursor.getInt(0);
        }
        cursor.close();
        return size;
    }

    public synchronized int getUserMaxNumber() {
        return getEntityMaxNumber(TABLE_USERS, KEY_USER_ID);
    }

    public synchronized int getWeightChangeCalendarMaxNumber() {
        return getEntityMaxNumber(TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID);
    }

    public synchronized int getExerciseMaxNumber() {
        return getEntityMaxNumber(TABLE_EXERCISES, KEY_EXERCISE_ID);
    }

    public synchronized int getTrainingMaxNumber() {
        return getEntityMaxNumber(TABLE_TRAININGS, KEY_TRAINING_ID);
    }

    public synchronized int getTrainingContentMaxNumber() {
        return getEntityMaxNumber(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID);
    }

    public synchronized int getLogMaxNumber() {
        return getEntityMaxNumber(TABLE_LOGS, KEY_LOG_ID);
    }

    public synchronized int updateUser(User user) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());
        int rows = mDatabase.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized int updateWeightChangeCalendar(WeightChangeCalendar weightChangeCalendar) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_DAY, weightChangeCalendar.getDay());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_ID_USER, weightChangeCalendar.getUserId());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT, weightChangeCalendar.getWeight());

        int rows = mDatabase.update(TABLE_WEIGHT_CHANGE_CALENDAR, values, KEY_WEIGHT_CHANGE_CALENDAR_ID + " = ?",
                new String[]{String.valueOf(weightChangeCalendar.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized int updateExercise(Exercise exercise) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_IS_ACTIVE, exercise.getIsActive());
        values.put(KEY_EXERCISE_ID_USER, exercise.getUserId());
        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_EXERCISE_EXPLANATION, exercise.getExplanation());
        values.put(KEY_EXERCISE_AMOUNT_DEFAULT, exercise.getAmountDefault());
        values.put(KEY_EXERCISE_PICTURE_NAME, exercise.getPicture());

        int rows = mDatabase.update(TABLE_EXERCISES, values, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized int updateTraining(Training training) {
        openDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TRAINING_ID_USER, training.getUserId());
        values.put(KEY_TRAINING_DAY, training.getDay());

        int rows = mDatabase.update(TABLE_TRAININGS, values, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(training.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized int updateTrainingContent(TrainingContent trainingContent) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_CONTENT_ID_USER, trainingContent.getUserId());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE, trainingContent.getExerciseId());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING, trainingContent.getTrainingId());
        values.put(KEY_TRAINING_CONTENT_AMOUNT, trainingContent.getAmount());
        values.put(KEY_TRAINING_CONTENT_WEIGHT, trainingContent.getWeight());
        values.put(KEY_TRAINING_CONTENT_COMMENT, trainingContent.getComment());

        int rows = mDatabase.update(TABLE_TRAINING_CONTENT, values, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized int updateLog(Log log) {
        openDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOG_DATETIME, log.getDatetime());
        values.put(KEY_LOG_TEXT, log.getText());
        int rows = mDatabase.update(TABLE_LOGS, values, KEY_LOG_ID + " = ?",
                new String[]{String.valueOf(log.getId())});
        closeDatabase();
        return rows;
    }

    public synchronized void deleteUser(User user) {
        openDatabase();
        mDatabase.delete(TABLE_USERS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        closeDatabase();
    }

    public synchronized void deleteWeightChangeCalendar(WeightChangeCalendar weightChangeCalendar) {
        openDatabase();
        mDatabase.delete(TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID + " = ?",
                new String[]{String.valueOf(weightChangeCalendar.getId())});
        closeDatabase();
    }

    public synchronized void deleteExercise(Exercise exercise) {
        openDatabase();
        mDatabase.delete(TABLE_EXERCISES, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getId())});
        closeDatabase();
    }

    public synchronized void deleteTraining(Training training) {
        openDatabase();
        mDatabase.delete(TABLE_TRAININGS, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(training.getId())});
        closeDatabase();
    }

    public synchronized void deleteTrainingContent(TrainingContent trainingContent) {
        openDatabase();
        mDatabase.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getId())});
        closeDatabase();
    }

    public synchronized void deleteTrainingContentOfTraining(int id_traning) {
        openDatabase();
        mDatabase.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID_TRAINING + " = ?",
                new String[]{String.valueOf(id_traning)});
        closeDatabase();
    }

    public synchronized void deleteLog(Log log) {
        openDatabase();
        mDatabase.delete(TABLE_LOGS, KEY_LOG_ID + " = ?",
                new String[]{String.valueOf(log.getId())});
        closeDatabase();
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        openDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);
        try {
            String maxQuery = Query;
            //export the query results will be save in Cursor c
            Cursor c = mDatabase.rawQuery(maxQuery, null);
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
            android.util.Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;

        }
    }
}
