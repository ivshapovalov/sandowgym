package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.*;
import ru.brainworkout.sandow_gym.common.*;
import ru.brainworkout.sandow_gym.database.entities.Exercise;
import ru.brainworkout.sandow_gym.database.entities.Training;
import ru.brainworkout.sandow_gym.database.entities.TrainingContent;
import ru.brainworkout.sandow_gym.database.manager.DatabaseManager;
import ru.brainworkout.sandow_gym.database.manager.TableDoesNotContainElementException;


public class ActivityFileExportImport extends AppCompatActivity {

    private static final String SYMBOL_ID = "#";
    private static final String SYMBOL_WEIGHT = "$";
    private static final String SYMBOL_DEF_VOLUME = "%";
    private static final String SYMBOL_SPLIT = ";";
    private String mDateFrom;
    private String mDateTo;
    private boolean mShowSymbols = false;
    private final DatabaseManager DB = new DatabaseManager(this);

    private StringBuilder message = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);

        getIntentParams();

        updateScreen();

        if (Common.dbCurrentUser != null) {
            this.setTitle(getTitle() + "(" + Common.dbCurrentUser.getName() + ")");
        }
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        String mCurrentDate = intent.getStringExtra("CurrentDate");
        String mCurrentDateTo = intent.getStringExtra("CurrentDateTo");
        mDateFrom = mCurrentDate;
        mDateTo = mCurrentDateTo;
    }

    private List<String[]> createDataArray(List<Training> mTrainingsList,
                                           List<Exercise> mExercisesList) {

        message = new StringBuilder();
        List<String[]> data = new ArrayList<String[]>();
        StringBuilder mNewString = new StringBuilder();
        if (mShowSymbols) {
            mNewString.append("EXERCISE(" + SYMBOL_ID + "ID" + SYMBOL_DEF_VOLUME + "DEF_VOL" + ")/DATE(" + SYMBOL_ID + "ID" + ");");
        } else {
            mNewString.append("EXERCISE(" + SYMBOL_DEF_VOLUME + "DEF_VOL" + ")/DATE;");

        }
        for (Training mCurrentTraining : mTrainingsList
                ) {
            if (mShowSymbols) {
                mNewString.append(mCurrentTraining.getDayString()).append("(" + SYMBOL_ID).append(mCurrentTraining.getID())
                        .append(")").append(SYMBOL_SPLIT);
            } else {
                mNewString.append(mCurrentTraining.getDayString()).append(SYMBOL_SPLIT);
            }
            message.append(mCurrentTraining.getDayString()).append('\n');
        }
        String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
        data.add(entries);

        for (Exercise mCurrentExercise : mExercisesList
                ) {
            mNewString = new StringBuilder();
            if (mShowSymbols) {
                mNewString.append(mCurrentExercise.getName()).append("(").append(SYMBOL_ID).
                        append(String.valueOf(mCurrentExercise.getID())).append(SYMBOL_DEF_VOLUME).append(mCurrentExercise.getVolumeDefault())
                        .append(")").append(SYMBOL_SPLIT);
            } else {
                mNewString.append(mCurrentExercise.getName()).append("(").append(SYMBOL_DEF_VOLUME).append(mCurrentExercise.getVolumeDefault())
                        .append(")").append(SYMBOL_SPLIT);
            }
            for (Training mCurrentTraining : mTrainingsList
                    ) {
                try {
                    TrainingContent mCurrentTrainingContent = DB.getTrainingContent(mCurrentExercise.getID(), mCurrentTraining.getID());
                    String curVolume = mCurrentTrainingContent.getVolume();
                    if (curVolume == null || "".equals(curVolume.trim())) {
                        mNewString.append("0");
                    } else {
                        mNewString.append(curVolume);
                    }
                    if (mShowSymbols) {
                        int curWeight = mCurrentTrainingContent.getWeight();
                        mNewString.append("(").append(SYMBOL_WEIGHT).append(mCurrentTrainingContent.getWeight()).append(")");
                    }
                        mNewString.append(SYMBOL_SPLIT);


                } catch (TableDoesNotContainElementException e) {
                    if (mShowSymbols) {
                        mNewString.append("(").append(SYMBOL_WEIGHT).append(")");
                    }
                    mNewString.append(SYMBOL_SPLIT);
                }


            }
            entries = mNewString.toString().split(SYMBOL_SPLIT);
            data.add(entries);
        }
        return data;
    }

    private void writeToFile(List<String[]> data) {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())

        {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "trainings.xls");

        try

        {

            if (file.createNewFile()) {
                //System.out.println("File is created!");

            } else {
                //System.out.println("File already exists.");
            }

            Workbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("trainings");

            Row row = sheet.createRow(0);
            Cell cName;
            Font font = book.getFontAt((short) 0);
            CellStyle boldStyle = book.createCellStyle();
            boldStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            boldStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            boldStyle.setFont(font);

            cName = row.createCell(0);

            cName.setCellStyle(boldStyle);
            cName.setCellValue(data.get(0)[0]);
            cName.setCellStyle(boldStyle);

            CellStyle usualStyle = book.createCellStyle();
            usualStyle.setFont(font);
            CellStyle dateStyle = book.createCellStyle();

            dateStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            dateStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            DataFormat format = book.createDataFormat();
            dateStyle.setFont(font);

            for (int j = 1; j < data.get(0).length; j++) {
                cName = row.createCell(j);
                cName.setCellStyle(dateStyle);
                cName.setCellValue(data.get(0)[j]);
                cName.setCellStyle(dateStyle);
            }

            for (int i = 1; i < data.size(); i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < data.get(i).length; j++) {
                    cName = row.createCell(j);

                    if (j == 0) {
                        cName.setCellStyle(boldStyle);
                    } else {
                        cName.setCellStyle(usualStyle);
                    }
                    try {
                        cName.setCellValue(Integer.valueOf(data.get(i)[j]));
                    } catch (Exception e) {
                        cName.setCellValue((data.get(i)[j]));
                    }
                    if (j == 0) {
                        cName.setCellStyle(boldStyle);
                    } else {
                        cName.setCellStyle(usualStyle);
                    }
                }

            }
            book.getSheetAt(0).setPrintGridlines(true);
            book.write(new FileOutputStream(file));
            book.close();

            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("");
                tvPath.setText("В файл XLS по пути \n" + Environment.getExternalStorageDirectory().toString() + '\n'
                        + " успешно выгружены тренировки\n" + message.toString());
            }
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Файл не выгружен в " + Environment.getExternalStorageDirectory().toString());
            }
        }

    }

    private void readFromFile(File file) {

        List<String[]> data = new ArrayList<>();

        try

        {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet myExcelSheet = myExcelBook.getSheet("trainings");
            HSSFRow currentRow = myExcelSheet.getRow(0);

            int mColumn = 0;
            int mColumnCount = 0;

            while (true) {

                try {
                    String name = currentRow.getCell(mColumn).getStringCellValue();
                    if ("".equals(name)) {
                        mColumnCount = mColumn;
                        break;
                    }
                    mColumn++;
                } catch (Exception e) {
                    mColumnCount = mColumn;
                    break;
                }

            }
            int mRow = 0;
            int mRowCount = 0;
            mColumn = 0;
            while (true) {
                currentRow = myExcelSheet.getRow(mRow);
                try {
                    String name = currentRow.getCell(mColumn).getStringCellValue();
                    if ("".equals(name)) {

                        mRowCount = mRow;
                        break;
                    }

                    mRow++;
                } catch (Exception e) {
                    mRowCount = mRow;
                    break;
                }

            }
            StringBuilder mNewString  = new StringBuilder();
            for (mRow = 0; mRow < mRowCount; mRow++) {
                currentRow = myExcelSheet.getRow(mRow);
                if (mRow != 0) {
                    String[] entries = mNewString.toString().split(";");
                    data.add(entries);
                    mNewString = new StringBuilder();
                }
                for (mColumn = 0; mColumn < mColumnCount; mColumn++) {
                    try {
                        if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                            mNewString.append("").append(SYMBOL_SPLIT);
                        } else if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            int num = (int) currentRow.getCell(mColumn).getNumericCellValue();
                            mNewString.append(num).append(SYMBOL_SPLIT);
                        } else if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            String name = currentRow.getCell(mColumn).getStringCellValue();
                            mNewString.append(name).append(SYMBOL_SPLIT);
                        }
                    } catch (Exception e) {
                        mNewString.append(0).append(SYMBOL_SPLIT);
                    }
                }
            }
            String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
            data.add(entries);

            myExcelBook.close();

            List<Training> mTrainingsList = new ArrayList<Training>();
            List<Exercise> mExercisesList = new ArrayList<Exercise>();

            for (int i = 1; i < data.get(0).length; i++) {
                String s = data.get(0)[i];
                String day = s.substring(0, s.indexOf("("));
                String id = s.substring(s.indexOf(SYMBOL_ID) + 1, s.indexOf(")"));
                //String id = s.substring(s.indexOf(SYMBOL_ID) + 1, s.indexOf("&"));
                Training training = new Training.TrainingBuilder(Integer.valueOf(id)).addDay(day).build();
                mTrainingsList.add(training);

            }

            for (int i = 1; i < data.size(); i++) {
                String s = data.get(i)[0];
                String name =s.substring(0, s.indexOf("("));;
                //String id = s.substring(s.indexOf(SYMBOL_ID) + 1, s.indexOf(")"));
                String id = s.substring(s.indexOf(SYMBOL_ID) + 1, s.indexOf(SYMBOL_DEF_VOLUME));
                 String def_volume = s.substring(s.indexOf(SYMBOL_DEF_VOLUME) + 1, s.indexOf(")"));

                Exercise exercise = new Exercise.ExerciseBuilder(Integer.valueOf(id))
                        .addName(name)
                        .addVolumeDefault(def_volume)
                        .build();
                  mExercisesList.add(exercise);

            }

            writeDataToDB(mTrainingsList, mExercisesList, data);

        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Тренировки не загружены из " + Environment.getExternalStorageDirectory().toString() + "/trainings.xls");
            }
            e.printStackTrace();
        }

    }

    private void writeDataToDB(List<Training> mTrainingsList, List<Exercise> mExercisesList, List<String[]> data) throws Exception {

        message = new StringBuilder();
        int maxNum = DB.getTrainingContentMaxNumber();
        for (int curTrainingIndex = 0; curTrainingIndex < mTrainingsList.size(); curTrainingIndex++
                ) {
            Training curTraining = mTrainingsList.get(curTrainingIndex);
            message.append(curTraining.getDayString()).append('\n');
            Training dbTraining;
            try {
                dbTraining = DB.getTraining(curTraining.getID());
                //System.out.println("add tr:" + dbTraining.getID());
                DB.updateTraining(curTraining);
            } catch (TableDoesNotContainElementException e) {
                //System.out.println("add tr:" + curTraining.getID());
                DB.addTraining(curTraining);
            }


            for (int curExerciseIndex = 0; curExerciseIndex < mExercisesList.size(); curExerciseIndex++
                    ) {
                Exercise curExercise = mExercisesList.get(curExerciseIndex);
                Exercise dbExercise;
                try {
                    dbExercise = DB.getExercise(curExercise.getID());
                    dbExercise.setName(curExercise.getName());
                    curExercise = dbExercise;
                    //System.out.println("update ex:" + dbExercise.getID());
                    DB.updateExercise(dbExercise);

                } catch (TableDoesNotContainElementException e) {
                    curExercise.setIsActive(1);
                    // System.out.println("add ex:" + curExercise.getID());
                    DB.addExercise(curExercise);

                }

                TrainingContent trainingContent = new TrainingContent.TrainingContentBuilder(++maxNum)
                        .addExerciseId(curExercise.getID())
                        .addTrainingId(curTraining.getID())
                        .build();

                //разбираем ячейку со значениями количества и веса
                String cellValue = data.get(curExerciseIndex + 1)[curTrainingIndex + 1];
                //String volume = cellValue;
                String volume = cellValue.substring(0, cellValue.indexOf("("));
                String weight = cellValue.substring(cellValue.indexOf(SYMBOL_WEIGHT) + 1, cellValue.indexOf(")"));

                int iWeight;
                try {
                    iWeight = Integer.parseInt(weight);
                } catch (NumberFormatException e) {
                    iWeight = 0;
                }
                trainingContent.setVolume(volume);
//                trainingContent.setWeight(5);
                trainingContent.setWeight(iWeight);

                TrainingContent dbTrainingContent;
                try {
                    dbTrainingContent = DB.getTrainingContent(curExercise.getID(), curTraining.getID());
                    dbTrainingContent.setVolume(trainingContent.getVolume());
                    dbTrainingContent.setWeight(trainingContent.getWeight());
//                    System.out.println("max num:" + maxNum);
//                    System.out.println("update tc:" + dbTrainingContent.getID());
                    DB.updateTrainingContent(dbTrainingContent);
                } catch (TableDoesNotContainElementException e) {
//                    System.out.println("max num:" + maxNum);
//                    System.out.println("update tc:" + trainingContent.getID());
                    DB.addTrainingContent(trainingContent);
                }

            }

            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("");
                tvPath.setText("Из файла  \n" + Environment.getExternalStorageDirectory().toString() + "/trainings.xls" + '\n'
                        + " успешно загружены тренировки\n" + message.toString());

            }
        }
    }

    public void btClose_onClick(View view) {

        Common.blink(view);
        Intent intent = new Intent(ActivityFileExportImport.this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void tvDayFrom_onClick(View view) {

        Common.blink(view);
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        Common.blink(view);
        day_onClick(false);
    }


    private void loadFromFile() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (exportDir.exists()) {
            File file = new File(exportDir, "trainings.xls");
            if (file.exists()) {

                readFromFile(file);
            }
        }
    }

    public void btImportFromFile_onClick(View view) {

        Common.blink(view);
        loadFromFile();

    }

    public void btExportToFile_onClick(View view) {

        Common.blink(view);
        if (mDateFrom == null || "".equals(mDateFrom)) {
            mDateFrom = "0000-00-00";
        }
        if (mDateTo == null || "".equals(mDateFrom)) {
            mDateTo = "9999-99-99";
        }
        List<Training> trainingList = DB.getTrainingsByDates(mDateFrom, mDateTo);
        List<Exercise> exerciseList = DB.getExercisesByDates(mDateFrom, mDateTo);
        List<String[]> data = createDataArray(trainingList, exerciseList);
        writeToFile(data);

    }

    private void day_onClick(boolean isBeginDate) {


        Intent intent = new Intent(ActivityFileExportImport.this, ActivityCalendarView.class);
        intent.putExtra("IsBeginDate", isBeginDate);
        intent.putExtra("CurrentActivity", "ActivityFileExportImport");

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            intent.putExtra("CurrentDate", String.valueOf(tvDayFrom.getText()));
        } else {
            intent.putExtra("CurrentDate", "");
        }
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            intent.putExtra("CurrentDateTo", String.valueOf(tvDayTo.getText()));
        } else {
            intent.putExtra("CurrentDateTo", "");
        }

        startActivity(intent);

    }

    public void btDayFromClear_onClick(final View view) {

        Common.blink(view);
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }

    }

    public void btDayToClear_onClick(final View view) {

        Common.blink(view);
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            tvDayTo.setText("");
        }

    }

    private void updateScreen() {

        //Имя
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView etDayFrom = (TextView) findViewById(mDayFromID);
        if (etDayFrom != null) {
            if (mDateFrom == null || mDateFrom == "") {
                etDayFrom.setText("");
            } else {
                etDayFrom.setText(mDateFrom);
            }
        }

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView etDayTo = (TextView) findViewById(mDayToID);
        if (etDayTo != null) {
            if (mDateTo == null || mDateTo == "") {
                etDayTo.setText("");
            } else {
                etDayTo.setText(mDateTo);
            }
        }

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgShowSymbols);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbShowSymbolsYes:
                            mShowSymbols = true;
                            break;
                        case R.id.rbShowSymbolsNo:
                            mShowSymbols = false;
                            break;
                        default:
                            mShowSymbols = false;
                            break;
                    }
                }
            });
        }


    }
}

