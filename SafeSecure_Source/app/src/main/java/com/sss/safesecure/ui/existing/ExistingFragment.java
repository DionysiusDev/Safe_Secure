package com.sss.safesecure.ui.existing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.MainActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;

public class ExistingFragment extends Fragment {

    /**
     * Purpose: instantiates a boolean to handle data validation before saving.
     * boolean isValidated.
     */
    private static boolean isValidated;
    /**
     * Purpose: instantiates a boolean to check if the user clicks ok after saving.
     * boolean isOk.
     */
    private boolean isOk;
    private EditText wsText, emText, adText, pwText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        ExistingViewModel existingViewModel = new ViewModelProvider(this).get(ExistingViewModel.class);

        //inflates the fragment to the view
        View root = inflater.inflate(R.layout.fragment_exist, container, false);

        //gets the edit text fields by id
        wsText = root.findViewById(R.id.WebsiteNameText);
        emText = root.findViewById(R.id.EmailText);
        adText = root.findViewById(R.id.AdditionalInfoText);
        pwText = root.findViewById(R.id.PasswordText);

        //gets the buttons by id
        Button btnSave = root.findViewById(R.id.BtnSave);
        //adds on click event to button save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        //gets the buttons by id
        Button btnHelp = root.findViewById(R.id.BtnHelp);
        //adds on click event to button save
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayHelp();
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
     * Purpose: validates data and calls the save method.
     */
    private void ValidateData() {

        //stores the text from the text views in string variables
        String strWS = wsText.getText().toString().trim();
        String strEm = emText.getText().toString().trim();
        String strAdd = adText.getText().toString().trim();
        String strPW = pwText.getText().toString().trim();

        //if the website text field is empty
        if (TextUtils.isEmpty(strWS)) {
            //display notification to user
            popup("Please enter the website the password is for - this helps if you forget later.");
            return;
        }
        //if the email text field is empty
        if (TextUtils.isEmpty(strEm)) {
            //display notification to user
            popup("Please enter the email for the website - this helps if you forget later.");
            return;
        }
        //if the additional info text field is empty
        if (TextUtils.isEmpty(strAdd)) {

            //set a default value
            strAdd = "No Additional Information";
        }
        //if the email text field is empty
        if (TextUtils.isEmpty(strPW)) {
            //display notification to user
            popup("Please enter an existing password.");
            return;
        }
        //checks if the text fields are not empty
        if (!TextUtils.isEmpty(strWS) && !TextUtils.isEmpty(strEm)
                && !TextUtils.isEmpty(strPW)) {
            isValidated = true;

            //calls the save data method and encrypts the data.
            SaveData(strWS, strEm, strAdd, strPW);
        }
    }

    /**
     * Purpose: Saves user data to the database.
     * @param website (String website)
     * @param email (String email)
     * @param additional (String additional)
     * @param password (String password)
     */
    private void SaveData(String website, String email, String additional, String password){

        if(isValidated){
            //creates new dbHelper - allows access to the SQLiteDBHelper methods
            //reference to the SQLiteDBHelper class
            SQLiteDBHelper DBHelper = new SQLiteDBHelper(getContext());

            //instantiates a long to store the current date in milliseconds
            long currentDateInMillis = System.currentTimeMillis();
            //instantiates a string to store the converted current date in milliseconds
            String dataTime = String.valueOf(currentDateInMillis);

            //saves the data to the table in the database
            DBHelper.Create(website, email, additional, password, dataTime);

            DBHelper.close();

            //display notification to user
            popup("Password information saved.");
            isOk = true;
        }
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
                        if(isOk){
                            openMainActivity();
                        }
                    }
                });

        //creates alert dialog
        AlertDialog alert = builder.create();

        //shows alert dialog
        alert.show();
    }

    /**
     * Purpose: Displays help messages to the user.
     */
    private void DisplayHelp(){
        popup("Click the save button to save the data.");
        popup("Enter an existing password.");
        popup("Enter any additional information - like a user name.");
        popup("Enter the email for the website.");
        popup("Enter the website the password is for.");
    }

    /**
     * Purpose: Returns to the main activity.
     */
    private void openMainActivity() {
        //instantiates new intent for activity list class
        Intent intent = new Intent(getActivity(), MainActivity.class);
        //adds intent flags
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //starts the intended activity
        startActivity(intent);
    }
}