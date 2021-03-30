package com.sss.safesecure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Purpose: this class allows the user to create a pass code to access the app.
 * it also generates a secret key for the user to encrypt and decrypt their data.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Purpose: Declares an SQLiteDBHelper class reference.
     * SQLiteDBHelper DBHelper.
     */
    private SQLiteDBHelper DBHelper;
    private CryptoClass crypto;

    /**
     * Purpose: Instantiates a integer to store the max user password entries allowed.
     * int maxEntries.
     */
    private static final int maxEntries = 1;
    /**
     * Purpose: Instantiates a integer to count the max user password entries allowed.
     * int count.
     */
    private int count = 0;
    /**
     * instantiates an array of p code data class objects.
     * PCodeDataClass[] pcodeClass.
     */
    public final PCodeDataClass[] pcodeClass = new PCodeDataClass[maxEntries];
    /**
     * instantiates an array of sec Key Class data class objects.
     * SecKeyClass[] keyClass.
     */
    public static final SecKeyClass[] keyClass = new SecKeyClass[maxEntries];
    /**
     * Purpose: instantiates a boolean to check if the user has entered a pass code.
     * boolean passcodeEntered.
     */
    private boolean passcodeEntered = false;
    /**
     * Purpose: instantiates a boolean to check if the data base has any entries.
     * boolean dbData.
     */
    private boolean dbData = false;
    /**
     * Purpose: instantiates a boolean to handle logging out of application.
     * boolean loggingOut.
     */
    private static boolean loggingOut = false;
    /**
     * Purpose: declares a boolean to check if pass code characters are accepted.
     * boolean isCharAccepted.
     */
    private boolean isCharAccepted;
    /**
     * Purpose: declares a boolean to check if pass code characters are validated.
     * boolean isCodeValidated.
     */
    private boolean isCodeValidated;
    /**
     * Purpose: declaration of layout edit text fields.
     * EditText C, O, D, E.
     */
    private EditText C, O, D,E;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //displays a message to the user
        Toast.makeText(this, "Make sure to remember your code!\n" +
                "Your code is the only way to decrypt and access your data!", Toast.LENGTH_LONG).show();

        //instantiates a new secKey class object - sets the secret key empty.
        keyClass[0] = new SecKeyClass("");

        //gets text views from page layout by id
        C = findViewById(R.id.code1);
        O = findViewById(R.id.code2);
        D = findViewById(R.id.code3);
        E = findViewById(R.id.code4);

        //text watchers should move to the next text field if one character is entered
        C.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int textlength = C.getText().length();

                if (textlength > 0) {
                    isCharAccepted = false;
                    //calls the validate character method
                    validateCharacter(C.getText().toString());

                    if(isCharAccepted){
                        C.clearFocus();
                        O.requestFocus();
                    } else {
                        popup("Invalid Character! Please re-enter field.");
                        C.setText("");
                        C.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
        O.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int textlength = O.getText().length();

                if (textlength > 0) {
                    isCharAccepted = false;
                    //calls the validate character method
                    validateCharacter(O.getText().toString());

                    if(isCharAccepted){
                        O.clearFocus();
                        D.requestFocus();
                    } else {
                        popup("Invalid Character! Please re-enter field.");
                        O.setText("");
                        O.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
        D.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int textlength = D.getText().length();

                if (textlength > 0) {
                    isCharAccepted = false;
                    //calls the validate character method
                    validateCharacter(D.getText().toString());

                    if(isCharAccepted){
                        D.clearFocus();
                        E.requestFocus();
                    } else {
                        popup("Invalid Character! Please re-enter field.");
                        D.setText("");
                        D.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
        E.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int textlength = E.getText().length();

                if (textlength > 0) {
                    isCharAccepted = false;
                    //calls the validate character method
                    validateCharacter(E.getText().toString());

                    if(isCharAccepted){
                        isCodeValidated = true;
                    } else {
                        popup("Invalid Character! Please re-enter field.");
                        E.setText("");
                        E.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });

        final Button BtnLogin, BtnHelp;

        //instantiates button from login activity
        BtnLogin = findViewById(R.id.BtnLogin);
        //adds on click event to button login
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!passcodeEntered && !dbData) {
                    //tracks un-successful login attempts
                    count = count + 1;
                    enterPasscode();
                }
                if (passcodeEntered && !dbData) {
                    E.clearFocus();
                    C.requestFocus();
                    confirmPasscode();
                }
            }
        });

        //instantiates help button from login activity
        BtnHelp = findViewById(R.id.BtnHelp);
        //adds on click event to button help
        BtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayHelp();
            }
        });
    }

    /**
     * Purpose: gets the pass code the user enters -
     * checks if it contains unacceptable characters -
     * if not it stores it temporarily or else it displays message to user.
     */
    private void enterPasscode() {

        if(!TextUtils.isEmpty(C.getText().toString())
                && !TextUtils.isEmpty(O.getText().toString())
                && !TextUtils.isEmpty(D.getText().toString())
                && !TextUtils.isEmpty(E.getText().toString()))
        {
            //assigns value to the new pass code string
            String newPasscode = C.getText().toString()
                    + O.getText().toString()
                    + D.getText().toString()
                    + E.getText().toString();

            if (isCodeValidated) {

                //instantiates a new p code class object - sets the pass code value to temp memory.
                pcodeClass[0] = new PCodeDataClass(newPasscode);

                //calls the get existing pass code method
                getExistingPasscode();

            } else {
                //resets booleans for entering new pass code
                passcodeEntered = false;
                dbData = false;

                //checks if the window is closing
                if (! isFinishing()) {

                    //displays message to user
                    popup("Only enter:\nlower case letters.\nOR UPPER CASE LETTERS." +
                            "\nOr Numbers [0-9].");
                    popup("Please enter a different code.");
                    resetTextFields();
                }
            }
        } else{
            //checks if the window is closing
            if (! isFinishing()) {
                popup("Please enter a 4 digit code!");
                resetTextFields();
            }
        }
    }

    /**
     * Purpose: resets the text fields for re-entering / confirming first time pass codes.
     */
    private void resetTextFields(){
        //resets the text fields to empty
        C.setText("");
        O.setText("");
        D.setText("");
        E.setText("");
    }

    /**
     * Purpose: checks for invalid characters in the user code.
     * @param input the user entered character.
     */
    private void validateCharacter(String input) {

        //declares a character to check the characters in the code string
        char character;

        //checks each character at the index
        character = input.charAt(0);

        //checks if the character is lower case, upper case or a digit.
        //the character is accepted
        //the character is not accepted
        isCharAccepted = Character.isLowerCase(character) || Character.isUpperCase(character)
                || Character.isDigit(character);
    }

    /**
     * Purpose: Checks if data and database file exists -
     * otherwise it gets first time users to verify pass code.
     */
    private void getExistingPasscode() {

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(this);

        //calls the db helper method
        if(DBHelper.checkDataBase()){

            //cursor used to navigate entry data in table query
            Cursor cursor = DBHelper.getDataById("LoginInfo", 1);

            while(cursor.moveToNext()) {

                //there is data so this is set to true
                dbData = true;
                passcodeEntered = true;

                Log.i("getExistingPasscode", "Trying to retrieve pass code");

                //calls verify pass code method and passes the cursor value as arguments
                verifyPasscode(cursor.getString(1));
            }
            cursor.close();     //closes the cursor
            DBHelper.close();   //closes the database

        } else{
            passcodeEntered = true;
            dbData = false;
            //reset the fields to confirm password and continue with authentication
            resetFields("Confirm");
        }
    }

    /**
     * Purpose: Verifies the user pass code -
     * this method runs when the database exists.
     */
    private void verifyPasscode(String code) {

        //utilises text utilities - checks if the text views are not empty
        if (!TextUtils.isEmpty(C.getText().toString())
                && !TextUtils.isEmpty(O.getText().toString())
                && !TextUtils.isEmpty(D.getText().toString())
                && !TextUtils.isEmpty(E.getText().toString())) {

            //creates a string to compare the original and confirmation passcodes
            String passcode2 = C.getText().toString()
                    + O.getText().toString()
                    + D.getText().toString()
                    + E.getText().toString();

            //creates a secret key with the users pass code
            createSecretKey(pcodeClass[0].getPC());

            //decrypts and stores the users code
            String decryptedCode = decryptData(code);

            if(crypto.decodedKey()) {
                //Checks that the unencrypted password matches one that has previously been hashed
                if (BCrypt.checkpw(passcode2, decryptedCode)) {
                    //displays a message to the user
                    Toast.makeText(this, "Login Successful! ", Toast.LENGTH_SHORT).show();
                    openMainActivity(); //opens the main activity
                }
            } else {
                //displays a message to the user
                Toast.makeText(this, "Un-Successful Login Attempts: " + count, Toast.LENGTH_SHORT).show();

                //resets the booleans so pass code can be re-entered
                passcodeEntered = false;
                dbData = false;

                //closes the activity if the unsuccessful login attempts equals 3
                if (count == 3) {
                    this.finish();
                }
            }
        }  else {
            //checks if the window is closing
            if (! isFinishing()) {
                //displays message to the user
                popup("Please Enter 4 Digit Pass code!");
                resetTextFields();
            }
        }
    }

    /**
     * Purpose: Confirms the user pass code -
     * this method runs on first time application use.
     */
    private void confirmPasscode() {

        //utilises text utilities - checks if the text views are not empty
        if (!TextUtils.isEmpty(C.getText().toString())
                && !TextUtils.isEmpty(O.getText().toString())
                && !TextUtils.isEmpty(D.getText().toString())
                && !TextUtils.isEmpty(E.getText().toString())) {

            //string array stores the user confirmation passcode
            String passcode2 = C.getText().toString()
                    + O.getText().toString()
                    + D.getText().toString()
                    + E.getText().toString();

            //gets user pass code from data class
            String userPassCode = pcodeClass[0].getPC();

            //Hashes a password for the first time
            String hashed = BCrypt.hashpw(userPassCode, BCrypt.gensalt());

            // Check that the entered password matches one that has
            // previously been hashed
            if (BCrypt.checkpw(passcode2, hashed)) {

                //call the create secret key method
                createSecretKey(userPassCode);

                //displays a message to the user
                Toast.makeText(this, "Pass Code Confirmed! ", Toast.LENGTH_SHORT).show();

                //creates a database
                createDB();

                //saves hashed pass code to the database.
                saveToDB(hashed);

                //opens the main activity
                openMainActivity();
            } else {
                popup("Pass Code does not match!");
            }
        }
    }

    /**
     * Purpose: generates a 32 byte random secret key for encrypting and decrypting data.
     * stores the secret key in the keyClass.
     * @param userCode the pass code entered by the user.
     */
    private void createSecretKey(String userCode) {

        //instantiates strings to store the encoded user pass code -
        //  this is required to return the same result each time it is encoded.
        String encodeUserPassCode1 = String.valueOf((userCode.hashCode() * 13));
        String encodeUserPassCode2 = String.valueOf((userCode.hashCode() * 7));
        String encodeUserPassCode3 = String.valueOf((userCode.hashCode() * 3));

        //instantiates a string to store a thirty two byte code
        String secretKey = encodeUserPassCode1
                + encodeUserPassCode2 + encodeUserPassCode3 + "SSDZH2020";

        //instantiates a new crypto class object - allows access to methods.
        crypto = new CryptoClass();
        //calls the count bytes of string method to verify the secret key size is 32 bytes
        crypto.countBytesOfString(secretKey);

        if (crypto.encodedKey()) {
            //instantiates a new secKey class object - sets the secret key to temp memory.
            keyClass[0] = new SecKeyClass(secretKey);

            //resets user pass code to empty
            pcodeClass[0].setPCInfo("");
        }
    }

    /**
     * Purpose: decrypts data from the database before displaying to the user.
     * @param dataToDecrypt the data to decrypt.
     * @return the decrypted data for displaying to the user.
     */
    private String decryptData(String dataToDecrypt){
        //instantiates a new crypto class object - allows access to methods.
        crypto = new CryptoClass();

        //instantiates a string to store the decrypted data
        return crypto.decrypt(dataToDecrypt, keyClass[0].getKey());
    }

    /**
     * Purpose: Creates a new database for the first time user once the login is verified.
     */
    private void createDB(){
        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(this);

        //creates a new database
        try {
            DBHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBHelper.addFirstReportDates();

        DBHelper.close();
    }

    /**
     * Purpose: Saves the user pass code to the database table.
     * @param code the users login code after encrypting.
     */
    private void saveToDB(String code) {

        String salt  = BCrypt.gensalt();

        //creates new dbHelper - allows access to the SQLiteDBHelper methods
        DBHelper = new SQLiteDBHelper(this);

        //calls the save data method and saves the pass code and salt
        DBHelper.saveCodeData(code, salt);

        DBHelper.close();
    }

    /**
     * Purpose: resets text fields depending on entry of confirmation status.
     */
    private void resetFields(String Type) {

        //gets text views from page layout by id
        C = findViewById(R.id.code1);
        O = findViewById(R.id.code2);
        D = findViewById(R.id.code3);
        E = findViewById(R.id.code4);

        //gets button from home activity by id
        Button BtnLogin = findViewById(R.id.BtnLogin);

        //if the parameter string equals enter this sets the enter text views
        if (Type.equals("Enter")) {

            //sets text view text
            C.setText("C");
            O.setText("O");
            D.setText("D");
            E.setText("E");

            //gets and sets text view header
            TextView header = findViewById(R.id.textViewPasscodeHeader);
            header.setText(R.string.enterpasscode);

            //sets button text
            BtnLogin.setText(R.string.login);

        }   //if the parameter string equals confirm this sets the enter text views
        if (Type.equals("Confirm")) {

            //sets text view text
            C.setText("");
            O.setText("");
            D.setText("");
            E.setText("");

            //gets and sets text view header
            TextView header = findViewById(R.id.textViewPasscodeHeader);
            header.setText(R.string.confirmpc);

            //sets button text
            BtnLogin.setText(R.string.btnconfirm);
        }
    }

    /**
     * Purpose: Displays help messages to the user.
     */
    private void DisplayHelp(){
        popup("It is important to remember your code!" +
                "\nIf you forget your code you will lose all access!.");
        popup("Enter a 4 digit code.\n" +
                "Click login.\nYou will be asked to confirm your code." +
                "\nOnce verified you will be able to log in with your code.");
    }

    /**
     * Purpose: displays pop up alerts to the user.
     */
    private void popup(String msg) {

        //adds new alert builder to home activity
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    /**
     * Purpose: launches the main activity.
     */
    private void openMainActivity() {
        //instantiates new intent for activity home class
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //send the the secret key to the main activity.
        intent.putExtra("SecretKey", keyClass[0].getKey());

        //starts the intended activity
        startActivity(intent);
    }
}
