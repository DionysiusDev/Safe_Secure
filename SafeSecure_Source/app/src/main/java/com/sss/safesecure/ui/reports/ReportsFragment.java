package com.sss.safesecure.ui.reports;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.sss.safesecure.CryptoClass;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.sss.safesecure.LoginActivity.keyClass;

public class ReportsFragment extends Fragment {

    /**
     * Purpose: instantiates a boolean to check passwords for lower case letters.
     * boolean isLower.
     */
    boolean isLower = false;
    /**
     * Purpose: instantiates a boolean to check passwords for upper case letters.
     * boolean isCapital.
     */
    boolean isCapital = false;
    /**
     * Purpose: instantiates a boolean to check passwords for numbers.
     * boolean isNumber.
     */
    boolean isNumber = false;
    /**
     * Purpose: instantiates a boolean to check passwords for special characters.
     * boolean isSpecial.
     */
    boolean isSpecial = false;
    /**
     * Purpose: instantiates a boolean to check if  the reused report is displaying - used to show report help.
     * boolean isDisplayingReusedReport.
     */
    boolean isDisplayingReusedReport = false;
    /**
     * Purpose: instantiates a boolean to check if  the expired report is displaying - used to show report help.
     * boolean isDisplayingExpiredReport.
     */
    boolean isDisplayingExpiredReport = false;
    /**
     * Purpose: instantiates a boolean to check if  the strength report is displaying - used to show report help.
     * boolean isDisplayingStrengthReport.
     */
    boolean isDisplayingStrengthReport = false;
    boolean isDisplayingReport = false;
    /**
     * Purpose: Declares an SQLiteDBHelper class.
     * SQLiteDBHelper DBHelper.
     */
    private SQLiteDBHelper DBHelper;
    /**
     * Purpose: declares a string to store the password strength.
     * String passwordStrength.
     */
    private String passwordStrength;
    /**
     * Purpose: Instantiates a list of strings used to populate the list view.
     * List<String> dataList.
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * Purpose: Instantiates a list of strings used to populate the list view with report types.
     * List<String> reportTypeList.
     */
    private final List<String> reportTypeList = new ArrayList<>();
    /**
     * Purpose: Declaration of the list view.
     * ListView listView.
     */
    private ListView listView;
    /**
     * Purpose: Declaration of the layouts text views - used to graphically display password strength.
     * TextView reportHeading, vWeakCountText, weakCountText, medCountText, strongCountText,
     * vStrongCountText, weakText, vWeakText, medText, strongText, vStrongText.
     */
    private TextView reportHeading, vWeakCountText, weakCountText, medCountText, strongCountText, vStrongCountText, weakText, vWeakText, medText, strongText, vStrongText;
    /**
     * Purpose: Declaration of the layouts image views - used to graphically display password strength.
     * ImageView vWeakImg, weakImg, medImg, strongImg, vStrongImg.
     */
    private ImageView vWeakImg, weakImg, medImg, strongImg, vStrongImg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        ReportsViewModel reportsViewModel = new ViewModelProvider(this).get(ReportsViewModel.class);

        //inflates the fragment to the view
        View root = inflater.inflate(R.layout.fragment_reports, container, false);

        //gets buttons from list activity by id
        //declaration of buttons
        Button btnHelp = root.findViewById(R.id.BtnHelp);
        Button btnReset = root.findViewById(R.id.BtnReset);

        //text views to display count
        vWeakCountText = root.findViewById(R.id.veryWeakCountText);
        weakCountText = root.findViewById(R.id.weakCountText);
        medCountText = root.findViewById(R.id.mediumCountText);
        strongCountText = root.findViewById(R.id.strongCountText);
        vStrongCountText = root.findViewById(R.id.veryStrongCountText);

        //text views to display strength types
        vWeakText = root.findViewById(R.id.veryWeakImageText);
        weakText = root.findViewById(R.id.weakImageText);
        medText = root.findViewById(R.id.mediumImageText);
        strongText = root.findViewById(R.id.strongImageText);
        vStrongText = root.findViewById(R.id.veryStrongImageText);

        //gets image views by id
        vWeakImg = root.findViewById(R.id.veryWeakImage);
        weakImg = root.findViewById(R.id.weakImage);
        medImg = root.findViewById(R.id.mediumImage);
        strongImg = root.findViewById(R.id.strongImage);
        vStrongImg = root.findViewById(R.id.veryStrongImage);

        //hides the gui components associated with the password strength report
        hidePWStrengthGUI();

        //gets list view from list activity by id
        listView = root.findViewById(R.id.PWListView);

        //populates the list view with the report types
        populateReportList();

        //adds on click event to list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if(!isDisplayingReport){

                    if(position == 0){
                        clearDataListView();
                        findReusedPasswords();
                    }
                    if(position == 1){
                        clearDataListView();
                        findExpiredPasswords();
                    }
                    if(position == 2){
                        clearDataListView();
                        try {
                            checkPasswordStrength();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //adds on click event to button reset
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDataListView();
                hidePWStrengthGUI();
                populateListView(reportTypeList);
            }
        });

        //adds on click event to button help
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isDisplayingReport) {
                    DisplayHelp();
                }
                else if(isDisplayingReusedReport) {
                    popup("To ensure passwords are secure: " +
                            "\nUse this app to create a new password for each website." +
                            "\nDon't forget to update them on the website!");
                    popup("Passwords that have been used on multiple websites are a security risk.");
                    popup("This report shows passwords that have been reused on other websites.");
                }
                else if(isDisplayingExpiredReport) {
                    popup("To ensure passwords are secure update them often." +
                            "\nDon't forget to update them on the website!");
                    popup("Passwords that have been in use for longer than 30 days are a security risk.");
                    popup("This report shows passwords that have been in use for longer than 30 days.");
                }
                else if(isDisplayingStrengthReport) {
                    popup("If a password is less than 15 characters it is also considered weaker.");
                    popup("If a password contains simple words like 'Ilove123' it is considered weaker.");
                    popup("Very strong passwords include: " +
                            "\n1 UPPERCASE letter. " +
                            "\n1 lower case letter. " +
                            "\n1 symbol [@#$%*&]. " +
                            "\n1 number [0-9].");
                    popup("To create very strong passwords ensure the password is 15 characters long.");
                    popup("This report shows each website and the strength of it's password.");
                }
            }
        });

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(getActivity() != null){

            //prevents the orientation from being displayed horizontally
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Purpose: hides the password strength report gui components.
     */
    private void hidePWStrengthGUI(){
        //sets the image views background alpha to invisible
        vWeakImg.getBackground().setAlpha(0);
        weakImg.getBackground().setAlpha(0);
        medImg.getBackground().setAlpha(0);
        strongImg.getBackground().setAlpha(0);
        vStrongImg.getBackground().setAlpha(0);

        //sets the count text views to empty
        vWeakCountText.setText("");
        weakCountText.setText("");
        medCountText.setText("");
        strongCountText.setText("");
        vStrongCountText.setText("");

        //sets the strength type text views to empty
        vWeakText.setText("");
        weakText.setText("");
        medText.setText("");
        strongText.setText("");
        vStrongText.setText("");
    }

    /**
     * Purpose: un-hides the password strength report gui components.
     */
    private void unHidePWStrengthGUI(){
        //sets the image views background alpha to visible
        vWeakImg.getBackground().setAlpha(255);
        weakImg.getBackground().setAlpha(255);
        medImg.getBackground().setAlpha(255);
        strongImg.getBackground().setAlpha(255);
        vStrongImg.getBackground().setAlpha(255);

        //sets the strength type text views to default
        vWeakText.setText(R.string.very_weak);
        weakText.setText(R.string.weak);
        medText.setText(R.string.medium);
        strongText.setText(R.string.strong);
        vStrongText.setText(R.string.very_strong);
    }

    /**
     * Purpose: Clears the current list view data.
     */
    private void clearDataListView() {
        //clears the list
        dataList.clear();
        isDisplayingReport = false;
    }

    /**
     * Purpose: this method populates the report list on activity load -
     * and when reset button is clicked.
     */
    private void populateReportList(){

        //populates the report list
        reportTypeList.add("Scan for re-used passwords");
        reportTypeList.add("Scan for expired passwords");
        reportTypeList.add("Scan passwords for strength");

        populateListView(reportTypeList);
    }

    /**
     * Purpose: this method instantiates an array adapter and displays the data in the list view.
     * @param dataToAdd list of data to display
     */
    private void populateListView(List<String> dataToAdd){

        //instantiates an array adapter for a simple list item -
        //  displays the data to add values
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataToAdd);

        //sets the adapter to the list view
        listView.setAdapter(adapter);
    }

    /**
     * Purpose: this method reads the database and finds duplicate passwords in the database.
     * if no duplicates are found it displays notification to user -
     * if duplicates are found it populates the report.
     */
    private void findReusedPasswords() {

        updateReusedReportDate();

        boolean isReused = false;

        DBHelper = new SQLiteDBHelper(getActivity());

        //checks if temp table exists
        if(!DBHelper.tableExists()){

            //creates 2 temp tables for comparing values
            DBHelper.createTempTable();
            //populates temp table with decrypted data
            DBHelper.populateTempTable();
        }else{
            //populates temp table with decrypted data
            DBHelper.populateTempTable();
        }

        //checks temp table is populated
        if(DBHelper.tableExists() && DBHelper.tempIsPopulated()) {

            try {
                Cursor cursor = DBHelper.getReusedPasswords();

                while (cursor.moveToNext()) {

                    dataList.add("\n" + cursor.getString(2) + "\n"
                            + cursor.getString(0)
                            + "\n" + cursor.getString(1) + "\n");
                }
                cursor.close();

                if(dataList.size() > 0) {
                    isReused = true;
                }

                if (isReused) {
                    //sets the report heading
                    dataList.add(0, "Reused Password Report Compiled");
                    //populates the list view data
                    populateListView(dataList);

                    //displays messages to the user
                    popup("Re-using the same password on different websites is a security risk.");
                    popup("Re-used passwords have been identified.");

                    isDisplayingReusedReport = true;
                    isDisplayingExpiredReport = false;
                    isDisplayingStrengthReport = false;
                    isDisplayingReport = true;
                } else {
                    popup("No re-used passwords have been identified.");
                }
                DBHelper.dropTempTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Purpose: this method query's the database -
     * and calculates all passwords that are over 30 days old.
     */
    private void findExpiredPasswords() {

        updateExpiredReportDate();

        boolean isExpired = false;

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(getActivity());

        //calls the get all data method and query's the database
        Cursor cursor = DBHelper.Retrieve("PasswordInfo");

        //instantiates a new calender
        Calendar currentDate = Calendar.getInstance();
        //instantiates an integer to store the current calendar month
        int cMonth = currentDate.get(Calendar.MONTH);

        //instantiates a new calender - to store data base time/date
        Calendar savedDate = Calendar.getInstance();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            try {
                //instantiates a string to store the database value for the date saved
                String decryptedDate = decryptData(cursor.getString(5));

                //instantiates a long convert the decryptedDate string
                long timeData = Long.parseLong(decryptedDate);
                //sets the database time to the calendar
                savedDate.setTimeInMillis(timeData);

            } catch (IllegalArgumentException e) {
                e.getLocalizedMessage();
            }

            //instantiates integers to store the saved values for the year, month, day
            int sYear = savedDate.get(Calendar.YEAR);
            int sMonth = savedDate.get(Calendar.MONTH);
            int sDay = savedDate.get(Calendar.DAY_OF_MONTH);

            if (sMonth <= (cMonth - 1)) {

                //adds one to the calendar to display the months from 1-12
                savedDate.add(Calendar.MONTH, +1);
                //gets the month value
                sMonth = savedDate.get(Calendar.MONTH);

                //instantiates a string to format the day, month year values
                String dateSaved = sDay + "/" + sMonth + "/" + sYear;

                //add the websites, expired passwords and date to the data list
                dataList.add("\n" + decryptData(cursor.getString(1))
                        + "\n" + decryptData(cursor.getString(4))
                        + "\nLast Updated: " + dateSaved + "\n");

                isExpired = true;
            }
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

        if(isExpired){

            dataList.add(0, "Expired Password Report Compiled");
            //populates the list view data
            populateListView(dataList);

            //displays messages to the user
            popup("Passwords in use for longer than 30 days are a security risk.");
            popup("Expired passwords have been identified.");

            isDisplayingExpiredReport = true;
            isDisplayingReusedReport = false;
            isDisplayingStrengthReport = false;
            isDisplayingReport = true;

        } else{
            popup("No expired passwords have been identified.");
        }
    }

    /**
     * Purpose: checks each password in the database and returns their strength based on the requirements.
     */
    private void checkPasswordStrength() throws Exception {

        updateStrengthReportDate();
        int vWeakPw = 0;
        int weakPw = 0;
        int medPw = 0;
        int strongPw = 0;
        int vStrongPw = 0;

        int wsCount = 0;

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(getActivity());

        //calls the get all data method and query's the database
        Cursor cursor = DBHelper.Retrieve("PasswordInfo");

        //instantiates a list to store passwords
        List<String> pwList = new ArrayList<>();

        //instantiates a list to store websites and passwords
        List<String> wsList = new ArrayList<>();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            //adds the passwords to the list
            pwList.add(decryptData(cursor.getString(4)));
            //adds the website to the list
            wsList.add(decryptData(cursor.getString(1)));
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

        //iterates over each password in the password list
        for (String pw : pwList) {
            checkPasswordCharacters(pw);

            wsCount++;

            //region checks for passwords strength
            //all criteria is met password is very strong
            if (isNumber && isCapital && isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Very Strong";
                    vStrongPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Strong";
                    strongPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Weak";
                    weakPw++;

                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }

            //one requirement is not met password is strong
            if (!isNumber && isCapital && isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Strong";
                    strongPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Weak";
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && !isCapital && isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Strong";
                    strongPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Weak";
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && isCapital && !isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Strong";
                    strongPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Weak";
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && isCapital && isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Strong";
                    strongPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Weak";
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }

            //two requirements are not met password is medium
            if (!isNumber && !isCapital && isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (!isNumber && isCapital && !isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (!isNumber && isCapital && isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && !isCapital && !isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && !isCapital && isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            } else if (isNumber && isCapital && !isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Medium";
                    medPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }
            //three requirements are not met password is weak
            if (!isNumber && !isCapital && !isLower && isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }
            if (!isNumber && isCapital && !isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }
            if (!isNumber && !isCapital && isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }
            if (isNumber && !isCapital && !isLower && !isSpecial) {

                if (pw.length() >= 15) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 10 && pw.length() <= 12) {
                    passwordStrength = "Weak";
                    weakPw++;
                } else if (pw.length() > 8 && pw.length() <= 10) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() > 5 && pw.length() <= 8) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                } else if (pw.length() <= 5) {
                    passwordStrength = "Very Weak";
                    vWeakPw++;
                }
            }
            //endregion

            //adds the website from the ws list -
            //  adds the password from the pw list and the password strength
            dataList.add("\n" + wsList.get(wsCount - 1) + "\n" + pw + "\n" + passwordStrength + "\n");
        }
        if(dataList.size() == 0){
            popup("No passwords in database.");
        } else{
            dataList.add(0, "Password strength report compiled.");
            //calls the populate list view method
            populateListView(dataList);
            //calls the set count and graph method and passes the password strength counts
            setCountAndGraph(vWeakPw, weakPw, medPw, strongPw, vStrongPw);
            isDisplayingStrengthReport = true;
            isDisplayingExpiredReport = false;
            isDisplayingReusedReport = false;
            isDisplayingReport = true;
        }
    }

    /**
     * Purpose: counts the total values of password strengths and adjust the image view graph.
     * @param vWeakCount total number of very weak passwords
     * @param weakCount total number of weak passwords
     * @param mediumCount total number of medium passwords
     * @param strongCount total number of strong passwords
     * @param vStrongCount total number of very strong passwords
     */
    private void setCountAndGraph(int vWeakCount, int weakCount, int mediumCount, int strongCount, int vStrongCount) {

        String message = String.format("%s %d", "Total: ", vWeakCount);
        vWeakCountText.setText(message);

        message = String.format("%s %d", "Total: ", weakCount);
        weakCountText.setText(message);

        message = String.format("%s %d", "Total: ", mediumCount);
        medCountText.setText(message);
        message = String.format("%s %d", "Total: ", strongCount);
        strongCountText.setText(message);
        message = String.format("%s %d", "Total: ", vStrongCount);
        vStrongCountText.setText(message);

        int[] countArr = {vWeakCount, weakCount, mediumCount, strongCount, vStrongCount};

        //sorts the count array
        Arrays.sort(countArr);

        //values for adjusting the graph size on the gui
        int xHighBar = 170 + (countArr[4] * 20);
        int highBar = 170 + (countArr[3] * 20);
        int medBar = 170 + (countArr[2] * 20);
        int lowBar = 170 + (countArr[1] * 20);
        int xLowBar = 170;

        //region Very Weak
        //checks if the very weak count is the highest value - sets the graph gui component
        if(vWeakCount > weakCount && vWeakCount > mediumCount && vWeakCount > strongCount && vWeakCount > vStrongCount) {
            vWeakImg.getLayoutParams().width = xHighBar;
            vWeakImg.requestLayout();

            //checks if very weak count is less than one other counts
        } else if(vWeakCount < weakCount || vWeakCount < mediumCount
                || vWeakCount < strongCount || vWeakCount < vStrongCount){

            vWeakImg.getLayoutParams().width = highBar;
            vWeakImg.requestLayout();

            //checks if very weak count is less than two other counts
            if(vWeakCount < weakCount && vWeakCount < mediumCount
                    || vWeakCount < weakCount && vWeakCount < strongCount
                    || vWeakCount < weakCount && vWeakCount < vStrongCount
                    || vWeakCount < mediumCount && vWeakCount < strongCount
                    || vWeakCount < mediumCount && vWeakCount < vStrongCount
                    || vWeakCount < strongCount && vWeakCount < vStrongCount){

                vWeakImg.getLayoutParams().width = medBar;
                vWeakImg.requestLayout();

                //checks if very weak count is less than three other counts
                if(vWeakCount < weakCount && vWeakCount < mediumCount && vWeakCount < strongCount
                        || vWeakCount < weakCount && vWeakCount < mediumCount && vWeakCount < vStrongCount
                        || vWeakCount < mediumCount && vWeakCount < strongCount && vWeakCount < vStrongCount
                        || vWeakCount < weakCount && vWeakCount < strongCount && vWeakCount < vStrongCount){

                    vWeakImg.getLayoutParams().width = lowBar;
                    vWeakImg.requestLayout();

                    //checks if very weak count is less than four other counts or equals zero
                    if(vWeakCount == 0 ||  vWeakCount < weakCount && vWeakCount < mediumCount
                            && vWeakCount < strongCount && vWeakCount < vStrongCount){

                        vWeakImg.getLayoutParams().width = xLowBar;
                        vWeakImg.requestLayout();
                    }
                }
            }
        }
        //endregion

        //region Weak
        //checks if the weak count is the highest value - sets the graph gui component
        if(weakCount > vWeakCount && weakCount > mediumCount && weakCount > strongCount && weakCount > vStrongCount) {
            weakImg.getLayoutParams().width = xHighBar;
            weakImg.requestLayout();

            //checks if weak count is less than one other counts
        } else if(weakCount < vWeakCount || vWeakCount < mediumCount
                || vWeakCount < strongCount || vWeakCount < vStrongCount){

            weakImg.getLayoutParams().width = highBar;
            weakImg.requestLayout();

            //checks if weak count is less than two other counts
            if(weakCount < vWeakCount && weakCount < mediumCount
                    || weakCount < vWeakCount && weakCount < strongCount
                    || weakCount < vWeakCount && weakCount < vStrongCount
                    || weakCount < mediumCount && weakCount < strongCount
                    || weakCount < mediumCount && weakCount < vStrongCount
                    || weakCount < strongCount && weakCount < vStrongCount){

                weakImg.getLayoutParams().width = medBar;
                weakImg.requestLayout();

                //checks if weak count is less than three other counts
                if(weakCount < vWeakCount && weakCount < mediumCount && weakCount < strongCount
                        || weakCount < vWeakCount && weakCount < mediumCount && weakCount < vStrongCount
                        || weakCount < mediumCount && weakCount < strongCount && weakCount < vStrongCount
                        || weakCount < vWeakCount && weakCount < strongCount && weakCount < vStrongCount){

                    weakImg.getLayoutParams().width = lowBar;
                    weakImg.requestLayout();

                    //checks if weak count is less than four other counts or equals zero
                    if(weakCount == 0 || weakCount < vWeakCount && vWeakCount < mediumCount
                            && vWeakCount < strongCount && vWeakCount < vStrongCount){

                        weakImg.getLayoutParams().width = xLowBar;
                        weakImg.requestLayout();
                    }
                }
            }
        }
        //endregion

        //region Medium
        //checks if the medium count is the highest value - sets the graph gui component
        if(mediumCount > vWeakCount && mediumCount > weakCount && mediumCount > strongCount && mediumCount > vStrongCount) {
            medImg.getLayoutParams().width = xHighBar;
            medImg.requestLayout();

            //checks if medium count is less than one other counts
        } else if(mediumCount < vWeakCount || mediumCount < weakCount
                || mediumCount < strongCount || mediumCount < vStrongCount){

            medImg.getLayoutParams().width = highBar;
            medImg.requestLayout();

            //checks if medium count is less than two other counts
            if(mediumCount < vWeakCount && mediumCount < weakCount
                    || mediumCount < vWeakCount && mediumCount < strongCount
                    || mediumCount < vWeakCount && mediumCount < vStrongCount
                    || mediumCount < weakCount && mediumCount < strongCount
                    || mediumCount < weakCount && mediumCount < vStrongCount
                    || mediumCount < strongCount && mediumCount < vStrongCount){

                medImg.getLayoutParams().width = medBar;
                medImg.requestLayout();

                //checks if medium count is less than three other counts
                if( mediumCount < vWeakCount && mediumCount < weakCount && mediumCount < strongCount
                        || mediumCount < vWeakCount && mediumCount < weakCount && mediumCount < vStrongCount
                        || mediumCount < vWeakCount && mediumCount < strongCount && mediumCount < vStrongCount
                        || mediumCount < weakCount && mediumCount < strongCount && mediumCount < vStrongCount){

                    medImg.getLayoutParams().width = lowBar;
                    medImg.requestLayout();

                    //checks if medium count is less than four other counts or equals zero
                    if(vWeakCount > mediumCount && weakCount > mediumCount && strongCount > mediumCount && vStrongCount > mediumCount || mediumCount == 0){

                        medImg.getLayoutParams().width = xLowBar;
                        medImg.requestLayout();
                    }
                }
            }
        }
        //endregion

        //region Strong
        //checks if the strong count is the highest value - sets the graph gui component
        if(strongCount > vWeakCount && strongCount > weakCount && strongCount > mediumCount && strongCount > vStrongCount) {
            strongImg.getLayoutParams().width = xHighBar;
            strongImg.requestLayout();

            //checks if strong count is less than one other counts
        } else if(strongCount < vWeakCount || strongCount < weakCount
                || strongCount < mediumCount || strongCount < vStrongCount){

            strongImg.getLayoutParams().width = highBar;
            strongImg.requestLayout();

            //checks if strong count is less than two other counts
            if(strongCount < vWeakCount && strongCount < weakCount
                    || strongCount < vWeakCount && strongCount < mediumCount
                    || strongCount < vWeakCount && strongCount < vStrongCount
                    || strongCount < weakCount && strongCount < mediumCount
                    || strongCount < weakCount && strongCount < vStrongCount
                    || strongCount < mediumCount && strongCount < vStrongCount){

                strongImg.getLayoutParams().width = medBar;
                strongImg.requestLayout();

                //checks if strong count is less than three other counts
                if(strongCount < vWeakCount && strongCount < weakCount && strongCount < mediumCount
                        || strongCount < vWeakCount && strongCount < weakCount && strongCount < vStrongCount
                        || strongCount < vWeakCount && strongCount < mediumCount && strongCount < vStrongCount
                        || strongCount < weakCount && strongCount < mediumCount && strongCount < vStrongCount){

                    strongImg.getLayoutParams().width = lowBar;
                    strongImg.requestLayout();

                    //checks if strong count is less than four other counts or equals zero
                    if(strongCount == 0 || strongCount < vWeakCount && strongCount < weakCount
                            && strongCount < mediumCount && strongCount < vStrongCount){

                        strongImg.getLayoutParams().width = xLowBar;
                        strongImg.requestLayout();
                    }
                }
            }
        }
        //endregion

        //region Very Strong
        //checks if the very strong count is the highest value - sets the graph gui component
        if(vStrongCount > vWeakCount && vStrongCount > weakCount && vStrongCount > mediumCount && vStrongCount > strongCount) {
            vStrongImg.getLayoutParams().width = xHighBar;
            vStrongImg.requestLayout();

            //checks if very strong count is less than one other counts
        } else if(vStrongCount < vWeakCount || vStrongCount < weakCount
                || vStrongCount < mediumCount || vStrongCount < strongCount){

            vStrongImg.getLayoutParams().width = highBar;
            vStrongImg.requestLayout();

            //checks if very strong count is less than two other counts
            if(vStrongCount < vWeakCount && strongCount < weakCount
                    || vStrongCount < vWeakCount && vStrongCount < mediumCount
                    || vStrongCount < vWeakCount && vStrongCount < strongCount
                    || vStrongCount < weakCount && vStrongCount < mediumCount
                    || vStrongCount < weakCount && vStrongCount < strongCount
                    || vStrongCount < mediumCount && vStrongCount < strongCount){

                vStrongImg.getLayoutParams().width = medBar;
                vStrongImg.requestLayout();

                //checks if very strong count is less than three other counts
                if(vStrongCount < vWeakCount && vStrongCount < weakCount && vStrongCount < mediumCount
                        || vStrongCount < vWeakCount && vStrongCount < weakCount && vStrongCount < strongCount
                        || vStrongCount < vWeakCount && vStrongCount < mediumCount && vStrongCount < strongCount
                        || vStrongCount < weakCount && vStrongCount < mediumCount && vStrongCount < strongCount){

                    vStrongImg.getLayoutParams().width = lowBar;
                    vStrongImg.requestLayout();

                    //checks if very strong count is less than four other counts or equals zero
                    if(vStrongCount == 0 || vStrongCount < vWeakCount && vStrongCount < weakCount
                            && vStrongCount < mediumCount && vStrongCount < strongCount){

                        vStrongImg.getLayoutParams().width = xLowBar;
                        vStrongImg.requestLayout();
                    }
                }
            }
        }
        //endregion

        unHidePWStrengthGUI();
    }

    /**
     * Purpose: checks each character in the password and verifies if it meets requirements.
     * @param password the password to check
     * @return true or false
     */
    private boolean checkPasswordCharacters(String password) throws Exception {
        char character;

        for(int index = 0; index < password.length(); index++) {

            character = password.charAt(index);

            //checks if the character is not lower case, upper case or digit.
            if(!Character.isLowerCase(character) && !Character.isUpperCase(character)
                    && !Character.isDigit(character)) {
                isSpecial = true;
            }
            //check for upper and lower case letters, and numbers
            else if( Character.isLowerCase(character)) {
                isLower = true;
            } else if (Character.isUpperCase(character)) {
                isCapital = true;
            } else if (Character.isDigit(character)) {
                isNumber = true;
            }
            if(isNumber && isCapital && isLower && isSpecial)
                return true;
        }
        return false;
    }

    /**
     * Purpose: stores the date of the last reused password report.
     */
    private void updateReusedReportDate(){
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());
        //instantiates a new calender - to store data base time/date
        Calendar savedDate = Calendar.getInstance();
        //sets the database time to the calendar
        long reportDate = savedDate.getTimeInMillis();
        String saveReportDate = String.valueOf(reportDate);

        DBHelper.updateReusedReportDate(saveReportDate);
    }

    /**
     * Purpose: stores the date of the last expired password report.
     */
    private void updateExpiredReportDate(){

        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());
        //instantiates a new calender - to store data base time/date
        Calendar savedDate = Calendar.getInstance();
        //sets the database time to the calendar
        long reportDate = savedDate.getTimeInMillis();
        String saveReportDate = String.valueOf(reportDate);

        DBHelper.updateExpiredReportDate(saveReportDate);
    }

    /**
     * Purpose: stores the date of the last strength report.
     */
    private void updateStrengthReportDate(){
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());
        //instantiates a new calender - to store data base time/date
        Calendar savedDate = Calendar.getInstance();
        //sets the database time to the calendar
        long reportDate = savedDate.getTimeInMillis();
        String saveReportDate = String.valueOf(reportDate);

        DBHelper.updateStrengthReportDate(saveReportDate);
    }

    /**
     * Purpose: gets the secret key used for decrypting the database.
     */
    private String getSecretKey(){

        return keyClass[0].getKey();
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

    /**
     * Purpose: Displays help messages to the user.
     */
    private void DisplayHelp() {
        popup("Click 'scan passwords for strength' to verify the strength of each password.");
        popup("Click 'scan for expired passwords' to search for any passwords that are older than 30 days.");
        popup("Click 'scan for re-used passwords' to search for any passwords that are used multiple times.");
    }

    /**
     * Purpose: Creates pop up alerts.
     * @param msg (String for the message to display to user)
     */
    private void popup(String msg) {

        //adds new alert builder to home activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //sets alert message
        builder.setMessage(msg);
        //sets builder cancelable
        builder.setCancelable(false);

        //returns positive button for ok
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //creates alert dialog
        AlertDialog alert = builder.create();

        //shows alert dialog
        alert.show();
    }
}