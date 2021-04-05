package com.sss.safesecure.ui.create;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.MainActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;

import java.util.Random;

/**
 * Purpose: this class allows the user to create a new entry.
 */
public class CreateFragment extends Fragment {

    /**
     * Purpose: instantiates a boolean to handle data validation before saving.
     * boolean isValidated.
     */
    private static boolean isValidated;
    /**
     * Purpose: instantiates a boolean to handle password generation.
     * boolean isGenerated.
     */
    private static boolean isGenerated;
    /**
     * Purpose: instantiates a boolean to check if the user clicks ok after saving.
     * boolean isOk.
     */
    private boolean isOk;
    /**
     * Declaration of edit text fields used for displaying data to the user.
     */
    private EditText wsText, emText, adText, pwText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        CreateViewModel createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);

        //inflates the fragment to the view
        View root = inflater.inflate(R.layout.fragment_create, container, false);

        //gets the edit text fields by id
        wsText = root.findViewById(R.id.WebsiteNameText);
        emText = root.findViewById(R.id.EmailText);
        adText = root.findViewById(R.id.AdditionalInfoText);
        pwText = root.findViewById(R.id.PasswordText);
        //disables editing text for the field
        pwText.setEnabled(false);

        //these methods update the text in the edit text fields on text change
        createViewModel.getWsText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                wsText.setText(s);
            }
        });
        createViewModel.getEmText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                emText.setText(s);
            }
        });
        createViewModel.getAdText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                adText.setText(s);
            }
        });
        createViewModel.getPwText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                pwText.setText(s);
            }
        });

        //gets the button from the fragment layout by id
        Button btnGenerate = root.findViewById(R.id.BtnGenerate);
        //adds on click event to button generate
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isGenerated){
                    createPassword();
                }
            }
        });

        Button btnSave = root.findViewById(R.id.BtnSave);
        //adds on click event to button save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        Button btnCopyPw = root.findViewById(R.id.BtnCopyPw);
        //adds on click event to button copy password
        btnCopyPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyPassword();
            }
        });

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
     * Purpose: Creates a new password for the user.
     * Calls the generate password method and displays the password to the user.
     */
    private void createPassword(){

        //instantiates a string to store the random password
        String pw = genPw();

        if(isGenerated){
            //clear the current password text
            if(!TextUtils.isEmpty(pwText.getText())) {
                pwText.setText("");
            }
            //checks if the current password text is empty
            if(TextUtils.isEmpty(pwText.getText())) {
                //sets the password field text - displays the generated password
                pwText.setText(pw);

                isGenerated = false;
            }
        }
    }

    /**
     * Purpose: generates a random password.
     */
    private String genPw() {

        //instantiates various strings for selecting password characters from
        String pwUpper = "ABCDEFGHJKLOMOPQRSTUVWXYZ";
        String pwLower = "abcdefghijkmnopqrstuvwxyz";
        String pwChars = "!@#$?^&*";
        String pwNums = "0123456789";

        //generates random combination
        final Random random = new Random();
        //builds string at length of parameter
        final StringBuilder stringbuilder = new StringBuilder(5);

        String PW = "";

        try{
            //iterates over the pwLength length
            for (int length = 0; length < 5; length++) {

                //appends random characters from the character strings to the string builder
                stringbuilder.append(pwUpper.charAt(random.nextInt(pwUpper.length())));
                stringbuilder.append(pwNums.charAt(random.nextInt(pwNums.length())));
                stringbuilder.append(pwChars.charAt(random.nextInt(pwChars.length())));
                stringbuilder.append(pwLower.charAt(random.nextInt(pwLower.length())));
            }
            //formats the password
            PW = String.format(stringbuilder.toString(), pwUpper + pwChars + pwNums + pwLower + pwChars);

        } catch(Exception e){
            //tries again
            isGenerated = false;
            createPassword();
            e.printStackTrace();
        }
        //assigns value to the new pw string
        //if pw length is larger than 15 - create a sub string from character 0 - 15 of the pw string
        String newPW =  (PW.length() > 15) ? PW.substring(0, 15) : PW ;

        //sets is generated to true
        isGenerated = true;

        //returns the new password
        return newPW;
    }

    /**
     * Purpose: allows the user to copy the generated password -
     * for pasting into websites or editing entries.
     */
    @SuppressWarnings("deprecation")
    private void copyPassword(){

        //instantiates a string to store the password text
        String textToCopy = pwText.getText().toString();

        //checks if the password field is empty
        if(!TextUtils.isEmpty(textToCopy)){

            int sdk = android.os.Build.VERSION.SDK_INT;

            //checks for os version compatibility
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                //instantiates a new clipboard manager service
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);

                //instantiates a new clip data variable and assigns the text to copy
                ClipData clipData = ClipData.newPlainText("Password Text", textToCopy);
                //adds the clip data to the clipboard
                clipboard.setPrimaryClip(clipData);

                //displays a notification to the user
                Toast.makeText(getActivity(), "Password copied to clipboard",
                        Toast.LENGTH_SHORT).show();

            } else if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {

                //instantiates a new clipboard manager service
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                //adds the password text to the clipboard
                clipboard.setText(textToCopy);

                //displays a notification to the user
                Toast.makeText(getActivity(), "Password copied to clipboard",
                        Toast.LENGTH_SHORT).show();

            } else {
                //instantiates a new clipboard manager service
                ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);

                //instantiates a new clip data variable and assigns the text to copy
                ClipData clipData = android.content.ClipData.newPlainText("Password Text", textToCopy);
                //adds the clip data to the clipboard
                clipboard.setPrimaryClip(clipData);

                //displays a notification to the user
                Toast.makeText(getActivity(), "Password copied to clipboard",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            popup("The password field is empty.");
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
            popup("Please click generate to create a new password.");
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
     * Purpose: Displays help messages to the user.
     */
    private void DisplayHelp(){
        popup("Click the copy button to copy the password.");
        popup("Click the save button to save the data.");
        popup("Click the generate button to generate a password.");
        popup("Enter any additional information - like a user name.");
        popup("Enter the email for the website.");
        popup("Enter the website the password is for.");
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