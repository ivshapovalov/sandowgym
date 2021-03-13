package ru.ivan.sandowgym.common;

import android.content.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.sandowgym.database.entities.Exercise;
import ru.ivan.sandowgym.database.entities.Training;
import ru.ivan.sandowgym.database.entities.TrainingContent;
import ru.ivan.sandowgym.database.entities.WeightChangeCalendar;
import ru.ivan.sandowgym.database.manager.SQLiteDatabaseManager;
import ru.ivan.sandowgym.database.manager.TableDoesNotContainElementException;

import static ru.ivan.sandowgym.common.Common.convertStringToDate;
import static ru.ivan.sandowgym.common.Common.convertTextToDigit;
import static ru.ivan.sandowgym.common.Common.getBackupFolder;
import static ru.ivan.sandowgym.common.Constants.dbCurrentUser;

public class FileExportImport {
    private Context context;
    private long mDateFrom;
    private long mDateTo;
    private File file;
    private final SQLiteDatabaseManager DB;

    private List<Training> trainingsList = new ArrayList<>();
    private List<Exercise> exercisesList = new ArrayList<>();
    private List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
    private StringBuilder messageTrainingList = new StringBuilder();
    private List<String> specialSymbols = new ArrayList<>();
    private final String SYMBOL_ID = "#";
    private final String SYMBOL_WEIGHT = "$";
    private final String SYMBOL_DEF_AMOUNT = "%";
    private final String SYMBOL_SPLIT = ";";
    private final File BACKUP_FOLDER = getBackupFolder();

    {
        specialSymbols.add(SYMBOL_ID);
        specialSymbols.add(SYMBOL_WEIGHT);
        specialSymbols.add(SYMBOL_DEF_AMOUNT);
        specialSymbols.add(SYMBOL_SPLIT);
        specialSymbols.add(")");
    }

    public FileExportImport(Context context, File file, long mDateFrom, long mDateTo) {
        this.context = context;
        this.mDateFrom = mDateFrom;
        this.mDateTo = mDateTo;
        this.file = file;
        DB = new SQLiteDatabaseManager(context);

    }

    public File exportToFile() throws IOException {
        if (mDateFrom == 0) {
            mDateFrom = Long.MIN_VALUE;
        }
        if (mDateTo == 0) {
            mDateTo = Long.MAX_VALUE;
        }

        if (dbCurrentUser != null) {
            trainingsList = DB.getTrainingsOfUserByDates(dbCurrentUser.getId(), mDateFrom, mDateTo);
            exercisesList = DB.getExercisesOfUserByDates(dbCurrentUser.getId(), mDateFrom, mDateTo);
            weightChangeCalendarList = DB.getAllWeightChangeCalendarOfUser(dbCurrentUser.getId());
        } else {

        }

        Map<TypeOfView, List<String[]>> dataSheets = new HashMap<>();
        dataSheets.put(TypeOfView.SHORT, createDataArray(TypeOfView.SHORT));
        dataSheets.put(TypeOfView.FULL, createDataArray(TypeOfView.FULL));
        dataSheets.put(TypeOfView.SHORT_WITH_WEIGHTS, createDataArray(TypeOfView.SHORT_WITH_WEIGHTS));
        dataSheets.put(TypeOfView.WEIGHT_CALENDAR, createWeightChangeCalendarArray());

        return writeToFile(file, dataSheets);
    }

    private List<String[]> createDataArray(TypeOfView type) {

        messageTrainingList = new StringBuilder();
        int countTrainings = 1;
        List<String[]> data = new ArrayList<>();
        StringBuilder mNewString = new StringBuilder();
        switch (type) {
            case FULL:
                mNewString.append("EXERCISE(" + SYMBOL_ID + "ID" + SYMBOL_DEF_AMOUNT + "DEF_AMOUNT" + ")/DATE(" + SYMBOL_ID + "ID" + ");");
                break;
            default:
                mNewString.append("EXERCISE(" + SYMBOL_DEF_AMOUNT + "DEF_AMOUNT" + ")/DATE;");
                break;
        }

        for (Training mCurrentTraining : trainingsList) {
            switch (type) {
                case FULL:
                    mNewString
                            .append(mCurrentTraining.getDayString())
                            .append("(" + SYMBOL_ID)
                            .append(mCurrentTraining.getId())
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

        for (Exercise mCurrentExercise : exercisesList) {
            mNewString = new StringBuilder();
            switch (type) {
                case FULL:
                    mNewString
                            .append(mCurrentExercise.getName())
                            .append("(").append(SYMBOL_ID)
                            .append(String.valueOf(mCurrentExercise.getId()))
                            .append(SYMBOL_DEF_AMOUNT)
                            .append(mCurrentExercise.getAmountDefault())
                            .append(")")
                            .append(SYMBOL_SPLIT);
                    break;
                default:
                    mNewString
                            .append(mCurrentExercise.getName())
                            .append("(")
                            .append(SYMBOL_DEF_AMOUNT)
                            .append(mCurrentExercise.getAmountDefault())
                            .append(")")
                            .append(SYMBOL_SPLIT);
                    break;

            }

            for (Training mCurrentTraining : trainingsList) {
                try {
                    TrainingContent mCurrentTrainingContent = DB.getTrainingContent(mCurrentExercise.getId(), mCurrentTraining.getId());
                    int curAmount = mCurrentTrainingContent.getAmount();
                    if (curAmount == 0) {
                        mNewString.append("0");
                    } else {
                        mNewString.append(curAmount);
                    }
                    switch (type) {
                        case FULL:
                            int curWeight = mCurrentTrainingContent.getWeight();
                            mNewString.append("(").append(SYMBOL_WEIGHT).append(curWeight).append(")");
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

    private List<String[]> createWeightChangeCalendarArray() {

        List<String[]> data = new ArrayList<>();
        StringBuilder mNewString = new StringBuilder();

        mNewString
                .append("ID").append(SYMBOL_SPLIT)
                .append("Date").append(SYMBOL_SPLIT)
                .append("Weight").append(SYMBOL_SPLIT);
        String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
        data.add(entries);
        for (WeightChangeCalendar mWeightChangeCalendar : weightChangeCalendarList
        ) {

            mNewString = new StringBuilder();
            mNewString
                    .append(mWeightChangeCalendar.getId()).append(SYMBOL_SPLIT)
                    .append(mWeightChangeCalendar.getDayString()).append(SYMBOL_SPLIT)
                    .append(mWeightChangeCalendar.getWeight()).append(SYMBOL_SPLIT);
            entries = mNewString.toString().split(SYMBOL_SPLIT);
            data.add(entries);

        }
        return data;
    }

    private File writeToFile(File file, Map<TypeOfView, List<String[]>> dataSheets) throws IOException {

//        System.setProperty("javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        Workbook book = new XSSFWorkbook();
        for (Map.Entry<TypeOfView, List<String[]>> dataSheet : dataSheets.entrySet()) {
            addSheetWithData(dataSheet.getValue(), book, dataSheet.getKey().getName());
        }
        Sheet sheetLegend = book.createSheet("legend");
        FillLegendSheet(sheetLegend);
        book.write(new FileOutputStream(file));

        return file;

    }

    private void FillLegendSheet(Sheet sheetLegend) {

        int rowCount = 0;

        Row row;
        Cell cell;
        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("Detail description");

        rowCount++;
        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("All special symbols must be in ()");

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("#");
        cell = row.createCell(1);
        cell.setCellValue("ID. Example 'Exercise 10(#10)' - exercise with ID = '10' and name 'Exercise 10' \n" +
                " Searching in database works by ID. If training or exercise didn't found - it will be created ");

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("$");
        cell = row.createCell(1);
        cell.setCellValue("Weight of barbel. Weight can be for the whole training  ('2016-07-04(#10$5)' -" +
                "\n training with ID = '10' and weight in all exercises - '5')" +
                " or for every exercise '20($5)'");

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("%");
        cell = row.createCell(1);
        cell.setCellValue("Default weight of barbell. Example 'Exercise 1(#10%19)' - \n " +
                "Exercise 1 with с ID = '10' default weight '19'");

        rowCount++;

        row = sheetLegend.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("YYYY-MM-DD");
        cell = row.createCell(1);
        cell.setCellValue("Date format in trainings. Example 2016-08-25");
    }

    private void addSheetWithData(List<String[]> data, Workbook book, String sheetName) {
        Sheet sheet = book.createSheet(sheetName);
        Row row = sheet.createRow(0);
        Cell cName;
        Font font = book.getFontAt((short) 0);
        CellStyle boldStyle = book.createCellStyle();
        boldStyle.setFont(font);

        cName = row.createCell(0);

        cName.setCellStyle(boldStyle);
        cName.setCellValue(data.get(0)[0]);
        cName.setCellStyle(boldStyle);

        CellStyle usualStyle = book.createCellStyle();
        usualStyle.setFont(font);
        CellStyle dateStyle = book.createCellStyle();

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

    private String writeDataToDB(List<String[]> data) throws Exception {

        trainingsList = new ArrayList<>();
        exercisesList = new ArrayList<>();
        List<String> trainingWeights = new ArrayList<>();

        int trainingsCount = 1;

        for (int i = 1; i < data.get(0).length; i++) {
            String s = data.get(0)[i];
            String day;
            if (s.contains("(")) {
                day = s.substring(0, s.indexOf("("));
            } else {
                day = s;
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
                training = new Training
                        .Builder(Integer.valueOf(id))
                        .addDay(convertStringToDate(day).getTime())
                        .build();
            } else {
                training = new Training
                        .Builder(DB.getTrainingMaxNumber() + trainingsCount++)
                        .addDay(convertStringToDate(day).getTime())
                        .build();
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

            String def_amount;
            int indexSymbolDefaultAmount = s.indexOf(SYMBOL_DEF_AMOUNT);
            if (indexSymbolDefaultAmount != -1) {
                def_amount = textBeforeNextSpecialSymbol(s, indexSymbolDefaultAmount);
            } else {
                def_amount = "";
            }

            Exercise exercise;
            if (!"".equals(id)) {
                exercise = new Exercise.Builder(Integer.valueOf(id))
                        .addName(name)
                        .addAmountDefault(convertTextToDigit(def_amount))
                        .build();

            } else {
                exercise = new Exercise.Builder(DB.getExerciseMaxNumber() + exercisesCount++)
                        .addName(name)
                        .addAmountDefault(convertTextToDigit(def_amount))
                        .build();
            }
            exercisesList.add(exercise);
        }

        messageTrainingList = new StringBuilder();
        int maxNum = DB.getTrainingContentMaxNumber();
        for (int curTrainingIndex = 0; curTrainingIndex < trainingsList.size(); curTrainingIndex++) {
            //System.out.println("Handle training " +curTrainingIndex);
            Training curTraining = trainingsList.get(curTrainingIndex);
            messageTrainingList.append(curTraining.getDayString()).append('\n');
            if (DB.containsTraining(curTraining.getId())) {
                DB.updateTraining(curTraining);
            } else {
                DB.addTraining(curTraining);
            }
            //System.out.println("After save training " +curTraining.getId());

            for (int curExerciseIndex = 0; curExerciseIndex < exercisesList.size(); curExerciseIndex++) {
                Exercise curExercise = exercisesList.get(curExerciseIndex);
                Exercise dbExercise;
                if (DB.containsExercise(curExercise.getId())) {
                    dbExercise = DB.getExercise(curExercise.getId());
                    dbExercise.setName(curExercise.getName());
                    curExercise = dbExercise;
                    DB.updateExercise(dbExercise);
                } else {
                    curExercise.setIsActive(1);
                    DB.addExercise(curExercise);
                }
                //System.out.println("After save exercise " +curExercise.getId());

                TrainingContent trainingContent = new TrainingContent.Builder(++maxNum)
                        .addExercise(curExercise)
                        .addTraining(curTraining)
                        .build();

                String cellValue = data.get(curExerciseIndex + 1)[curTrainingIndex + 1];

                String amount;
                int indexSymbolBrackets = cellValue.indexOf("(");
                if (indexSymbolBrackets != -1) {
                    amount = cellValue.substring(0, indexSymbolBrackets);
                } else {
                    amount = cellValue.substring(0);
                }

                String weight;
                int indexSymbolWeight = cellValue.indexOf(SYMBOL_WEIGHT);
                if (indexSymbolWeight != -1) {
                    weight = textBeforeNextSpecialSymbol(cellValue, indexSymbolWeight);
                } else {
                    //check common weight of training
                    weight = trainingWeights.get(curTrainingIndex);
                }

                trainingContent.setWeight(convertTextToDigit(weight));
                trainingContent.setAmount(convertTextToDigit(amount));

                //System.out.println("Before save content exercise " +curExercise.getId()+" training " +curTraining.getId());

                TrainingContent dbTrainingContent;
                try {
                    dbTrainingContent = DB.getTrainingContent(curExercise.getId(), curTraining.getId());
                    dbTrainingContent.setAmount(trainingContent.getAmount());
                    dbTrainingContent.setWeight(trainingContent.getWeight());
                    DB.updateTrainingContent(dbTrainingContent);
                } catch (TableDoesNotContainElementException e) {
                    DB.addTrainingContent(trainingContent);
                }
                //System.out.println("After save content exercise " +curExercise.getId()+" training " +curTraining.getId());
            }
        }

        messageTrainingList
                .insert(0, "From file  \n" + BACKUP_FOLDER + "/trainings.xlsx" + '\n'
                        + " successfully loaded trainings:" + "\n");
        return messageTrainingList.toString();
    }

    private List<WeightChangeCalendar> ReadWeightsFromSheet(Sheet excelWeightsSheet) {

        Row currentRow;

        int mRow = 1;
        int mRowCount = 0;
        int mColumn = 0;
        while (true) {
            currentRow = excelWeightsSheet.getRow(mRow);
            try {
                double id = currentRow.getCell(mColumn).getNumericCellValue();
                if (id == 0) {
                    mRowCount = mRow;
                    break;
                }
                mRow++;
            } catch (Exception e) {
                mRowCount = mRow;
                break;
            }
        }
        int columnID = 0;
        int columnDate = 1;
        int columnWeight = 2;

        for (mRow = 1; mRow < mRowCount; mRow++) {
            currentRow = excelWeightsSheet.getRow(mRow);
            try {
                int id = (int) currentRow.getCell(columnID).getNumericCellValue();
                String date = currentRow.getCell(columnDate).getStringCellValue();
                int weight = (int) currentRow.getCell(columnWeight).getNumericCellValue();
                weightChangeCalendarList.add(new WeightChangeCalendar.Builder(id)
                        .addDayString(date)
                        .addWeight(weight).build());
            } catch (Exception e) {
            }
        }
        return weightChangeCalendarList;
    }

    private List<String[]> ReadDataFromSheet(Sheet myExcelSheet) {

        List<String[]> data = new ArrayList<>();
        Row currentRow = myExcelSheet.getRow(0);

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
                    if (currentRow.getCell(mColumn).getCellTypeEnum() == CellType.BLANK) {
                        mNewString.append("").append(SYMBOL_SPLIT);
                    } else if (currentRow.getCell(mColumn).getCellTypeEnum() == CellType.NUMERIC) {
                        int num = (int) currentRow.getCell(mColumn).getNumericCellValue();
                        mNewString.append(num).append(SYMBOL_SPLIT);
                    } else if (currentRow.getCell(mColumn).getCellTypeEnum() == CellType.STRING) {
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

    public String importFromFile() {
        //        System.setProperty("javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        List<String[]> data;
        StringBuilder message = new StringBuilder();
        try {
//            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
            XSSFWorkbook myExcelBook = (XSSFWorkbook) WorkbookFactory.create(new FileInputStream(file));
            Sheet myExcelSheet = myExcelBook.getSheet("trainings_full");

            if (myExcelSheet == null) {

                message.append("Missed sheet - \"trainings_full\"").append("\n")
                        .append("Try to load data from sheet \"trainings\"").append("\n");
                myExcelSheet = myExcelBook.getSheet("trainings");
                if (myExcelSheet == null) {
                    message.append("Missed sheet - \"trainings\"")
                            .append("\n")
                            .append("Trainings didn't load from ")
                            .append(BACKUP_FOLDER)
                            .append("/trainings.xlsx")
                            .append("\n");
                }
            }
            data = ReadDataFromSheet(myExcelSheet);
            message.append(writeDataToDB(data));

            List<WeightChangeCalendar> weights = new ArrayList<>();
            Sheet myExcelSheetWeights = myExcelBook.getSheet("weight_calendar");
            if (myExcelSheetWeights != null) {
                weights = ReadWeightsFromSheet(myExcelSheetWeights);
                for (WeightChangeCalendar weight : weights) {
                    weight.save(DB);
                }
            } else {
                message
                        .append("Missed sheet - \"trainings\"")
                        .append("\n").append("Trainings didn't load from ")
                        .append(BACKUP_FOLDER)
                        .append("/trainings.xlsx")
                        .append("\n");
            }
            myExcelBook.close();
        } catch (Exception e) {
            message
                    .append("Trainings didn't load from ")
                    .append(BACKUP_FOLDER)
                    .append("/trainings.xlsx");
        }
        return message.toString();
    }
}
