package com.sss.safesecure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

import static com.sss.safesecure.LoginActivity.keyClass;

/**
 * Purpose: Helper class for SQLite DB transactions - this class wil hold all methods for -
 * creating, reading, updating and deleting.
 *
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private String DATABASE_PATH;
    private static final String DATABASE_NAME = "PWManagerDB.db";
    private final Context context;
    private boolean isCreated = false;
    private boolean isTempPopulated = false;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        DATABASE_PATH = context.getFilesDir().getPath();
        sqLiteDatabase.execSQL(DBContract.LoginDataTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBContract.PwTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBContract.SecurityReportTable.CREATE_TABLE);

        if(!databaseIsEmpty()){
            isCreated = true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.PwTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.LoginDataTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.SecurityReportTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //region Password Table
    /**
     * Purpose: this method allows for creation of database once user has confirmed pass code
     */
    public void createDataBase() throws Exception {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            if(isCreated) {
            }
        }
    }

    /**
     * Purpose: saves the password data and inserts new table row.
     * @param Website (string website)
     * @param Email (string Email)
     * @param Additional (string Additional)
     * @param Pw (string Password)
     */
    public void Create(String Website, String Email, String Additional, String Pw, String date_time) {

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //Content values for the database
        ContentValues values = new ContentValues();
        //PUT values in the content values
        values.put(DBContract.PwTable.COLUMN_NAME1, encryptData(Website));
        values.put(DBContract.PwTable.COLUMN_NAME2, encryptData(Email));
        values.put(DBContract.PwTable.COLUMN_NAME3, encryptData(Additional));
        values.put(DBContract.PwTable.COLUMN_NAME4, encryptData(Pw));
        values.put(DBContract.PwTable.COLUMN_NAME5, encryptData(date_time));

        //inserts a row into the database
        long newRowId = database.insert(DBContract.PwTable.TABLE_NAME, null, values);

        Log.i("DB Create", encryptData(Website));

        database.close();
    }

    /**
     * Purpose: gets all data from password info table.
     */
    public Cursor Retrieve() {

        //gets the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        //SQL SELECT QUERY - selects all data from db table
        String selectQuery = "SELECT * FROM PasswordInfo";

        return database.rawQuery(selectQuery, null);
    }

    /**
     * Purpose: Updates the password data for the given table row.
     * @param index the site id
     * @param Website website name
     * @param Email Email address
     * @param Additional Additional info
     * @param Pw Password
     */
    public void Update(int index, String Website, String Email, String Additional, String Pw, String date_time) {

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //local variables to store database and query info - prevents sql injection
        String table = "PasswordInfo";
        String whereClause = "_id="+index;

        //instantiate content values to update
        ContentValues values = new ContentValues();
        //PUT values in the database, data column
        values.put(DBContract.PwTable.COLUMN_NAME1, encryptData(Website));
        values.put(DBContract.PwTable.COLUMN_NAME2, encryptData(Email));
        values.put(DBContract.PwTable.COLUMN_NAME3, encryptData(Additional));
        values.put(DBContract.PwTable.COLUMN_NAME4, encryptData(Pw));
        values.put(DBContract.PwTable.COLUMN_NAME5, encryptData(date_time));

        //updates the database - table - values - where
        database.update(table, values, whereClause, null);

        database.close();
    }

    /**
     * purpose: get data entries from table by id.
     * @param index (index to delete)
     */
    public void Delete(int index) {

        SQLiteDatabase database = this.getWritableDatabase();

        //local variables to store database and query info - prevents sql injection
        String table = "PasswordInfo";
        String whereClause = "_id="+index;

        //delete the corresponding entry from the database
        database.delete(table, whereClause, null);

        database.close();
    }

    /**
     * Purpose: gets table data by id.
     * @param TableName the name of the table to insert the data.
     * @param index the index of the table data to find.
     * @return the table data for the id provided.
     */
    public Cursor getDataById(String TableName, int index) {

        //gets the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        //SQL SELECT QUERY - selects data from db table uses the parameters table name and index
        String selectQuery = "SELECT * FROM " + TableName + " WHERE _ID=" + index;

        return database.rawQuery(selectQuery, null);
    }

    /**
     * Purpose: gets all websites and passwords that have been reused.
     * @return database query cursor.
     */
    public Cursor getReusedPasswords() {

        //gets the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT DISTINCT a.TempWebsite, b.TempWebsite, a.TempPassword " +
                "FROM TempPasswordInfo a " +
                "INNER JOIN TempPasswordInfo b " +
                "ON(a.TempWebsite != b.TempWebsite and a.TempPassword = b.TempPassword) " +
                "Order by a._ID " +
                "limit 0," + (getTempCount()/2);

        return database.rawQuery(query, null);
    }

    /**
     * Purpose: returns boolean value based on whether the database exists at the file path.
     */
    public boolean checkDataBase() {
        File databasePath = context.getDatabasePath(DATABASE_NAME);
        return databasePath.exists();
    }

    /**
     * Purpose: returns boolean value based on whether the database file is empty.
     */
    public boolean databaseIsEmpty() {
        return !DATABASE_PATH.isEmpty();
    }
    //endregion

    //region Pass code table
    /**
     * Purpose: Saves passcode data and inserts new table row.
     * @param Code the pass code form the user
     */
    public void saveCodeData(String Code, String salt) {

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //Content values for the database
        ContentValues values = new ContentValues();
        //PUT values in the content values
        values.put(DBContract.LoginDataTable.COLUMN_NAME1, encryptData(Code));
        values.put(DBContract.LoginDataTable.COLUMN_NAME2, salt);

        //inserts a row into the database
        long newRowId = database.insert(DBContract.LoginDataTable.TABLE_NAME, null, values);

        database.close();

        boolean isSaved = true;
    }
    //endregion

    //region Temporary table creation, population and dropping.
    /**
     * Purpose: creates a temp table for querying data.
     */
    public void createTempTable(){

        //gets the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL(DBContract.TempTable.CREATE_TABLE);

        database.close();
    }

    /**
     * Purpose: populates the temp table with data for querying.
     */
    public void populateTempTable(){

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //Content values for the database
        ContentValues values = new ContentValues();

        //retrieves all data from the pw info table
        Cursor cursor = Retrieve();

        //while the cursor is moving to the next entry
        while (cursor.moveToNext()) {

            //decrypts websites
            String ws = decryptData(cursor.getString(1));
            //decrypts passwords
            String pw = decryptData(cursor.getString(4));

            //PUT values in the content values
            values.put(DBContract.TempTable.COLUMN_NAME1, ws);
            values.put(DBContract.TempTable.COLUMN_NAME2, pw);

            //inserts a row into the database
            long newRowId = database.insert(DBContract.TempTable.TABLE_NAME, null, values);
        }
        cursor.close();
        database.close();

        isTempPopulated = true;
    }

    /**
     * Purpose: returns boolean value based on whether the temp table data is populated.
     */
    public boolean tempIsPopulated(){

        return isTempPopulated;
    }

    /**
     * Purpose: Drops the temp table.
     */
    public void dropTempTable(){

        //gets the writable database
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + DBContract.TempTable.TABLE_NAME);

        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='TempPasswordInfo'";

        Cursor cursor = database.rawQuery(query, null);

        cursor.close();
        database.close();
    }

    /**
     * Purpose: gets the count of all rows in the temp table.
     * @return the row count.
     */
    public int getTempCount(){

        //gets the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        return (int) DatabaseUtils.longForQuery(database, "SELECT COUNT(*) FROM TempPasswordInfo", null);
    }

    /**
     * Purpose: returns boolean value based on whether temp table exists.
     */
    public boolean tableExists() {

        boolean doesExist = false;

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='TempPasswordInfo'";

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() == 0){
            doesExist = false;
        }
        if(cursor.getCount() == 1){
            doesExist = true;
        }
        cursor.close();
        database.close();

        return doesExist;
    }
    //endregion

    //region Data Encryption and Decryption
    /**
     * Purpose: gets the secret key for encrypting and decrypting data.
     */
    private String getSecretKey(){

        return keyClass[0].getKey();
    }

    /**
     * Purpose: encrypts data before saving into the database.
     * @param dataToEncrypt the data to encrypt.
     * @return the encrypted data for saving to the database.
     */
    private String encryptData(String dataToEncrypt){
        //instantiates a new crypto class object - allows access to methods.
        CryptoClass crypto = new CryptoClass();

        //returns the encrypted data
        return crypto.encrypt(dataToEncrypt, getSecretKey());
    }

    /**
     * Purpose: decrypts data from the database before displaying to the user.
     * @param dataToDecrypt the data to decrypt.
     * @return the decrypted data for displaying to the user.
     */
    private String decryptData(String dataToDecrypt){
        //instantiates a new crypto class object - allows access to methods.
        //reference to the Crypto Class
        CryptoClass crypto = new CryptoClass();

        //returns the decrypted data
        return crypto.decrypt(dataToDecrypt, getSecretKey());
    }
    //endregion

    //region Security Report Dates Table
    /**
     * Purpose: puts default values into the security report table -
     * for each report type.
     */
    public void addFirstReportDates(){

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //instantiates a calender
        Calendar dateSaved = Calendar.getInstance();

        //instantiates a long to store the date in milliseconds
        long currentDateInMillis = dateSaved.getTimeInMillis();
        //instantiates a string to store the converted current date in milliseconds
        String dateTime = String.valueOf(currentDateInMillis);

        String SQLInsert = "INSERT INTO SecurityReportDates (ReusedReportDate, " +
                "ExpiredReportDate, StrengthReportDate) " +
                "VALUES('" + dateTime + "', '" + dateTime + "', '" + dateTime + "')";

        database.execSQL(SQLInsert);
    }
    /**
     * Purpose: updates the report date  -
     * once the user populates the reused password report.
     * @param reusedRepDate the date the report was compiled.
     */
    public void updateReusedReportDate(String reusedRepDate){

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //local variables to store database and query info - prevents sql injection
        String table = "SecurityReportDates";
        String whereClause = "_id=1";

        //instantiate content values to update
        ContentValues values = new ContentValues();
        //PUT values in the database, data column
        values.put(DBContract.SecurityReportTable.COLUMN_NAME1, reusedRepDate);

        //updates the database - table - values - where
        database.update(table, values, whereClause, null);

        database.close();
    }
    /**
     * Purpose: updates the report date  -
     * once the user populates the expired password report.
     * @param expRepDate the date the report was compiled.
     */
    public void updateExpiredReportDate(String expRepDate){

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //local variables to store database and query info - prevents sql injection
        String table = "SecurityReportDates";
        String whereClause = "_id=1";

        //instantiate content values to update
        ContentValues values = new ContentValues();
        //PUT values in the database, data column
        values.put(DBContract.SecurityReportTable.COLUMN_NAME2, expRepDate);

        //updates the database - table - values - where
        database.update(table, values, whereClause, null);

        database.close();
    }
    /**
     * Purpose: updates the report date  -
     * once the user populates the password strength report.
     * @param strengthRepDate the date the report was compiled.
     */
    public void updateStrengthReportDate(String strengthRepDate){

        //Access the writable database
        SQLiteDatabase database = this.getWritableDatabase();

        //local variables to store database and query info - prevents sql injection
        String table = "SecurityReportDates";
        String whereClause = "_id=1";

        //instantiate content values to update
        ContentValues values = new ContentValues();
        //PUT values in the database, data column
        values.put(DBContract.SecurityReportTable.COLUMN_NAME3, strengthRepDate);

        //updates the database - table - values - where
        database.update(table, values, whereClause, null);

        database.close();
    }
    /**
     * Purpose: gets dates from the security reports table.
     */
    public Cursor RetrieveSecurityReportDate() {

        //gets the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        //SQL SELECT QUERY - selects all data from db table
        String selectQuery = "SELECT * FROM SecurityReportDates";

        return database.rawQuery(selectQuery, null);
    }
    //endregion
}
