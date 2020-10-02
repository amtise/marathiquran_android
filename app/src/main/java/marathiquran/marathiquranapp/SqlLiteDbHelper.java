package marathiquran.marathiquranapp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db.sqlite3";
    private static final String DB_PATH_SUFFIX = "/databases/";
    static Context ctx;

    public SqlLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }


    public void CopyDataBaseFromAsset() throws IOException {
        InputStream myInput = ctx.getAssets().open(DATABASE_NAME);
// Path to the just created empty db
        String outFileName = getDatabasePath();

// if the path doesn't exist first, create it
        File f = new File(ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();

// Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

// transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
// Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private static String getDatabasePath() {
        return ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_NAME;
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                System.out.println("Copying sucess from Assets folder");
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public List<Surat> getAllSurat() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Surat> contList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM portal_surat order by surat_number", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Surat cont = new Surat(cursor.getInt(0), cursor.getInt(1), cursor.getString(4), cursor.getString(3), cursor.getString(8));
                contList.add(cont);
            }
            cursor.close();
            db.close();
        }
        cursor.close();
        db.close();
        return contList;
    }

    public List<Ayat> getAllAyats(int surat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Ayat> contList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM portal_ayat where  surat_id =" + surat_id + " order by ayat_number", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Ayat cont = new Ayat(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                contList.add(cont);
            }
            cursor.close();
            db.close();
        }
        cursor.close();
        db.close();
        return contList;
    }


    // Get User Details
    public ArrayList<HashMap<String, String>> GetVachan(int surat_number, int surat_number2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT ayat_number,arabic_text,marathi_text,arabic_audio,marathi_audio FROM portal_ayat where surat_id=" + surat_number + " order by ayat_number";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            user.put("vachan_version", surat_number2 + ":" + cursor.getString(cursor.getColumnIndex("ayat_number")));
            user.put("arabic_text", cursor.getString(cursor.getColumnIndex("arabic_text")));
            user.put("marathi_text", cursor.getString(cursor.getColumnIndex("marathi_text")));
            user.put("arabic_audio", cursor.getString(cursor.getColumnIndex("arabic_audio")));
            user.put("marathi_audio", cursor.getString(cursor.getColumnIndex("marathi_audio")));
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }


    // Getting All Vachan
    public List<Vachan> getAllVachan(int surat_number, int surat_number2) {
        List vachanList = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT  * FROM portal_ayat where surat_id=" + surat_number;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                vachanList.add(new Vachan(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        Integer.parseInt(cursor.getString(6)), surat_number2));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return vachan list
        return vachanList;
    }




}