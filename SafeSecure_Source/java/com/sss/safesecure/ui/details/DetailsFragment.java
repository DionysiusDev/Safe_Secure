package com.sss.safesecure.ui.details;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import androidx.navigation.Navigation;
import com.sss.safesecure.CryptoClass;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;

import java.util.Objects;

import static com.sss.safesecure.LoginActivity.keyClass;

public class DetailsFragment extends Fragment {

    /**
     * Purpose: instantiates a boolean to handle data validation before saving.
     * boolean isValidated.
     */
    private static boolean isValidated;
    /**
     * Purpose: instantiates a boolean to check if the user clicks ok after saving.
     * boolean isOk.
     */
    private boolean isOk = false;
    /**
     * Purpose: instantiates a boolean to control the deletion of data.
     * boolean deleteEntry.
     */
    private static boolean deleteEntry = false;
    /**
     * Purpose: instantiates a boolean to check if the text fields are editable.
     * boolean isEditable.
     */
    private static boolean isEditable;
    /**
     * Purpose: Declares and integer to store the site id
     * - that has been put extra from the list activity.
     * int siteId.
     */
    private int siteId;
    /**
     * Purpose: Declares an SQLiteDBHelper class.
     * SQLiteDBHelper DBHelper.
     */
    private SQLiteDBHelper DBHelper;
    /**
     * Purpose: declaration of layout edit text fields.
     * EditText passwordEditText, websiteEditText, emailEditText, AdditionalInfoEditText.
     */
    private EditText wsText, emText, adText, pwText;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        DetailsViewModel detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        //inflates the fragment to the view
        root = inflater.inflate(R.layout.fragment_details, container, false);

        //gets the edit text fields by id
        wsText = root.findViewById(R.id.WebsiteNameText);
        emText = root.findViewById(R.id.EmailText);
        adText = root.findViewById(R.id.AdditionalInfoText);
        pwText = root.findViewById(R.id.PasswordText);

        receiveSiteId();
        disableEditTextFields();

        //gets buttons from the layout by their id
        //gets buttons from the layout by their id
        Button btnEdit = root.findViewById(R.id.BtnEdit);
        Button btnSave = root.findViewById(R.id.BtnSave);
        Button btnDelete = root.findViewById(R.id.BtnDelete);
        Button btnHelp = root.findViewById(R.id.BtnHelp);
        //Button btnBack = root.findViewById(R.id.BtnBack);

        //handles interaction with the edit button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditTextFields();
            }
        });
        //handles interaction with the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
        //handles interaction with the delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YesNoPopup();
            }
        });
        //handles interaction with the help button
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
     * Purpose: Disables the edit text fields so they can't be edited.
     */
    private void disableEditTextFields(){

        //disables editing text for the field
        wsText.setEnabled(false);
        emText.setEnabled(false);
        adText.setEnabled(false);
        pwText.setEnabled(false);

        isEditable = false;
    }

    /**
     * Purpose: Enables the edit text fields so they can be edited.
     */
    private void enableEditTextFields(){
        //enables editing text for the field
        wsText.setEnabled(true);
        emText.setEnabled(true);
        adText.setEnabled(true);
        pwText.setEnabled(true);

        isEditable = true;
    }

    /**
     * Purpose: resets the edit text field text to empty.
     */
    private void resetEditTextFields(){

        //sets the edit text fields text
        wsText.setText("");
        emText.setText("");
        adText.setText("");
        pwText.setText("");
    }

    /**
     * Purpose: Gets the site id from the list activity.
     */
    private void receiveSiteId(){
        //get the intent put extra from the list activity class
        Intent intent = requireActivity().getIntent();

        //stores the values of the string extra
        String siteName = intent.getStringExtra("WebsiteName");

        try {
            getSiteDetails(siteName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Purpose: Gets the site details from the database based on ID.
     */
    private void getSiteDetails(String siteName) throws Exception {

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(getActivity());

        //cursor used to navigate entry data in table query
        Cursor cursor = DBHelper.Retrieve();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            if(decryptData(cursor.getString(1)).equals(siteName)){

                //sets the edit text fields text
                wsText.setText(decryptData(cursor.getString(1)));
                emText.setText(decryptData(cursor.getString(2)));
                adText.setText(decryptData(cursor.getString(3)));
                pwText.setText(decryptData(cursor.getString(4)));

                siteId = cursor.getInt(0);
            }
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

    }

    /**
     * Purpose: gets the secret key for use with encrypting / decrypting the database.
     */
    private String getSecretKey(){

        return keyClass[0].getKey();
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
            popup("A password is required.");
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
            SQLiteDBHelper DBHelper = new SQLiteDBHelper(getActivity());

            //instantiates a long to store the current date in milliseconds
            long currentDateInMillis = System.currentTimeMillis();
            //instantiates a string to store the converted current date in milliseconds
            String dataTime = String.valueOf(currentDateInMillis);

            //updates the data to the table in the database
            DBHelper.Update(siteId, website, email, additional, password, dataTime);

            DBHelper.close();

            //display notification to user
            popup("Password information saved.");
            isOk = true;
        }
    }

    /**
     * Purpose: Deletes the current entry from the database.
     */
    private void deleteEntry(){

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(getActivity());
        //deletes the row from the table
        DBHelper.Delete(siteId);

        DBHelper.close();
        //display notification to user
        popup("Password information deleted.");
        isOk = true;
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
    private void DisplayHelp(){
        popup("Click the back arrow at the top left to return to the list.");
        popup("Click the delete button to delete an entry.");
        popup("Click the save button to save any changes.");
        popup("Click the edit button to edit the details.");
    }

    /**
     * Purpose: Creates pop up alerts.
     * @param msg (String for the message to display to user)
     */
    private void popup(String msg) {

        //adds new alert builder to home activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            //should navigate to the list fragment
                            Navigation.findNavController(root).navigate(R.id.action_nav_details_to_nav_list);
                        }
                    }
                });

        //creates alert dialog
        AlertDialog alert = builder.create();

        //shows alert dialog
        alert.show();
    }

    /**
     * Purpose: Creates pop up alert that has yes and no options.
     */
    private void YesNoPopup() {

        //adds new alert builder to home activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //sets alert message
        builder.setMessage("Are you sure you want to delete this entry?");
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
                        //sets delete entry true if yes is selected
                        deleteEntry = true;
                        //calls delete method
                        deleteEntry();
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