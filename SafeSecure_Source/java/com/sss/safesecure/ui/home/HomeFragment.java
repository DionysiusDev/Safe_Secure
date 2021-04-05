package com.sss.safesecure.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.MainActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;

import java.util.Calendar;

/**
 * Purpose: this class displays notifications and home screen gui components.
 */
public class HomeFragment extends Fragment {

    /**
     * Declaration of the layouts image views.
     */
    private ImageView Row1_ImageView1, Row2_ImageView1, Row3_ImageView1;

    /**
     * Declaration of the layouts text views.
     */
    private TextView Row1_TextView1, Row2_TextView1, Row3_TextView1;

    /**
     * Instantiates a boolean to control if the back button is pressed.
     */
    private boolean isBackPressed = false;
    /**
     * Declaration of a boolean to control if the app is logging out.
     */
    private boolean isLoggingOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //gets the image view for changing icon tint
        Row1_ImageView1 = root.findViewById(R.id.row1_ImageView1);
        Row2_ImageView1 = root.findViewById(R.id.row2_ImageView1);
        Row3_ImageView1 = root.findViewById(R.id.row3_ImageView1);

        //gets the table rows for on click navigation
        TableRow tableRow1 = root.findViewById(R.id.tableRow1);
        TableRow tableRow2 = root.findViewById(R.id.tableRow2);
        TableRow tableRow3 = root.findViewById(R.id.tableRow3);

        //gets the text views
        Row1_TextView1 = root.findViewById(R.id.row1_TextView1);
        Row2_TextView1 = root.findViewById(R.id.row2_TextView1);
        Row3_TextView1  = root.findViewById(R.id.row3_TextView1);

        //gets the button from the fragment layout by id
        Button btnBackup = root.findViewById(R.id.BtnBackup);
        //adds on click event to button generate
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backupDB();
            }
        });

        //checks security reports to notify user
        checkReusedReportDate();
        checkExpiredReportDate();
        checkStrengthReportDate();

        tableRow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //should navigate to the details fragment
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_reports);
            }
        });
        tableRow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //should navigate to the details fragment
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_reports);
            }
        });
        tableRow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //should navigate to the details fragment
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_reports);
            }
        });

        return root;
    }

    /**
     * Purpose: this method runs when the application resumes.
     * It sets the view orientation to verticle.
     * It sets an on key listener for the back button on the users device -
     * this triggers a log out pop up message.
     *
     */
    @Override
    public void onResume() {
        super.onResume();

        if(getActivity() != null){
            //prevents the orientation from being displayed horizontally
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if the user presses the back button
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    isBackPressed = true;

                    //sets the main activity back pressed boolean to override the back pressed event
                    MainActivity main = new MainActivity();
                    main.setBackPressed(isBackPressed);

                    if(main.getBackPressed()){
                        YesNoPopup();
                    }
                    // handle back button's click listener
                    return isBackPressed;
                }
                isBackPressed = false;
                return isBackPressed;
            }
        });
    }

    /**
     * Purpose: decrypts and backs up database to users documents.
     */
    private void backupDB(){

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        //reference to the SQLiteDBHelper class
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getContext());

        if(DBHelper.checkTableExists("PasswordInfoBak")){
            Log.d("Drop Table", "Dropping Table");
            DBHelper.dropBakTable();
        }
        if(!DBHelper.checkTableExists("PasswordInfoBak")){
            Log.d("Create Table", "Creating Backup Table");
            DBHelper.createBakTable();
            Log.d("Get Backup Data", "Getting Backup Data");
            DBHelper.getBackupData();

            DBHelper.exportDatabase();
        }
    }

    /**
     * Purpose: this method checks the last date the reused report was compiled.
     * It notifies the user if the report is older than 30 days.
     */
    private void checkReusedReportDate(){
        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());

        //calls the get all data method and query's the database
        Cursor cursor = DBHelper.RetrieveSecurityReportDate();

        //instantiates a new calender
        Calendar currentDate = Calendar.getInstance();
        //instantiates an integer to store the current calendar month
        int cMonth = currentDate.get(Calendar.MONTH);
        int cDay = currentDate.get(Calendar.DAY_OF_MONTH);

        //instantiates a new calender - to store data base time/date
        Calendar savedDate = Calendar.getInstance();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            try {
                //instantiates a string to store the database value for the date saved
                String lastReportDate = cursor.getString(1);

                //instantiates a long convert the decryptedDate string
                long timeData = Long.parseLong(lastReportDate);
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

                String message = String.format("%s %d %s %d %s %d", "You have not scanned for re-used passwords: ", sDay, "/", sMonth, "/", sYear);

                Row1_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                Row1_TextView1.setText(message);
            } else {
                Row1_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                Row1_TextView1.setText(R.string.reusedreportupdate);
            }
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database
    }

    /**
     * Purpose: this method checks the last date the expired report was compiled.
     * It notifies the user if the report is older than 30 days.
     */
    private void checkExpiredReportDate(){
        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());

        //calls the get all data method and query's the database
        Cursor cursor = DBHelper.RetrieveSecurityReportDate();

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
                String lastReportDate = cursor.getString(2);

                //instantiates a long convert the decryptedDate string
                long timeData = Long.parseLong(lastReportDate);
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

                String message = String.format("%s %d %s %d %s %d", "You have not scanned for expired passwords: ", sDay, "/", sMonth, "/", sYear);

                Row2_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                Row2_TextView1.setText(message);
            } else {
                Row2_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                Row2_TextView1.setText(R.string.expiredreportupdate);
            }
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database
    }

    /**
     * Purpose: this method checks the last date the strength report was compiled.
     * It notifies the user if the report is older than 30 days.
     */
    private void checkStrengthReportDate(){
        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());

        //calls the get all data method and query's the database
        Cursor cursor = DBHelper.RetrieveSecurityReportDate();

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
                String lastReportDate = cursor.getString(3);

                //instantiates a long convert the decryptedDate string
                long timeData = Long.parseLong(lastReportDate);
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

                String message = String.format("%s %d %s %d %s %d", "You have not scanned password strength since: ", sDay, "/", sMonth, "/", sYear);

                Row3_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                Row3_TextView1.setText(message);
            } else {
                Row3_ImageView1.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                Row3_TextView1.setText(R.string.strengthreportupdate);
            }
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database
    }

    /**
     * Purpose: Returns to the login activity if the user is inactive for a period of time.
     */
    private void openLoginActivity() {
        //instantiates new intent for activity list class
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        //adds intent flags
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //starts the intended activity
        startActivity(intent);
    }

    /**
     * Purpose: Creates pop up alert that has yes and no options.
     */
    public void YesNoPopup() {

        //adds new alert builder to home activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //sets alert message
        builder.setMessage("Are you sure you want to log out?");
        //sets builder cancelable
        builder.setCancelable(true);

        //returns positive button for yes
        //triggers logging out true if response is 1
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //closes dialog
                        dialog.cancel();
                        isLoggingOut = true;
                        openLoginActivity();
                    }
                });

        //returns negative button for no
        builder.setNegativeButton(
                "No",
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