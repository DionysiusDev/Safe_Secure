package com.sss.safesecure.ui.faqs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.sss.safesecure.LoginActivity;
import com.sss.safesecure.R;
import java.util.ArrayList;
import java.util.List;

public class FaqsFragment extends Fragment {

    /**
     * Purpose: Instantiates a list of strings used to populate the list view with faqs.
     * List<String> faqList.
     */
    private final List<String> faqList = new ArrayList<>();
    /**
     * Purpose: declares a list view to display faqs.
     */
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //creates the view model class object
        FaqsViewModel faqsViewModel = new ViewModelProvider(this).get(FaqsViewModel.class);

        //inflates the fragment to the view
        View root = inflater.inflate(R.layout.fragment_faqs, container, false);

        //gets list view from faqs activity by id
        listView = root.findViewById(R.id.PWListView);

        //populates the list view with the faqs types
        populateFaqsList();

        //adds on click event to list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (position == 0) {

                    popup("The files that store your data can only be unlocked by the master code.");
                    popup("If you forget your code you cannot access your data.");
                    popup("The code is created by you when your first login.");
                    popup("For added security each install of Safe and Secure is protected by a master code.");
                }
                if (position == 1) {
                    popup("You can uninstall and re-install the app for a fresh start.");
                    popup("For added security Safe and Secure does not allow for resetting of codes.");
                }
                if (position == 2) {

                    popup("As an added security measure, the only way to access your data is with the code you created.");
                    popup("You are the only person that can access your Safe and Secure.");
                }
                if (position == 3) {

                    popup("Safe and Secure stores all data locally on your device.");
                    popup("Safe and Secure will run without internet connection.");
                    popup("No, none of your passwords or website details are stored online.");
                }
                if (position == 4) {

                    popup("All your data is stored securely in your own database.");
                    popup("After pass code confirmation, a database is created for you.");
                    popup("Safe and Secure stores all data locally on your device.");
                }
                if (position == 5) {

                    popup("The only way to decrypt your data is by logging in with your pass code.");
                    popup("All data is encrypted with the master key before being stored in your database.");
                    popup("Your pass code was used to create a master key.");
                    popup("Safe and Secure stores all data as securely as possible.");
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
     * Purpose: assigns values to the faqs list.
     */
    private void populateFaqsList(){

        //populates the faqs list
        faqList.add("What happens if I lose forget my pass code?");
        faqList.add("Can I reset my pass code?");
        faqList.add("Who else has access to my Safe and Secure?");
        faqList.add("Does any of my data get stored online?");
        faqList.add("How is my data stored?");
        faqList.add("How is my data secured?");

        populateListView(faqList);
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
     * Purpose: Creates pop up alerts.
     *
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
                    }
                });

        //creates alert dialog
        AlertDialog alert = builder.create();

        //shows alert dialog
        alert.show();
    }
}