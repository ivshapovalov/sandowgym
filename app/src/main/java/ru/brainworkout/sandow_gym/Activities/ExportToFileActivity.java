package ru.brainworkout.sandow_gym.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.sandow_gym.*;
import ru.brainworkout.sandow_gym.commons.*;
import ru.brainworkout.sandow_gym.database.*;


public class ExportToFileActivity extends AppCompatActivity {

    public static final boolean isDebug = true;
    private final String TAG = this.getClass().getSimpleName();

    private String mDateFrom;
    private String mDateTo;
    DatabaseManager db;

    StringBuilder message=new StringBuilder();
    List<Training> mTrainingsList;
    List<Exercise> mExercisesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_file);

        db = new DatabaseManager(this);

        Intent intent = getIntent();
        String mCurrentDate = intent.getStringExtra("CurrentDate");
        String mCurrentDateTo = intent.getStringExtra("CurrentDateTo");
        Boolean isBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mDateFrom = mCurrentDate;
        mDateTo = mCurrentDateTo;
        mDateFrom = "2016-05-16";
        mDateTo = "2016-05-19";

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

    private void saveToFile() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())

        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "trainings.csv");
        List<String[]> data = new ArrayList<String[]>();

        try

        {
            if (file.createNewFile()) {
                System.out.println("File is created!");

            } else {
                System.out.println("File already exists.");
            }

            CSVWriter writer = new CSVWriter(new FileWriter(file),";".charAt(0));
            StringBuilder mNewString = new StringBuilder();
            mNewString.append("Упражнение/Дата;");
            for (Training mCurrentTraining : mTrainingsList
                    ) {
                mNewString.append(mCurrentTraining.getDayString()).append(";");
                message.append(mCurrentTraining.getDayString()).append('\n');
            }
            String[] entries = mNewString.toString().split(";");
            data.add(entries);

            for (Exercise mCurrentExercise : mExercisesList
                    ) {
                mNewString = new StringBuilder();
                mNewString.append(mCurrentExercise.getName()).append("(id#").
                        append(String.valueOf(mCurrentExercise.getID()))
                        .append(")").append(";");
                for (Training mCurrentTraining : mTrainingsList
                        ) {
                    TrainingContent mCurrentTrainingContent = mCurrentTraining.getTrainingContentList().get(mExercisesList.indexOf(mCurrentExercise));
                    if (mCurrentTrainingContent.getVolume() == null) {
                        mNewString.append(";");
                    } else {
                        mNewString.append(mCurrentTrainingContent.getVolume()).append(";");
                    }


                }
                entries = mNewString.toString().split(";");
                data.add(entries);
            }

            writer.writeAll(data);

            //System.out.println("GOOD");
            writer.close();
            //System.out.println("trainings.csv " + file.getAbsolutePath());


        } catch (
                Exception e) {
        }
        //в эксель

        file = new File(exportDir, "trainings.xls");


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
            dateStyle.setDataFormat(format.getFormat("yyyy-mm-dd"));
            dateStyle.setFont(font);
            //dateStyle.setWrapText(true);
            //dateStyle.setIndention((short)2);
            for (int j = 1; j < data.get(0).length; j++) {
                cName = row.createCell(j);
                cName.setCellStyle(dateStyle);
                cName.setCellValue(Common.ConvertStringToDate(data.get(0)[j]));
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
            // Нумерация лет начинается с 1900-го
            //birthdate.setCellValue(new Date(110, 10, 10));

            // Меняем размер столбца

//            for (int j= 0; j < data.get(0).length; j++) {
//
            //sheet.autoSizeColumn(j);
//
//            }


            // Записываем всё в файл
            book.write(new FileOutputStream(file));
            //sheet.autoSizeColumn((short) 0);
            book.close();

            //System.out.println("trainings.xls " + file.getAbsolutePath());
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("В файлы (CSV и XLS) по пути \n" +Environment.getExternalStorageDirectory().toString()+'\n'
                        + " успешно выгружены тренировки\n"+ message.toString());
            }
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Файлы не выгружены в " +Environment.getExternalStorageDirectory().toString());
            }
        }

    }


    public void btExport_onClick(View view) {

       // boolean fault = false;
        //Проверим даты
        if (mDateFrom == null || "".equals(mDateFrom)) {
            mDateFrom="0000-00-00";}
        if( mDateTo == null || "".equals(mDateFrom)) {
            mDateTo = "9999-99-99";
        }

            getDataFromDB();
            saveToFile();


    }

    private void getDataFromDB() {

        mTrainingsList = db.getTrainingsByDates(mDateFrom, mDateTo);

        mExercisesList = db.getExercisesByDates(mDateFrom, mDateTo);

        for (Training mCurrentTraining : mTrainingsList
                ) {
            List<TrainingContent> mTrainingContentList = new ArrayList<>();

            for (Exercise mCurrentExercise : mExercisesList
                    ) {
                TrainingContent mCurrentTrainingContent = db.getTrainingContent(mCurrentExercise.getID(), mCurrentTraining.getID());

                if (mCurrentTrainingContent == null) {
                    mTrainingContentList.add(new TrainingContent());
                } else {
                    mTrainingContentList.add(mCurrentTrainingContent);
                }


            }
            mCurrentTraining.setTrainingContentList(mTrainingContentList);
        }

        //System.out.println("GOOD");


    }

    public void btClose_onClick(View view) {

        Intent intent = new Intent(ExportToFileActivity.this, MainActivity.class);
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

        Intent intent = new Intent(ExportToFileActivity.this, CalendarViewActivity.class);
        intent.putExtra("IsBeginDate", isBeginDate);
        intent.putExtra("CurrentActivity", "ExportToFileActivity");

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
            File file = new File(exportDir, "trainings.csv");
            if (file.exists()) {
                //грузим из файла
            }

        }


    }
}

