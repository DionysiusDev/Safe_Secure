package com.sss.safesecure;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Purpose: this class displays the welcome splash screen and allows the user to accept permissions -
 * it then launches the login activity.
 */
public class IntroSplashScreen extends AppCompatActivity {

    /**
     * Purpose: instantiates an integer to store request code -
     * used to verify permission granted.
     * int REQUEST_CODE.
     */
    public static final int REQUEST_CODE = 1;
    /**
     * Purpose: Declares an image view from the layout -
     * used for the splash screen.
     * ImageView splashBG.
     */
    private ImageView splashBG;

    boolean isfading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        InitialiseView();    // draws the background image
        permissionRequest();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(isfading){
                    //sets back ground image alpha to invisible
                    splashBG.getBackground().setAlpha(0);
                }
            }
        });
    }

    /**
     * Request permission to access the users media files
     * Might need a different method for newer API's 23 and aboves
     */
    private void permissionRequest() {

        // ContextCompat use to retrieve resources. It provide uniform interface to access resources.
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(IntroSplashScreen.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        }
        else{
            FadeOutAnimation();
            splashScreenThread();
        }
    }

    /**
     * Verify the permission request
     * @param requestCode the request code.
     * @param permissions the permissions required.
     * @param grantResults returns true of false.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FadeOutAnimation();
                splashScreenThread();
            } else {

                // If user denys permission re-request permissions send request again
                ActivityCompat.requestPermissions(IntroSplashScreen.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    /**
     * Will stay on splash screen until permission is granted
     */
    private void AfterPermissionGrantedChangeIntent() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent); // start main activity
        finish(); // destroy activity
    }

    /**
     * Instantiate the image view component for the splash screen
     */
    private void InitialiseView() {
        splashBG = findViewById(R.id.splashBG);
    }

    /**
     * Fade out image view animation method
     */
    private void FadeOutAnimation() {
        Animation aniFadeInOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        splashBG.startAnimation(aniFadeInOut);
    }

    /**
     * This will instantiate a thread use for how long the splash screen runs
     */
    private void splashScreenThread() {
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(1950);                      // wait 2 seconds then,
                    isfading = true;
                    AfterPermissionGrantedChangeIntent();   // switch intent to login activity

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashThread.start(); // start the thread
    }
}

