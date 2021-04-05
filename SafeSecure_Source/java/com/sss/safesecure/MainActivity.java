package com.sss.safesecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Purpose: this activity is the main entry point to the application -
 * it draws the navigation menu and implements the mobile navigation.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    /**
     * Declaration of a boolean to control if the back button is pressed.
     */
    private boolean isBackPressed;
    /**
     * Instantiates a long to store the system time on pause.
     */
    private long timeOfPause = 0L;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set activity main layout
        setContentView(R.layout.activity_main);

        //instantiates a new tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiates a new drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //instantiates navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_create, R.id.nav_edit, R.id.nav_list, R.id.nav_reports,
                R.id.nav_faqs)
                .setDrawerLayout(drawer)
                .build();

        //instantiates navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //instantiates navigation UI
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Purpose: this method will return to the home fragment when the back button is pressed -
     * from fragments other than home.
     * It will do nothing when back button is pressed from the home fragment
     * - allowing the log out pop up to be displayed, and log out activity to be launched.
     */
    @Override
    public void onBackPressed() {
        if (!getBackPressed()) {
            super.onBackPressed();
        } else {
            Log.d("On Back Pressed", "Log out - pop up message should display");
        }
    }

    /**
     * Purpose: this method overrides the on pause method for the main activity.
     * It sets the time of pause to compare to when the app resumes.
     */
    @Override
    public void onPause(){
        super.onPause();

        timeOfPause = System.currentTimeMillis();
    }

    /**
     * Purpose: this method overrides the on resume method for the main activity.
     * It compares the time of pause to the time or resuming -
     * and opens the login activity if the time is longer than 5 minutes.
     */
    @Override
    public void onResume(){
        super.onResume();

        long timeOfResume = System.currentTimeMillis();

        if(timeOfResume > timeOfPause + 300000L && timeOfPause != 0L){
            timeOfPause = 0L;
            openLoginActivity();
        }
    }

    /**
     * Purpose: sets a boolean to return a value if the back button is pressed from the home fragment.
     * @param isPressed returns true or false.
     */
    public void setBackPressed(boolean isPressed){
        isBackPressed = isPressed;
    }
    /**
     * Purpose: gets the boolean value from the set back pressed method.
     */
    public boolean getBackPressed(){
        return isBackPressed;
    }

    /**
     * Purpose: Returns to the login activity if the user is inactive for a period of time.
     */
    private void openLoginActivity() {
        //instantiates new intent for activity list class
        Intent intent = new Intent(this, LoginActivity.class);
        //adds intent flags
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //starts the intended activity
        startActivity(intent);

        this.finish();
    }
}