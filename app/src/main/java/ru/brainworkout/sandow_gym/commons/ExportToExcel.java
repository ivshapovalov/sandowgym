package ru.brainworkout.sandow_gym.commons;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ivan on 16.05.2016.
 */
public class ExportToExcel {


//    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>
//
//    {
//
//        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//
//        @Override
//
//        protected void onPreExecute()
//
//        {
//
//            this.dialog.setMessage("Exporting database...");
//
//            this.dialog.show();
//
//        }
//
//
//
//        protected Boolean doInBackground(final String... args)
//
//        {
//
//
//            File dbFile=getDatabasePath("database_name");
//            //AABDatabaseManager dbhelper = new AABDatabaseManager(getApplicationContext());
//            AABDatabaseManager dbhelper = new AABDatabaseManager(DatabaseExampleActivity.this) ;
//            System.out.println(dbFile);  // displays the data base path in your logcat
//
//
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//
//            if (!exportDir.exists())
//
//            {
//                exportDir.mkdirs();
//            }
//
//
//            File file = new File(exportDir, "excerDB.csv");
//
//
//            try
//
//            {
//
//                if (file.createNewFile()){
//                    System.out.println("File is created!");
//                    System.out.println("myfile.csv "+file.getAbsolutePath());
//                }else{
//                    System.out.println("File already exists.");
//                }
//
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//                //SQLiteDatabase db = dbhelper.getWritableDatabase();
//
//                Cursor curCSV=db.getdb().rawQuery("select * from " + db.TABLE_NAME,null);
//
//                csvWrite.writeNext(curCSV.getColumnNames());
//
//                while(curCSV.moveToNext())
//
//                {
//
//                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1),curCSV.getString(2)};
//
//         /*curCSV.getString(3),curCSV.getString(4)};*/
//
//                    csvWrite.writeNext(arrStr);
//
//
//                }
//
//                csvWrite.close();
//                curCSV.close();
//        /*String data="";
//        data=readSavedData();
//        data= data.replace(",", ";");
//        writeData(data);*/
//
//                return true;
//
//            }
//
//            catch(SQLException sqlEx)
//
//            {
//
//                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
//
//                return false;
//
//            }
//
//            catch (IOException e)
//
//            {
//
//                Log.e("MainActivity", e.getMessage(), e);
//
//                return false;
//
//            }
//
//        }
//
//        protected void onPostExecute(final Boolean success)
//
//        {
//
//            if (this.dialog.isShowing())
//
//            {
//
//                this.dialog.dismiss();
//
//            }
//
//            if (success)
//
//            {
//
//                Toast.makeText(DatabaseExampleActivity.this, "Export succeed", Toast.LENGTH_SHORT).show();
//
//            }
//
//            else
//
//            {
//
//                Toast.makeText(DatabaseExampleActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
//
//            }
//        }}
//
//    public class CSVToExcelConverter extends AsyncTask<String, Void, Boolean> {
//
//
//        private final ProgressDialog dialog = new ProgressDialog(DatabaseExampleActivity.this);
//
//        @Override
//        protected void onPreExecute()
//        {this.dialog.setMessage("Exporting to excel...");
//            this.dialog.show();}
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            ArrayList arList=null;
//            ArrayList al=null;
//
//            //File dbFile= new File(getDatabasePath("database_name").toString());
//            File dbFile=getDatabasePath("database_name");
//            String yes= dbFile.getAbsolutePath();
//
//            String inFilePath = Environment.getExternalStorageDirectory().toString()+"/excerDB.csv";
//            outFilePath = Environment.getExternalStorageDirectory().toString()+"/test.xls";
//            String thisLine;
//            int count=0;
//
//            try {
//
//                FileInputStream fis = new FileInputStream(inFilePath);
//                DataInputStream myInput = new DataInputStream(fis);
//                int i=0;
//                arList = new ArrayList();
//                while ((thisLine = myInput.readLine()) != null)
//                {
//                    al = new ArrayList();
//                    String strar[] = thisLine.split(",");
//                    for(int j=0;j<strar.length;j++)
//                    {
//                        al.add(strar[j]);
//                    }
//                    arList.add(al);
//                    System.out.println();
//                    i++;
//                }} catch (Exception e) {
//                System.out.println("shit");
//            }
//
//            try
//            {
//                HSSFWorkbook hwb = new HSSFWorkbook();
//                HSSFSheet sheet = hwb.createSheet("new sheet");
//                for(int k=0;k<arList.size();k++)
//                {
//                    ArrayList ardata = (ArrayList)arList.get(k);
//                    HSSFRow row = sheet.createRow((short) 0+k);
//                    for(int p=0;p<ardata.size();p++)
//                    {
//                        HSSFCell cell = row.createCell((short) p);
//                        String data = ardata.get(p).toString();
//                        if(data.startsWith("=")){
//                            cell.setCellType(Cell.CELL_TYPE_STRING);
//                            data=data.replaceAll("\"", "");
//                            data=data.replaceAll("=", "");
//                            cell.setCellValue(data);
//                        }else if(data.startsWith("\"")){
//                            data=data.replaceAll("\"", "");
//                            cell.setCellType(Cell.CELL_TYPE_STRING);
//                            cell.setCellValue(data);
//                        }else{
//                            data=data.replaceAll("\"", "");
//                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//                            cell.setCellValue(data);
//                        }
//                        //*/
//                        // cell.setCellValue(ardata.get(p).toString());
//                    }
//                    System.out.println();
//                }
//                FileOutputStream fileOut = new FileOutputStream(outFilePath);
//                hwb.write(fileOut);
//                fileOut.close();
//                System.out.println("Your excel file has been generated");
//            } catch ( Exception ex ) {
//                ex.printStackTrace();
//            } //main method ends
//            return true;
//        }
//
//        protected void onPostExecute(final Boolean success)
//
//        {
//
//            if (this.dialog.isShowing())
//
//            {
//
//                this.dialog.dismiss();
//
//            }
//
//            if (success)
//
//            {
//
//                Toast.makeText(DatabaseExampleActivity.this, "file is built!", Toast.LENGTH_LONG).show();
//
//            }
//
//            else
//
//            {
//
//                Toast.makeText(DatabaseExampleActivity.this, "file fail to build", Toast.LENGTH_SHORT).show();
//
//            }
//
//        }
//
//
//    }
//
//    public class ExportDatabaseToCSV{
//
//        Context context;
//        public ExportDatabaseToCSV(Context context) {
//            this.context=context;
//        }
//
//
//        public void exportDataBaseIntoCSV(){
//
//
//            CredentialDb db = new CredentialDb(context);//here CredentialDb is my database. you can create your db object.
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//
//            if (!exportDir.exists())
//            {
//                exportDir.mkdirs();
//            }
//
//            File file = new File(exportDir, "csvfilename.csv");
//
//            try
//            {
//                file.createNewFile();
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//                SQLiteDatabase sql_db = db.getReadableDatabase();//here create a method ,and return SQLiteDatabaseObject.getReadableDatabase();
//                Cursor curCSV = sql_db.rawQuery("SELECT * FROM "+CredentialDb.TABLE_NAME,null);
//                csvWrite.writeNext(curCSV.getColumnNames());
//
//                while(curCSV.moveToNext())
//                {
//                    //Which column you want to export you can add over here...
//                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
//                    csvWrite.writeNext(arrStr);
//                }
//
//                csvWrite.close();
//                curCSV.close();
//            }
//            catch(Exception sqlEx)
//            {
//                Log.e("Error:", sqlEx.getMessage(), sqlEx);
//            }
//        }
//    }
}



