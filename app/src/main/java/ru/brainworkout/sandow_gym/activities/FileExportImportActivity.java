package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.opencsv.CSVWriter;

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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.*;
import ru.brainworkout.sandow_gym.commons.*;
import ru.brainworkout.sandow_gym.database.*;


public class FileExportImportActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    private String mDateFrom;
    private String mDateTo;
    DatabaseManager db;

    StringBuilder message = new StringBuilder();
    List<Training> mTrainingsList;
    List<Exercise> mExercisesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        String mCurrentDate = intent.getStringExtra("CurrentDate");
        String mCurrentDateTo = intent.getStringExtra("CurrentDateTo");
        Boolean isBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mDateFrom = mCurrentDate;
        mDateTo = mCurrentDateTo;
//        mDateFrom = "2016-05-16";
//        mDateTo = "2016-07-19";

        updateScreen();

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
    }

    private List<String[]> createDataArray() {


        message = new StringBuilder();
        List<String[]> data = new ArrayList<String[]>();
        StringBuilder mNewString = new StringBuilder();
        mNewString.append("Упражнение(#id)/Дата(#id&Вес);");
        for (Training mCurrentTraining : mTrainingsList
                ) {
            mNewString.append(mCurrentTraining.getDayString()).append("(#").append(mCurrentTraining.getID()).append("&")
                    .append(mCurrentTraining.getWeight()).append(")").append(";");
            message.append(mCurrentTraining.getDayString()).append('\n');
        }
        String[] entries = mNewString.toString().split(";");
        data.add(entries);

        for (Exercise mCurrentExercise : mExercisesList
                ) {
            mNewString = new StringBuilder();
            mNewString.append(mCurrentExercise.getName()).append("(#").
                    append(String.valueOf(mCurrentExercise.getID()))
                    .append(")").append(";");
            for (Training mCurrentTraining : mTrainingsList
                    ) {
                try {
                    TrainingContent mCurrentTrainingContent = db.getTrainingContent(mCurrentExercise.getID(), mCurrentTraining.getID());
                    if (mCurrentTrainingContent.getVolume() == null) {
                        mNewString.append(";");
                    } else {
                        mNewString.append(mCurrentTrainingContent.getVolume()).append(";");
                    }
                } catch (Exception e) {
                    mNewString.append(";");
                }


            }
            entries = mNewString.toString().split(";");
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
                System.out.println("File is created!");

            } else {
                System.out.println("File already exists.");
            }

            Workbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("trainings");
            // sheet.autoSizeColumn(0);

            // Нумерация начинается с нуля
            Row row = sheet.createRow(0);
            Cell cName;
            Font font = book.getFontAt((short) 0);
            //font.setFontName("Arial");

            CellStyle boldStyle = book.createCellStyle();
            boldStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            boldStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            boldStyle.setFont(font);
            //boldStyle.setWrapText(true);
            //boldStyle.setIndention((short)2);

            cName = row.createCell(0);

            cName.setCellStyle(boldStyle);
            cName.setCellValue(data.get(0)[0]);
            cName.setCellStyle(boldStyle);

            CellStyle usualStyle = book.createCellStyle();
            usualStyle.setFont(font);
            //usualStyle.setWrapText(true);
            //usualStyle.setIndention((short)2);

            CellStyle dateStyle = book.createCellStyle();

            dateStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            dateStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            DataFormat format = book.createDataFormat();
            //dateStyle.setDataFormat(format.getFormat("yyyy-mm-dd"));
            dateStyle.setFont(font);
            //dateStyle.setWrapText(true);
            //dateStyle.setIndention((short)2);
            for (int j = 1; j < data.get(0).length; j++) {
                cName = row.createCell(j);
                cName.setCellStyle(dateStyle);
                cName.setCellValue(data.get(0)[j]);
                //data.get(0)[j].substring(0,data.get(0)[j].indexOf("#")-1)+data.get(0)[j].substring(data.get(0)[j].indexOf("#")+1,data.get(0)[j].indexOf("#")+2)
                cName.setCellStyle(dateStyle);
            }

            for (int i = 1; i < data.size(); i++) {
                row = sheet.createRow(i);
                //sheet.autoSizeColumn(i);
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
                    //System.out.println("Строка -" +String.valueOf(i)+": Колонка -"+String.valueOf(j)+ " : Текст - "+(String)(data.get(i)[j]));
                }
                //System.out.println("Строка +" +String.valueOf(i));

            }
            book.getSheetAt(0).setPrintGridlines(true);
            // Записываем всё в файл
            book.write(new FileOutputStream(file));
            book.close();

            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("");
                tvPath.setText("В файлы (CSV и XLS) по пути \n" + Environment.getExternalStorageDirectory().toString() + '\n'
                        + " успешно выгружены тренировки\n" + message.toString());
            }
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Файлы не выгружены в " + Environment.getExternalStorageDirectory().toString());
            }
        }

    }

    private void readFromFile(File file) {


        List<String[]> data = new ArrayList<String[]>();

        try

        {

            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet myExcelSheet = myExcelBook.getSheet("trainings");
            HSSFRow currentRow = myExcelSheet.getRow(0);

            StringBuilder mNewString = new StringBuilder();

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
            mNewString = new StringBuilder();
            for (mRow = 0; mRow < mRowCount; mRow++) {
                currentRow = myExcelSheet.getRow(mRow);
                if (mRow != 0) {
                    String[] entries = mNewString.toString().split(";");
                    data.add(entries);
                    mNewString = new StringBuilder();
                }
                for (mColumn = 0; mColumn < mColumnCount; mColumn++) {
                    try {
                        if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            int num = (int) currentRow.getCell(mColumn).getNumericCellValue();
                            mNewString.append(num).append(";");
                        } else if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            String name = currentRow.getCell(mColumn).getStringCellValue();
                            if ("".equals(name)) {

                                mColumn = 0;
                                break;
                            }
                            mNewString.append(name).append(";");
                        }
                    } catch (Exception e) {
                        mNewString.append(0).append(";");
                    }
                }
            }
            String[] entries = mNewString.toString().split(";");
            data.add(entries);


            myExcelBook.close();
            //create
            mTrainingsList = new ArrayList<Training>();
            mExercisesList = new ArrayList<Exercise>();

            for (int i = 1; i < data.get(0).length; i++) {
                String s = data.get(0)[i];
                String day = s.substring(0, s.indexOf("("));
                String id = s.substring(s.indexOf("#") + 1, s.indexOf("&"));
                String weight = s.substring(s.indexOf("&") + 1, s.indexOf(")"));
                Training training = new Training();
                training.setID(Integer.valueOf(id));
                training.setWeight(Integer.valueOf(weight));
                training.setDayString(day);
                mTrainingsList.add(training);

            }

            for (int i = 1; i < data.size(); i++) {
                String s = data.get(i)[0];
                String name = s.substring(0, s.indexOf("("));
                String id = s.substring(s.indexOf("#") + 1, s.indexOf(")"));
                Exercise exercise = new Exercise();
                exercise.setID(Integer.valueOf(id));
                exercise.setName(name);

                mExercisesList.add(exercise);

            }

            message = new StringBuilder();
            for (int curTrainingIndex = 0; curTrainingIndex < mTrainingsList.size(); curTrainingIndex++
                    ) {
                Training curTraining = mTrainingsList.get(curTrainingIndex);
                message.append(curTraining.getDayString()).append('\n');
                Training dbTraining;
                try {
                    dbTraining = db.getTraining(curTraining.getID());
                    db.updateTraining(curTraining);
                } catch (IndexOutOfBoundsException e) {
                    db.addTraining(curTraining);
                }
                TrainingContent trainingContent = new TrainingContent();
                int maxNum = db.getTrainingContentCount();
                for (int curExerciseIndex = 0; curExerciseIndex < mExercisesList.size(); curExerciseIndex++
                        ) {
                    Exercise curExercise = mExercisesList.get(curExerciseIndex);
                    Exercise dbExercise;
                    try {
                        dbExercise = db.getExercise(curExercise.getID());
                        dbExercise.setName(curExercise.getName());
                        curExercise = dbExercise;
                        db.updateExercise(dbExercise);
                    } catch (IndexOutOfBoundsException e) {
                        curExercise.setIsActive(1);
                        db.addExercise(curExercise);
                    }


                    trainingContent.setID(++maxNum);
                    trainingContent.setIdExercise(curExercise.getID());
                    trainingContent.setIdTraining(curTraining.getID());
                    trainingContent.setVolume(data.get(curExerciseIndex + 1)[curTrainingIndex + 1]);

                    TrainingContent baseTrainingContent;
                    try {
                        baseTrainingContent = db.getTrainingContent(curExercise.getID(), curTraining.getID());
                        baseTrainingContent.setVolume(trainingContent.getVolume());
                        db.updateTrainingContent(baseTrainingContent);
                    } catch (Exception e) {
                        db.addTrainingContent(trainingContent);
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
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Тренировки не загружены из " + Environment.getExternalStorageDirectory().toString() + "/trainings.xls");
            }
        }


    }


    public void btExportToFile_onClick(View view) {

        // boolean fault = false;
        //Проверим даты
        if (mDateFrom == null || "".equals(mDateFrom)) {
            mDateFrom = "0000-00-00";
        }
        if (mDateTo == null || "".equals(mDateFrom)) {
            mDateTo = "9999-99-99";
        }

        readDataFromDB();
        List<String[]> data = createDataArray();
        writeToFile(data);

    }

    private void readDataFromDB() {

        mTrainingsList = db.getTrainingsByDates(mDateFrom, mDateTo);
        mExercisesList = db.getExercisesByDates(mDateFrom, mDateTo);

    }

    public void btClose_onClick(View view) {

        Intent intent = new Intent(FileExportImportActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void tvDayFrom_onClick(View view) {
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        day_onClick(false);
    }

    private void day_onClick(boolean isBeginDate) {

        Intent intent = new Intent(FileExportImportActivity.this, CalendarViewActivity.class);
        intent.putExtra("IsBeginDate", isBeginDate);
        intent.putExtra("CurrentActivity", "FileExportImportActivity");

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

    public void btImportFromFile_onClick(View view) {

        loadFromFile();
    }

    private void loadFromFile() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (exportDir.exists()) {
            File file = new File(exportDir, "trainings.xls");
            if (file.exists()) {

                readFromFile(file);
                //writeDataToDB();
            }

        }


    }

    public void btDayFromClear_onClick(View view) {

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }

    }

    public void btDayToClear_onClick(View view) {

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            tvDayTo.setText("");
        }
    }
}

