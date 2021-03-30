package com.sss.safesecure.ui.list;

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
import android.widget.ListView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.sss.safesecure.CryptoClass;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.R;
import com.sss.safesecure.SQLiteDBHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.sss.safesecure.LoginActivity.keyClass;

public class ListFragment extends Fragment {

    /**
     * Purpose: Declares an SQLiteDBHelper class.
     * SQLiteDBHelper DBHelper.
     */
    private SQLiteDBHelper DBHelper;
    /**
     * Purpose: Instantiates a list of strings used to populate the list view.
     * List<String> dataList.
     */
    private final List<String> dataList = new ArrayList<>();
    /**
     * Purpose: Declaration of the list view.
     * ListView listView.
     */
    private ListView listView;
    /**
     * Purpose: Declaration of the search view.
     * SearchView searchView.
     */
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        ListViewModel listViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        //inflates the fragment to the view
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        //gets buttons from list activity by id
        Button btnHelp = root.findViewById(R.id.BtnHelp);

        //gets list view from list activity by id
        listView = root.findViewById(R.id.PWListView);

        //adds on click event to list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                //instantiates a string to store the selected item text
                String selectedFromList = (String) (listView.getItemAtPosition(position));

                //instantiates new intent for activity list class
                Intent intent = requireActivity().getIntent();
                //adds intent flags
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //send the website id to the view details activity
                intent.putExtra("WebsiteName", selectedFromList);

                //should navigate to the details fragment
                Navigation.findNavController(view).navigate(R.id.action_nav_list_to_nav_details);
            }
        });

        //TODO delete after testing
        //PopulateTestData();

        //calls the populate list view method
        try {
            PopulateListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //gets search view from list activity by id
        searchView = root.findViewById(R.id.SearchView);

        //adds on query text listener to the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                clearListView();
                try {
                    QueryListView(searchView.getQuery().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clearListView();
                try {
                    DynamicQueryListView(searchView.getQuery().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        //adds on click event to button help
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
     * Purpose: gets the secret key used for decrypting the database.
     */
    private String getSecretKey(){

        return keyClass[0].getKey();
    }

    /**
     * Purpose: Clears the current list view data.
     */
    private void clearListView(){
        //clears the list
        dataList.clear();
    }

    /**
     * Purpose: Populates the list view with data.
     */
    private void PopulateListView() throws Exception {

        //instantiates a new db helper -
        //  allows access to the methods in the SQLiteDBHelper class.
        DBHelper = new SQLiteDBHelper(getActivity());

        //calls the clear list view method
        clearListView();

        //cursor used to navigate entry data in table query
        Cursor cursor = DBHelper.Retrieve();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            //decrypts and assigns the cursor values to the string list - website
            dataList.add(decryptData(cursor.getString(1)));

            //instantiates an array adapter for a simple list item, with the data list values
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);

            //sets the adapter to the list view
            listView.setAdapter(adapter);
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

        if(cursor.getCount() == 0){
            cursor.close();     //closes the cursor
            DBHelper.close();   //closes the database
            popup("There are no entries to display.");
        }
    }

    /**
     * Purpose: this will display any data that contains the user query - it should update as the user types
     * @param query (String for the user query)
     */
    private void DynamicQueryListView(String query) throws Exception {

        //instantiates a new db helper -
        //  allows access to the methods in the SQLiteDBHelper class.
        DBHelper = new SQLiteDBHelper(getActivity());

        //cursor used to navigate entry data in table query
        Cursor cursor = DBHelper.Retrieve();

        //instantiates a list store decrypted website data
        List<String> decryptedList = new ArrayList<>();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            //assigns values to the decrypted data list
            decryptedList.add(decryptData(cursor.getString(1)));
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

        //iterates over each data entry in the decrypted list
        for(String data : decryptedList) {

            //if the data entry contains the user's query
            if(data.contains(query)){

                //assigns the values to the data list
                dataList.add(data);

                //instantiates an array adapter for a simple list item, with the data list values
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);

                //sets the adapter to the list view
                listView.setAdapter(adapter);
            }
        }
        //clears the list from memory
        decryptedList.clear();

    }

    /**
     * Purpose: this will display any data that contains the user query - runs when user submits query
     * @param query (String for the user query)
     */
    private void QueryListView(String query) throws Exception {

        //instantiates a new db helper -
        //  allows access to the methods in the SQLiteDBHelper class.
        DBHelper = new SQLiteDBHelper(getActivity());

        //cursor used to navigate entry data in table query
        Cursor cursor = DBHelper.Retrieve();

        //instantiates a list store decrypted website data
        List<String> decryptedList = new ArrayList<>();

        //while the cursor is moving through entries
        while (cursor.moveToNext()) {

            //assigns values to the decrypted data list
            decryptedList.add(decryptData(cursor.getString(1)));
        }
        cursor.close();     //closes the cursor
        DBHelper.close();   //closes the database

        //iterates over each data entry in the decrypted list
        for(String data : decryptedList) {

            //if the data entry contains the user's query
            if(data.contains(query)){

                //assigns the values to the data list
                dataList.add(data);

                //instantiates an array adapter for a simple list item, with the data list values
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);

                //sets the adapter to the list view
                listView.setAdapter(adapter);
            }
        }
        //clears the list from memory
        decryptedList.clear();

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
        popup("Start typing and the results will display in the list.");
        popup("To search click the magnifying glass icon.");
        popup("Click on a website name to view the full details.");
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

    /**
     * Purpose: Populates test data.
     */
    public void PopulateTestData() {

        String[] website = {"Website 1", "Website 2", "Website 3", "Website 4", "Website 5",
                "Website 6", "Website 7", "Website 8", "Website 9", "Website 10",
                "Reused PW 1", "Reused PW 2", "Reused PW 3", "Reused PW 4", "Reused PW 5",
                "Reused PW 6", "Reused PW 7", "Reused PW 8", "Reused PW 9", "Reused PW 10"};

        String[] email = {"Email 1", "Email 2", "Email 3", "Email 4", "Email 5",
                "Email 6", "Email 7", "Email 8", "Email 9", "Email 10",
                "Email 1", "Email 2", "Email 3", "Email 4", "Email 5",
                "Email 6", "Email 7", "Email 8", "Email 9", "Email 10"};

        String[] additional = {"Additional 1", "Additional 2", "Additional 3", "Additional 4",
                "Additional 5", "Additional 6", "Additional 7", "Additional 8",
                "Additional 9", "Additional 10", "Additional 1", "Additional 2",
                "Additional 3", "Additional 4", "Additional 5", "Additional 6",
                "Additional 7", "Additional 8", "Additional 9", "Additional 10"};

        String[] password = {"Password 1", "Password 2", "Password 3", "Password 4",
                "Password 5", "Password 6", "Password 7", "Password 8",
                "Password 9", "Password 10", "Password 1", "Password 2", "Password 3", "Password 4",
                "Password 5", "Password 6", "Password 7", "Password 8",
                "Password 9", "Password 10"};

        //instantiates a calender
        Calendar dateSaved = Calendar.getInstance();
        //subtracts 1 from calendar month
        dateSaved.add(Calendar.MONTH, -1);

        //instantiates a long to store the date in milliseconds
        long currentDateInMillis = dateSaved.getTimeInMillis();
        //instantiates a string to store the converted current date in milliseconds
        String dataTime = String.valueOf(currentDateInMillis);

        String[] dateTime = {dataTime, dataTime, dataTime,
                dataTime, dataTime, dataTime, dataTime, dataTime,
                dataTime, dataTime,dataTime, dataTime, dataTime,
                dataTime, dataTime, dataTime, dataTime, dataTime,
                dataTime, dataTime};

        //instantiates a new db helper -
        //  allows access to the methods in the SQLiteDBHelper class.
        DBHelper = new SQLiteDBHelper(getActivity());

        for(int i = 0; i < website.length; i++){

            //saves the data to the table in the database
            DBHelper.Create(website[i], email[i],
                    additional[i], password[i],
                    dateTime[i]);
        }

        DBHelper.close();
    }
}