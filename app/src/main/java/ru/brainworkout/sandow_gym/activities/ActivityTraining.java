package ru.brainworkout.sandow_gym.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.brainworkout.sandow_gym.common.Common;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.entities.Exercise;
import ru.brainworkout.sandow_gym.R;
import ru.brainworkout.sandow_gym.database.entities.Training;
import ru.brainworkout.sandow_gym.database.entities.TrainingContent;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;

public class ActivityTraining extends AppCompatActivity {

    private static final int NUMBER_OF_VIEWS = 30000;
    private static final int MAX_NUMBER_OF_TRANSFER_BUTTONS = 7;
    private SharedPreferences mSettings;
    private boolean mShowPicture;
    private boolean mShowExplanation;
    private boolean mShowVolumeDefaultButton;
    private boolean mShowVolumeLastDayButton;
    private Training mCurrentTraining;
    private TrainingContent mCurrentTrainingContent;
    private Exercise mCurrentExercise;
    private String mVolumeLastDay = "";
    private final DatabaseManager DB = new DatabaseManager(this);
    private boolean mTrainingIsNew;
    private int mHeight;
    private int mWidth;
    private int mTextSize;
    private List<Exercise> mActiveExercises;
    private int mCurrentExerciseNumberInList;
    private List<TrainingContent> mTrainingContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        getPreferencesFromFile();
        setPreferencesOnScreen();

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        mHeight = displaymetrics.heightPixels / 2;
        mWidth = displaymetrics.widthPixels / 1;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        Intent intent = getIntent();
        mTrainingIsNew = intent.getBooleanExtra("IsNew", false);

        String mCurrentDate = intent.getStringExtra("CurrentDate");

        if (mTrainingIsNew) {

            mCurrentTraining = new Training.TrainingBuilder(DB.getTrainingMaxNumber() + 1).build();
            //Calendar calendar = Calendar.getInstance();
            if ((mCurrentDate == null)) {
                String cal = (Calendar.getInstance().getTime()).toLocaleString();
                try {
                    mCurrentTraining.setDay(Calendar.getInstance().getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mCurrentTraining.setDay(Common.ConvertStringToDate(mCurrentDate, Common.DATE_FORMAT_STRING));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            int id = intent.getIntExtra("CurrentID", 0);
            if (id == 0) {
                mCurrentTraining = new Training.TrainingBuilder(DB.getTrainingMaxNumber() + 1).build();
            } else {

                try {
                    mCurrentTraining = DB.getTraining(id);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    //возможно удалили элемент
                    tableDoesNotContainElementException.printStackTrace();
                }
            }
            try {
                if ((mCurrentDate != null)) {
                    mCurrentTraining.setDayString(mCurrentDate);
                }
            } catch (Exception e) {
            }
        }


        showTrainingOnScreen();

        //не используем. Почему то на Philips W732 не работает прокрутка вниз. на всех остальных устройствах работает.
        //убираю скрол вправо, влево. перемещение кнопками внизу

//        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(ActivityTraining.this);
//        ScrollView sv = (ScrollView) this.findViewById(R.id.svMain);
//        sv.setOnTouchListener(swipeDetectorActivity);


        if (mTrainingIsNew) {
            getAllActiveExercises();
        } else {
            getAllExercisesOfTraining();
        }

        saveTraining();
        updateTrainingList();


        int exID = intent.getIntExtra("CurrentExerciseID", 0);

        if (exID != 0) {
            saveAndGoToNewExercise(exID);
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (Common.mCurrentUser != null) {
            this.setTitle(getTitle() + "(" + Common.mCurrentUser.getName() + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferencesFromFile();
        setPreferencesOnScreen();

    }

    public void btVolumeDefault_onClick(final View view) {

        Common.blink(view);
        if (mCurrentExercise != null) {
            if (!"".equals(mCurrentExercise.getVolumeDefault())) {
                int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
                TextView etVolume = (TextView) findViewById(mVolumeID);
                if (etVolume != null) {
                    etVolume.setText(mCurrentExercise.getVolumeDefault());
                }
            }
        }

    }

    public void btVolumeLastDay_onClick(final View view) {

        Common.blink(view);
        int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
        TextView etVolume = (TextView) findViewById(mVolumeID);
        if (etVolume != null) {
            if ("".equals(mVolumeLastDay)) {
                etVolume.setText("0");
            } else {
                etVolume.setText(mVolumeLastDay);
            }
        }

    }

    public void btOptions_onClick(final View view) {

        Common.blink(view);

        Intent intent = new Intent(ActivityTraining.this, ActivityTrainingOptions.class);

        startActivity(intent);

    }


    private class ExerciseComparator implements Comparator {
        public int compare(Object ex1, Object ex2) {
            return ((Exercise) (ex1)).getID() - ((Exercise) (ex2)).getID();
        }
    }

    private void setNextExercise() {
        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != mActiveExercises.size() - 1) {

                mCurrentExerciseNumberInList++;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран
                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == mCurrentExercise.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }
                if (!isFound) {
                    mCurrentTrainingContent = new TrainingContent.TrainingContentBuilder(DB.getTrainingContentMaxNumber() + 1)
                            .addExerciseId(mCurrentExercise.getID())
                            .addTrainingId(mCurrentTraining.getID())
                            .addVolume("")
                            .build();
                    mCurrentTrainingContent.dbSave(DB);
                }
            }
        }
    }

    private void showExercise() {
        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {
            if (mCurrentExercise.getPicture() != null && !"".equals(mCurrentExercise.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(mCurrentExercise.getPicture(), "drawable", getPackageName()));
                ivPicture.setMinimumHeight(mHeight);
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }
        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(mCurrentExercise.getExplanation());
        }
        showTrainingContentOnScreen(mCurrentTrainingContent.getIdExercise());
        updateTrainingList();

    }

    private void setPreviousExercise() {

        if (mActiveExercises.size() != 0) {
            if (mCurrentExerciseNumberInList != 0) {

                mCurrentExerciseNumberInList--;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);

                //ищем есть ли в списке упражнение с ID. Если нет - создаем новое, есть - выводим на экран
                boolean isFound = false;
                for (TrainingContent mTr : mTrainingContentList) {
                    if (mTr.getIdExercise() == mCurrentExercise.getID()) {
                        isFound = true;
                        mCurrentTrainingContent = mTr;
                        mTrainingContentList.remove(mTrainingContentList.indexOf(mTr));
                        break;
                    }
                }

                if (!isFound) {

                    mCurrentTrainingContent = new TrainingContent.TrainingContentBuilder(DB.getTrainingContentMaxNumber() + 1)
                            .addExerciseId(mCurrentExercise.getID())
                            .addTrainingId(mCurrentTraining.getID())
                            .addVolume("")
                            .build();
                    mCurrentTrainingContent.dbSave(DB);
                }
            }
        }

    }

    private void getAllExercisesOfTraining() {

        Log.d("Reading: ", "Reading all active exercises..");
        mActiveExercises = DB.getAllActiveExercisesOfUser(Common.mCurrentUser.getID());
        Log.d("Reading: ", "Reading all exercises of training ..");
        mTrainingContentList = DB.getAllTrainingContentOfTraining(mCurrentTraining.getID());

        for (TrainingContent tr : mTrainingContentList
                ) {
            boolean isFound = false;
            int id_ex = tr.getIdExercise();
            for (Exercise ex : mActiveExercises
                    ) {
                if (ex.getID() == id_ex) {
                    isFound = true;
                    break;
                }

            }
            //если в текущих активных не нашли - добавляем новое
            if (!isFound) {
                //добавим в список упражнений упражнение старое
                Exercise ex = null;
                try {
                    ex = DB.getExercise(id_ex);
                } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                    tableDoesNotContainElementException.printStackTrace();
                }
                mActiveExercises.add(ex);
            }
        }

        //отсортируем по ID список упражнений
        Collections.sort(mActiveExercises, new ExerciseComparator());

        if (mActiveExercises.size() != 0) {
            mCurrentExerciseNumberInList = 0;
            mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
            //покажем первое упражнение

            if (mTrainingContentList.size() != 0 && mTrainingContentList.get(0).getIdExercise() == mCurrentExercise.getID()) {
                mCurrentTrainingContent = mTrainingContentList.get(0);
            } else {
                mCurrentTrainingContent = new TrainingContent.TrainingContentBuilder(DB.getTrainingContentMaxNumber() + 1)
                        .addExerciseId(mCurrentExercise.getID())
                        .addTrainingId(mCurrentTraining.getID())
                        .addVolume("")
                        .build();
                mCurrentTrainingContent.dbSave(DB);
            }
            showTrainingContentOnScreen(mCurrentExercise);

        }
    }

    private void getAllActiveExercises() {

        if (Common.mCurrentUser != null) {
            mActiveExercises = DB.getAllActiveExercisesOfUser(Common.mCurrentUser.getID());
            mTrainingContentList = new ArrayList<>();

            Exercise ex1;
            if (mActiveExercises.size() != 0) {
                mCurrentExerciseNumberInList = 0;
                mCurrentExercise = mActiveExercises.get(mCurrentExerciseNumberInList);
                //покажем первое упражнение
                showTrainingContentOnScreen(mCurrentExercise);
                mCurrentTrainingContent = new TrainingContent.TrainingContentBuilder(DB.getTrainingContentMaxNumber() + 1)
                        .addExerciseId(mCurrentExercise.getID())
                        .addTrainingId(mCurrentTraining.getID())
                        .addVolume("")
                        .build();
                mCurrentTrainingContent.dbSave(DB);
            }
        }
    }

    private void showTrainingContentOnScreen(final Exercise ex) {

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);
        if (ivPicture != null) {

            if (ex.getPicture() != null && !"".equals(ex.getPicture())) {
                ivPicture.setImageResource(getResources().getIdentifier(ex.getPicture(), "drawable", getPackageName()));
            } else {
                ivPicture.setBackgroundColor(Color.WHITE);
            }

        }
        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);
        if (tvExplanation != null) {

            tvExplanation.setText(ex.getExplanation());
        }
        TextView tvExerciseName = (TextView) findViewById(R.id.tvExerciseName);
        if (tvExerciseName != null) {

            tvExerciseName.setText("Упражнение: " + ex.getName());
        }

        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {
            if (mCurrentTrainingContent != null) {
                etComment.setText(mCurrentTrainingContent.getComment());
            }
        }
        int mVolumeID = getResources().getIdentifier("etVolume", "id", getPackageName());
        TextView etVolume = (TextView) findViewById(mVolumeID);
        if (etVolume != null) {
            if (mCurrentTrainingContent != null) {
                String vol = mCurrentTrainingContent.getVolume();
                if (vol != null && vol != "") {
                    etVolume.setText(vol);
                } else {
                    etVolume.setText("");
                }
            }

        }

        Button btDefaultVolume = (Button) findViewById(R.id.btVolumeDefault);
        if (btDefaultVolume != null) {

            String mVolumeDefault = mCurrentExercise.getVolumeDefault();
            btDefaultVolume.setText("DEFAULT VOL: " + String.valueOf("".equals(mVolumeDefault) ? "--" : mVolumeDefault));
        }
        Button btYesterdayVolume = (Button) findViewById(R.id.btVolumeLastDay);
        if (btYesterdayVolume != null) {
            List<TrainingContent> mTrainingsContentList = new ArrayList<TrainingContent>();

            if (Common.mCurrentUser != null) {
                mTrainingsContentList = DB.getLastExerciseNotNullVolumeOfUser(Common.mCurrentUser.getID(),
                        Common.ConvertDateToString(mCurrentTraining.getDay(), Common.DATE_FORMAT_STRING), mCurrentExercise.getID());
            }
            if (mTrainingsContentList.size() == 1) {
                try {
                    mVolumeLastDay = mTrainingsContentList.get(0).getVolume();

                } catch (Exception e) {
                    mVolumeLastDay = "";
                }
//                try {
//                    mCurrentTrainingContent.setWeight(String.valueOf(mTrainingsContentList.get(0).getVolume()));
//                } catch (Exception e) {}

            }
            btYesterdayVolume.setText("LAST VOL: " + String.valueOf("".equals(mVolumeLastDay) ? "--" : mVolumeLastDay));

        }

        int mWeight = getResources().getIdentifier("etWeight", "id", getPackageName());
        TextView etWeight = (TextView) findViewById(mWeight);
        if (etWeight != null && mCurrentTrainingContent != null) {

            etWeight.setText(String.valueOf(mCurrentTrainingContent.getWeight()));
        }
    }

//    private class Container extends ScrollView {
//
//        public Container(Context context) {
//            super(context);
//            setBackgroundColor(0xFF0000FF);
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent ev) {
//            Log.i(TAG, "onInterceptTouchEvent");
//            int action = ev.getActionMasked();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_DOWN");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE");
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    Log.i(TAG, "onInterceptTouchEvent.ACTION_UP");
//                    break;
//            }
//            return super.onInterceptTouchEvent(ev);
//        }
//    }

    private void showTrainingContentOnScreen(final int ex_id) {

        try {
            mCurrentExercise = DB.getExercise(ex_id);
        } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
            tableDoesNotContainElementException.printStackTrace();
        }

        showTrainingContentOnScreen(mCurrentExercise);

    }

//    public boolean onTouchEvent(MotionEvent event)
//    {
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//
//                if (Math.abs(deltaX) > MIN_DISTANCE)
//                {
//                    // Left to Right swipe action
//                    if (x2 > x1)
//                    {
//                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                    // Right to left swipe action
//                    else
//                    {
//                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
//                    }
//
//                }
//                else
//                {
//                    // consider as something else - a screen tap for example
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }


    private void showTrainingOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentTraining.getID()));
        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView etDay = (TextView) findViewById(mDayID);
        if (etDay != null) {
            if (mCurrentTraining.getDay() == null) {
                etDay.setText("");
            } else {
                etDay.setText(Common.ConvertDateToString(mCurrentTraining.getDay(), Common.DATE_FORMAT_STRING));
            }
        }


    }

    public void btClose_onClick(final View view) {

        Common.blink(view);

        Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
        intent.putExtra("id", mCurrentTraining.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void getPropertiesFromScreen() {


        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            mCurrentTraining.setID(Integer.parseInt(String.valueOf(tvID.getText())));

        }
        //Имя
        int mDayID = getResources().getIdentifier("tvDay", "id", getPackageName());
        TextView tvDay = (TextView) findViewById(mDayID);
        if (tvDay != null) {

            Date d = Common.ConvertStringToDate(String.valueOf(tvDay.getText()), Common.DATE_FORMAT_STRING);

            if (d != null) {
                try {
                    mCurrentTraining.setDay(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            try {
                mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));

            } catch (Exception e) {

            }
        }

        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        if (etWeight != null) {
            try {
                mCurrentTrainingContent.setWeight(Integer.parseInt(String.valueOf(etWeight.getText())));

            } catch (Exception e) {

            }
        }

        EditText etComment = (EditText) findViewById(R.id.etComment);
        if (etComment != null) {
            try {
                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));

            } catch (Exception e) {

            }
        }

    }

    public void btSave_onClick(final View view) {

        Common.blink(view);

        saveTraining();

    }

    public void saveTraining() {

        getPropertiesFromScreen();

        mCurrentTraining.dbSave(DB);

        if (mCurrentTrainingContent != null) {

            mCurrentTrainingContent.dbSave(DB);
        }
        mTrainingIsNew = false;

    }

    private void saveTrainingContent(final boolean readFromScreen) {

        if (readFromScreen) {
            EditText etVolume = (EditText) findViewById(R.id.etVolume);
            if (etVolume != null) {

                mCurrentTrainingContent.setVolume(String.valueOf(etVolume.getText()));
            }
            EditText etComment = (EditText) findViewById(R.id.etComment);
            if (etComment != null) {

                mCurrentTrainingContent.setComment(String.valueOf(etComment.getText()));
            }
        }
        mTrainingContentList.add(mCurrentTrainingContent);
        mCurrentTrainingContent.dbSave(DB);

    }

    public void btDelete_onClick(final View view) {

        Common.blink(view);

        if (!mTrainingIsNew) {

            new AlertDialog.Builder(this)
                    .setMessage("Вы действительно хотите удалить текущую тренировку?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mCurrentTrainingContent.dbDelete(DB);
                            mCurrentTraining.dbDelete(DB);

                            Intent intent = new Intent(getApplicationContext(), ActivityTrainingsList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Нет", null).show();
        }
    }

    public void tvDay_onClick(final View view) {

        Common.blink(view);

        Intent intent = new Intent(ActivityTraining.this, ActivityCalendarView.class);

        intent.putExtra("IsNew", mTrainingIsNew);
        intent.putExtra("CurrentActivity", "ActivityTraining");
        if (!mTrainingIsNew) {
            intent.putExtra("CurrentTrainingID", mCurrentTraining.getID());
        }
        if (mCurrentTraining.getDay() == null) {
            intent.putExtra("CurrentDate", "");
        } else {
            intent.putExtra("CurrentDate", Common.ConvertDateToString(mCurrentTraining.getDay(), Common.DATE_FORMAT_STRING));
        }
        intent.putExtra("CurrentExerciseID", mCurrentExerciseNumberInList);

        startActivity(intent);

    }

    private class SwipeDetectorActivity extends AppCompatActivity implements View.OnTouchListener {

        private Activity activity;
        static final int MIN_DISTANCE = 200;
        private float downX, downY, upX, upY;


        public SwipeDetectorActivity(final Activity activity) {
            this.activity = activity;
        }

        public final void onRightToLeftSwipe() {
            // System.out.println("Right to Left swipe [Previous]");
            Toast.makeText(ActivityTraining.this, "[Следующее упражнение]", Toast.LENGTH_SHORT).show();
            setNextExercise();
            showExercise();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(ActivityTraining.this, "[Предыдущее упражнение]", Toast.LENGTH_SHORT).show();
            setPreviousExercise();
            showExercise();

        }

        public void onTopToBottomSwipe() {

            //Toast.makeText(ActivityTraining.this, "Top to Bottom swipe [Down]", Toast.LENGTH_SHORT).show();

        }

        public void onBottomToTopSwipe() {

            //Toast.makeText(ActivityTraining.this, "Bottom to Top swipe [Up]", Toast.LENGTH_SHORT).show ();

        }


        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                    //break;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        //Toast.makeText(ActivityTraining.this, "DeltaX="+String.valueOf(deltaX), Toast.LENGTH_SHORT).show();
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {

                    }

                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        //Toast.makeText(ActivityTraining.this, "DeltaY="+String.valueOf(deltaY), Toast.LENGTH_SHORT).show();
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            //break;
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            //break;

                            return true;

                        }
                    } else {

                    }
                    //break;
                    return true;
                }
            }
            //Toast.makeText(ActivityTraining.this, "ЖОПА", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void btVolumeLeft_onClick(final View view) {

        Common.blink(view);

        VolumeChange(-1);
    }

    public void btVolumeLeft10_onClick(final View view) {

        Common.blink(view);

        VolumeChange(-10);
    }

    public void btVolumeRight_onClick(final View view) {

        Common.blink(view);

        VolumeChange(1);
    }

    public void btVolumeRight10_onClick(final View view) {

        Common.blink(view);

        VolumeChange(10);
    }

    private void VolumeChange(final int dx) {

        EditText etVolume = (EditText) findViewById(R.id.etVolume);
        if (etVolume != null) {
            int mVolume = 0;
            try {
                mVolume = Integer.parseInt(String.valueOf(etVolume.getText()));
            } catch (Exception e) {
                mVolume = 0;
            }
            mVolume = mVolume + dx;
            mVolume = mVolume < 0 ? 0 : mVolume;
            etVolume.setText(String.valueOf(mVolume));
        }
    }

    private void updateTrainingList() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int btWidth = displaymetrics.widthPixels / MAX_NUMBER_OF_TRANSFER_BUTTONS;
        mTextSize = (int) (btWidth / 3.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trow = (TableRow) findViewById(R.id.trowTrainingList);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        params.span = 4;
        if (trow != null) {
            trow.removeAllViews();
            trow.setMinimumHeight(btWidth);

            int mNumBegin = 0;
            int mNumEnd = 0;
            if (mCurrentExerciseNumberInList + 1 <= 3) {
                mNumBegin = 1;
                mNumEnd = 5;
            } else if (mCurrentExerciseNumberInList >= mActiveExercises.size() - 3) {
                mNumBegin = mActiveExercises.size() - 4;
                mNumEnd = mActiveExercises.size();
            } else {
                mNumBegin = (mCurrentExerciseNumberInList + 1) - 2;
                mNumEnd = (mCurrentExerciseNumberInList + 1) + 2;
            }

            Button but = new Button(this);
            but.setLayoutParams(params);
            but.setText("<-");
            but.setTextSize(mTextSize);
            but.setWidth(btWidth);
            but.setBackgroundResource(R.drawable.bt_border);
            but.setGravity(Gravity.CENTER);
            but.setWidth(btWidth);
            but.setHeight(btWidth);
            but.setTextColor(getResources().getColor(R.color.text_color));
            trow.addView(but);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btTrainingListPrevious_onClick((TextView) v);
                }
            });

            for (int mCount = mNumBegin; mCount <= mNumEnd; mCount++) {
                but = new Button(this);
                but.setLayoutParams(params);
                but.setId(NUMBER_OF_VIEWS + mCount);
                but.setText(String.valueOf(mCount));
                but.setTextSize(mTextSize);
                but.setWidth(btWidth);
                but.setHeight(btWidth);
                but.setBackgroundResource(R.drawable.bt_border);
                but.setGravity(Gravity.CENTER);
                but.setTextColor(getResources().getColor(R.color.text_color));
                if (mCount - 1 == mCurrentExerciseNumberInList) {
                    but.setTextColor(Color.RED);
                    but.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                trow.addView(but);
                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btTrainingList_onClick((TextView) v);
                    }
                });
            }

            but = new Button(this);
            but.setLayoutParams(params);
            but.setText("->");
            but.setWidth(btWidth);
            but.setHeight(btWidth);
            but.setTextSize(mTextSize);
            but.setBackgroundResource(R.drawable.bt_border);
            but.setGravity(Gravity.CENTER);
            but.setTextColor(getResources().getColor(R.color.text_color));
            trow.addView(but);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btTrainingListNext_onClick((TextView) v);
                }
            });
        }

    }

    public void btTrainingList_onClick(TextView view) {

        int newId = view.getId() % NUMBER_OF_VIEWS;
        int step = newId - (mCurrentExerciseNumberInList + 1);
        saveAndGoToNewExercise(step);

    }

    private void saveAndGoToNewExercise(final int steps) {

        saveTrainingContent(true);
        int mStepsABS = Math.abs(steps);
        for (int i = 1; i <= mStepsABS; i++) {
            saveTrainingContent(false);

            if (steps < 0) {
                setPreviousExercise();
            } else {
                setNextExercise();
            }
        }
        showExercise();
    }

    private void btTrainingListPrevious_onClick(final TextView view) {

        saveAndGoToNewExercise(-5);

    }

    private void btTrainingListNext_onClick(final TextView view) {

        saveAndGoToNewExercise(5);

    }

    private void getPreferencesFromFile() {

        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION)) {
            mShowExplanation = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_EXPLANATION, false);
        } else {
            mShowExplanation = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE)) {
            mShowPicture = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_PICTURE, false);
        } else {
            mShowPicture = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON)) {
            mShowVolumeDefaultButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_DEFAULT_BUTTON, false);
        } else {
            mShowVolumeDefaultButton = false;
        }

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON)) {
            mShowVolumeLastDayButton = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_TRAINING_SHOW_VOLUME_LAST_DAY_BUTTON, false);
        } else {
            mShowVolumeLastDayButton = false;
        }
    }

    private void setPreferencesOnScreen() {

        ImageView ivPicture = (ImageView) findViewById(R.id.ivPicture);

        if (ivPicture != null) {
            if (mShowPicture) {
                ivPicture.setVisibility(View.VISIBLE);
            } else {
                ivPicture.setVisibility(View.GONE);
            }
        }

        TextView tvExplanation = (TextView) findViewById(R.id.tvExplanation);

        if (tvExplanation != null) {
            if (mShowExplanation) {
                tvExplanation.setVisibility(View.VISIBLE);
            } else {
                tvExplanation.setVisibility(View.GONE);
            }
        }

        Button btVolumeDefault = (Button) findViewById(R.id.btVolumeDefault);

        if (btVolumeDefault != null) {
            if (mShowVolumeDefaultButton) {
                btVolumeDefault.setVisibility(View.VISIBLE);
            } else {
                btVolumeDefault.setVisibility(View.GONE);
            }
        }

        Button btVolumeLastDay = (Button) findViewById(R.id.btVolumeLastDay);

        if (btVolumeLastDay != null) {
            if (mShowVolumeLastDayButton) {
                btVolumeLastDay.setVisibility(View.VISIBLE);
            } else {
                btVolumeLastDay.setVisibility(View.GONE);
            }
        }


    }
}