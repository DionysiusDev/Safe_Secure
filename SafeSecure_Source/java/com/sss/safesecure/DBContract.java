package com.sss.safesecure;

import android.provider.BaseColumns;

/**
 * Purpose: Handles database model and table construction.
 *
 */
final class DBContract {
    private DBContract() {
    }

    /**
     * Purpose: Creates a data model and table for storing password data.
     *
     */
    public static class PwTable implements BaseColumns {
        public static final String TABLE_NAME = "PasswordInfo";
        public static final String COLUMN_NAME1 = "Website";
        public static final String COLUMN_NAME2 = "Email";
        public static final String COLUMN_NAME3 = "Additional";
        public static final String COLUMN_NAME4 = "Password";
        public static final String COLUMN_NAME5 = "Date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME1 + " VARCHAR, " +
                COLUMN_NAME2 + " VARCHAR, " +
                COLUMN_NAME3 + " VARCHAR, " +
                COLUMN_NAME4 + " VARCHAR, " +
                COLUMN_NAME5 + " VARCHAR " + ")";
    }

    /**
     * Purpose: Creates a backup table for storing decrypted password data.
     *
     */
    public static class PwBakTable implements BaseColumns {
        public static final String TABLE_NAME = "PasswordInfoBak";
        public static final String COLUMN_NAME1 = "Website";
        public static final String COLUMN_NAME2 = "Email";
        public static final String COLUMN_NAME3 = "Additional";
        public static final String COLUMN_NAME4 = "Password";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME1 + " VARCHAR, " +
                COLUMN_NAME2 + " VARCHAR, " +
                COLUMN_NAME3 + " VARCHAR, " +
                COLUMN_NAME4 + " VARCHAR)";
    }

    /**
     * Purpose: Creates a data model and table for storing user login data.
     *
     */
    public static class LoginDataTable implements BaseColumns {
        public static final String TABLE_NAME = "LoginInfo";
        public static final String COLUMN_NAME1 = "Code";
        public static final String COLUMN_NAME2 = "Salt";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME1 + " VARCHAR, " +
                COLUMN_NAME2 + " VARCHAR " + ")";
    }

    /**
     * Purpose: Creates a data model and table for storing security report dates.
     *
     */
    public static class SecurityReportTable implements BaseColumns {
        public static final String TABLE_NAME = "SecurityReportDates";
        public static final String COLUMN_NAME1 = "ReusedReportDate";
        public static final String COLUMN_NAME2 = "ExpiredReportDate";
        public static final String COLUMN_NAME3 = "StrengthReportDate";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME1 + " VARCHAR, " +
                COLUMN_NAME2 + " VARCHAR, " +
                COLUMN_NAME3 + " VARCHAR " +")";
    }

    /**
     * Purpose: Creates a data model and table for storing temporary password data.
     *
     */
    public static class TempTable implements BaseColumns {
        public static final String TABLE_NAME = "'TempPasswordInfo'";
        public static final String COLUMN_NAME1 = "TempWebsite";
        public static final String COLUMN_NAME2 = "TempPassword";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME1 + " VARCHAR, " +
                COLUMN_NAME2 + " VARCHAR )";
    }
}
