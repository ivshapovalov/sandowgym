package ru.brainworkout.sandowgym.activities;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.brainworkout.sandowgym.*;

import static ru.brainworkout.sandowgym.common.Common.*;

import ru.brainworkout.sandowgym.common.TypeOfView;
import ru.brainworkout.sandowgym.database.entities.Exercise;
import ru.brainworkout.sandowgym.database.entities.Training;
import ru.brainworkout.sandowgym.database.entities.TrainingContent;
import ru.brainworkout.sandowgym.database.manager.DatabaseManager;
import ru.brainworkout.sandowgym.database.manager.TableDoesNotContainElementException;


public class ActivityFileExportImport extends AppCompatActivity {

    private static final String SYMBOL_ID = "#";
    private static final String SYMBOL_WEIGHT = "$";
    private static final String SYMBOL_DEF_VOLUME = "%";
    private static final String SYMBOL_SPLIT = ";";

    private static List<String> specialSymbols = new ArrayList<>();

    static {

        specialSymbols.add(SYMBOL_ID);
        specialSymbols.add(SYMBOL_WEIGHT);
        specialSymbols.add(SYMBOL_DEF_VOLUME);
        specialSymbols.add(SYMBOL_SPLIT);
        specialSymbols.add(")");
    }

    private String mDateFrom;
    private String mDateTo;
    private boolean mFullView = false;
    private final DatabaseManager DB = new DatabaseManager(this);

    private StringBuilder messageTrainingList = new StringBuilder();
    private StringBuilder messageErrors = new StringBuilder();

    private List<Training> trainingsList = new ArrayList<>();
    private List<Exercise> exercisesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);

        getIntentParams();

        updateScreen();

        setTitleOfActivity(this);
    }


    private void getIntentParams() {
        Intent intent = getIntent();
        String mCurrentDate = intent.getStringExtra("CurrentDate");
        String mCurrentDateTo = intent.getStringExtra("CurrentDateTo");
        mDateFrom = mCurrentDate;
        mDateTo = mCurrentDateTo;
    }

    private List<String[]> createDataArray(TypeOfView type) {

        messageTrainingList = new StringBuilder();
        int countTrainings = 1;
        List<String[]> data = new ArrayList<String[]>();
        StringBuilder mNewString = new StringBuilder();
        switch (type) {
            case FULL:
                mNewString.append("EXERCISE(" + SYMBOL_ID + "ID" + SYMBOL_DEF_VOLUME + "DEF_VOL" + ")/DATE(" + SYMBOL_ID + "ID" + ");");
                break;
            default:
                mNewString.append("EXERCISE(" + SYMBOL_DEF_VOLUME + "DEF_VOL" + ")/DATE;");
                break;
        }

        for (Training mCurrentTraining : trainingsList
                ) {
            switch (type) {
                case FULL:
                    mNewString.append(mCurrentTraining.getDayString()).append("(" + SYMBOL_ID).append(mCurrentTraining.getID())
                            .append(")").append(SYMBOL_SPLIT);
                    break;
                default:
                    mNewString.append(mCurrentTraining.getDayString()).append(SYMBOL_SPLIT);
                    break;

            }

            messageTrainingList.append(countTrainings++).append(") ").append(mCurrentTraining.getDayString()).append('\n');
        }
        String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
        data.add(entries);

        for (Exercise mCurrentExercise : exercisesList
                ) {
            mNewString = new StringBuilder();

            switch (type) {
                case FULL:
                    mNewString.append(mCurrentExercise.getName()).append("(").append(SYMBOL_ID).
                            append(String.valueOf(mCurrentExercise.getID())).append(SYMBOL_DEF_VOLUME).append(mCurrentExercise.getVolumeDefault())
                            .append(")").append(SYMBOL_SPLIT);
                    break;
                default:
                    mNewString.append(mCurrentExercise.getName()).append("(").append(SYMBOL_DEF_VOLUME).append(mCurrentExercise.getVolumeDefault())
                            .append(")").append(SYMBOL_SPLIT);
                    break;

            }

            for (Training mCurrentTraining : trainingsList
                    ) {
                try {
                    TrainingContent mCurrentTrainingContent = DB.getTrainingContent(mCurrentExercise.getID(), mCurrentTraining.getID());
                    String curVolume = mCurrentTrainingContent.getVolume();
                    if (curVolume == null || "".equals(curVolume.trim())) {
                        mNewString.append("0");
                    } else {
                        mNewString.append(curVolume);
                    }
                    switch (type) {
                        case FULL:
                            int curWeight = mCurrentTrainingContent.getWeight();
                            mNewString.append("(").append(SYMBOL_WEIGHT).append(mCurrentTrainingContent.getWeight()).append(")");
                            break;
                        case SHORT_WITH_WEIGHTS:
                            mNewString.append("(").append(mCurrentTrainingContent.getWeight()).append(")");
                            break;
                        default:

                            break;

                    }

                    mNewString.append(SYMBOL_SPLIT);


                } catch (TableDoesNotContainElementException e) {
                    switch (type) {
                        case FULL:
                            mNewString.append("(").append(SYMBOL_WEIGHT).append(")");
                            break;

                        default:

                            break;

                    }
                    mNewString.append(SYMBOL_SPLIT);
                }


            }
            entries = mNewString.toString().split(SYMBOL_SPLIT);
            data.add(entries);
        }
        return data;
    }

    private void writeToFile(Map<TypeOfView, List<String[]>> dataSheets) {

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

            for (Map.Entry<TypeOfView, List<String[]>> dataSheet : dataSheets.entrySet()) {
                addSheetWithData(dataSheet.getValue(), book, dataSheet.getKey().getName());
            }

            Row row;
            Cell cName;
            Sheet sheet2 = book.createSheet("legend");
            FillLegendSheet(sheet2);


            book.write(new FileOutputStream(file));
            book.close();

            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("");
                tvPath.setText("В файл XLS по пути \n" + Environment.getExternalStorageDirectory().toString() + '\n'
                        + " успешно выгружены тренировки\n" + messageTrainingList.toString());
            }
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Файл не выгружен в " + Environment.getExternalStorageDirectory().toString());
            }
        }

    }

    private void FillLegendSheet(Sheet sheetLegend) {

        int rowCount=0;

        Row row;
        Cell cell;
        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("Подробное описание");


        rowCount++;
        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("Все специальные символы должны располагаться в круглых скобках");


        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("#");
        cell = row.createCell(1);
        cell.setCellValue("Обозначение для ID. Пример \"Упражнение номер 1 (#10)\" - будет загружено упражнение с ID 10 и именем \"Упражнение номер 1\".\n" +
                " По ID происходит поиск в базе данных. Если тренировка или упражнение не найдены - создаются новые.");

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("$");
        cell = row.createCell(1);
        cell.setCellValue("Обозначение для веса гантель. Вес может быть указан как для всей тренировки (\"2016-07-04(#10$5)\" -\n будет загружена тренировка с ID 10 и весами во всех упражнения - 5)" +
                " или для каждого упражнения отдельно 20($5)");

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("%");
        cell = row.createCell(1);
        cell.setCellValue("Обозначение для веса количества по умолчанию. Пример \"Упражнение номер один(#10%19)\" - \n будет загружено упражнение с ID 10 и количеством по умолчанию 19");

        rowCount++;

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("YYYY-MM-DD");
        cell = row.createCell(1);
        cell.setCellValue("Формат даты в тренировках. Пример 2016-08-25");


    }

    private void addSheetWithData(List<String[]> data, Workbook book, String sheetName) {
        Sheet sheet = book.createSheet(sheetName);
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
    }

    private void readFromFile(File file) {

        List<String[]> data = new ArrayList<>();

        try

        {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet myExcelSheet = myExcelBook.getSheet("trainings_full");
            if (myExcelSheet == null) {
                int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
                TextView tvPath = (TextView) findViewById(mPath);
                StringBuilder errorMessage = new StringBuilder();
                if (tvPath != null) {
                    errorMessage.append("Missed sheet - \"trainings_full\"").append("\n")
                            .append("Try to load data from sheet \"trainings\"").append("\n");
                    myExcelSheet = myExcelBook.getSheet("trainings");
                    if (myExcelSheet == null) {
                        errorMessage.append("Missed sheet - \"trainings\"").append("\n")
                                .append("Trainings didn't load from " + Environment.getExternalStorageDirectory().toString() + "/trainings.xls");
                    }
                    tvPath.setText(errorMessage.toString());
                }

            }
            data = ReadDataFromSheet(myExcelSheet);
            myExcelBook.close();

            writeDataToDB(data);

        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Trainings didn't load from " + Environment.getExternalStorageDirectory().toString() + "/trainings.xls");
            }
            e.printStackTrace();
        }

    }

    private List<String[]> ReadDataFromSheet(HSSFSheet myExcelSheet) {

        List<String[]> data = new ArrayList<>();
        ;
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
        StringBuilder mNewString = new StringBuilder();
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
        return data;
    }

    private void writeDataToDB(List<String[]> data) throws Exception {

        trainingsList = new ArrayList<Training>();
        exercisesList = new ArrayList<Exercise>();
        List<String> trainingWeights = new ArrayList<>();

        int trainingsCount = 1;

        for (int i = 1; i < data.get(0).length; i++) {
            String s = data.get(0)[i];
            String day;
            if (s.indexOf("(") != -1) {
                day = s.substring(0, s.indexOf("("));
            } else {
                day = s.substring(0);
            }

            String id;
            int indexSymbolID = s.indexOf(SYMBOL_ID);
            if (indexSymbolID != -1) {
                id = textBeforeNextSpecialSymbol(s, indexSymbolID);
            } else {
                id = "";
            }

            String weightOfAllTraining;
            int indexSymbolWeight = s.indexOf(SYMBOL_WEIGHT);
            if (indexSymbolWeight != -1) {
                weightOfAllTraining = textBeforeNextSpecialSymbol(s, indexSymbolWeight);

            } else {
                weightOfAllTraining = "";
            }
            trainingWeights.add(weightOfAllTraining);

            Training training;
            if (!"".equals(id)) {
                training = new Training.Builder(Integer.valueOf(id)).addDay(day).build();
            } else {
                training = new Training.Builder(DB.getTrainingMaxNumber() + trainingsCount++).addDay(day).build();
            }
            trainingsList.add(training);

        }

        int exercisesCount = 1;
        for (int i = 1; i < data.size(); i++) {
            String s = data.get(i)[0];
            String name = s.substring(0, s.indexOf("("));

            String id;
            int indexSymbolID = s.indexOf(SYMBOL_ID);
            if (indexSymbolID != -1) {
                id = textBeforeNextSpecialSymbol(s, indexSymbolID);
            } else {
                id = "";
            }

            String def_volume;
            int indexSymbolDefaultVolume = s.indexOf(SYMBOL_DEF_VOLUME);
            if (indexSymbolDefaultVolume != -1) {
                def_volume = textBeforeNextSpecialSymbol(s, indexSymbolDefaultVolume);
            } else {
                def_volume = "";
            }

            Exercise exercise;
            if (!"".equals(id)) {
                exercise = new Exercise.Builder(Integer.valueOf(id))
                        .addName(name)
                        .addVolumeDefault(def_volume)
                        .build();

            } else {
                exercise = new Exercise.Builder(DB.getExerciseMaxNumber() + exercisesCount++)
                        .addName(name)
                        .addVolumeDefault(def_volume)
                        .build();
            }
            exercisesList.add(exercise);

        }

        messageTrainingList = new StringBuilder();
        int maxNum = DB.getTrainingContentMaxNumber();
        for (int curTrainingIndex = 0; curTrainingIndex < trainingsList.size(); curTrainingIndex++
                ) {
            Training curTraining = trainingsList.get(curTrainingIndex);
            messageTrainingList.append(curTraining.getDayString()).append('\n');
            Training dbTraining;
            try {
                dbTraining = DB.getTraining(curTraining.getID());
                DB.updateTraining(curTraining);
            } catch (TableDoesNotContainElementException e) {
                DB.addTraining(curTraining);
            }


            for (int curExerciseIndex = 0; curExerciseIndex < exercisesList.size(); curExerciseIndex++
                    ) {
                Exercise curExercise = exercisesList.get(curExerciseIndex);
                Exercise dbExercise;
                try {
                    dbExercise = DB.getExercise(curExercise.getID());
                    dbExercise.setName(curExercise.getName());
                    curExercise = dbExercise;
                    DB.updateExercise(dbExercise);

                } catch (TableDoesNotContainElementException e) {
                    curExercise.setIsActive(1);
                    DB.addExercise(curExercise);

                }

                TrainingContent trainingContent = new TrainingContent.Builder(++maxNum)
                        .addExerciseId(curExercise.getID())
                        .addTrainingId(curTraining.getID())
                        .build();

                //разбираем ячейку со значениями количества и веса
                String cellValue = data.get(curExerciseIndex + 1)[curTrainingIndex + 1];
                //String volume = cellValue;

                String volume;
                int indexSymbolBrackets = cellValue.indexOf("(");
                if (indexSymbolBrackets != -1) {
                    volume = cellValue.substring(0, indexSymbolBrackets);
                } else {
                    volume = cellValue.substring(0);
                }

                String weight;
                int indexSymbolWeight = cellValue.indexOf(SYMBOL_WEIGHT);
                if (indexSymbolWeight != -1) {

                    weight = textBeforeNextSpecialSymbol(cellValue, indexSymbolWeight);

                } else {
                    //check common weight of training
                    weight = trainingWeights.get(curTrainingIndex);

                }

                int iWeight;
                try {
                    iWeight = Integer.parseInt(weight);
                } catch (NumberFormatException e) {
                    iWeight = 0;
                }
                trainingContent.setWeight(iWeight);

                trainingContent.setVolume(volume);

                TrainingContent dbTrainingContent;
                try {
                    dbTrainingContent = DB.getTrainingContent(curExercise.getID(), curTraining.getID());
                    dbTrainingContent.setVolume(trainingContent.getVolume());
                    dbTrainingContent.setWeight(trainingContent.getWeight());
                    DB.updateTrainingContent(dbTrainingContent);
                } catch (TableDoesNotContainElementException e) {
                    DB.addTrainingContent(trainingContent);
                }

            }

        }
        int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
        TextView tvPath = (TextView) findViewById(mPath);
        if (tvPath != null) {

            messageTrainingList.insert(0,"From file  \n" + Environment.getExternalStorageDirectory().toString() + "/trainings.xls" + '\n'
                            + " successfully loaded trainings:"+"\n")
            .insert(0, tvPath.getText().toString());
            tvPath.setText("");
            tvPath.setText(messageTrainingList);

        }
    }

    private String textBeforeNextSpecialSymbol(String s, int currentPosition) {

        List<Integer> positions = new ArrayList<>();

        for (String symbol : specialSymbols
                ) {
            int position = s.indexOf(symbol, currentPosition + 1);
            if (position != -1) {
                positions.add(position);
            }

        }
        Collections.sort(positions);

        if (!positions.isEmpty()) {
            return s.substring(currentPosition + 1, positions.get(0));
        } else {
            return s.substring(currentPosition + 1);
        }

    }

    public void btClose_onClick(View view) {

        blink(view);
        Intent intent = new Intent(ActivityFileExportImport.this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void tvDayFrom_onClick(View view) {

        blink(view);
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        blink(view);
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

        blink(view);
        loadFromFile();

    }

    public void btExportToFile_onClick(View view) {

        blink(view);
        if (mDateFrom == null || "".equals(mDateFrom)) {
            mDateFrom = "0000-00-00";
        }
        if (mDateTo == null || "".equals(mDateFrom)) {
            mDateTo = "9999-99-99";
        }
        trainingsList = new ArrayList<>();
        exercisesList = new ArrayList<>();
        trainingsList = DB.getTrainingsByDates(mDateFrom, mDateTo);
        exercisesList = DB.getExercisesByDates(mDateFrom, mDateTo);

        Map<TypeOfView, List<String[]>> dataSheets = new HashMap<>();
        if (mFullView) {
            dataSheets.put(TypeOfView.SHORT, createDataArray(TypeOfView.SHORT));
            dataSheets.put(TypeOfView.FULL, createDataArray(TypeOfView.FULL));
            dataSheets.put(TypeOfView.SHORT_WITH_WEIGHTS, createDataArray(TypeOfView.SHORT_WITH_WEIGHTS));
        } else {
            dataSheets.put(TypeOfView.SHORT, createDataArray(TypeOfView.SHORT));
        }

        writeToFile(dataSheets);

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

        blink(view);
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }

    }

    public void btDayToClear_onClick(final View view) {

        blink(view);
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

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgFullView);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbFullViewYes:
                            mFullView = true;
                            break;
                        case R.id.rbFullViewNo:
                            mFullView = false;
                            break;
                        default:
                            mFullView = false;
                            break;
                    }
                }
            });
        }


    }


}

