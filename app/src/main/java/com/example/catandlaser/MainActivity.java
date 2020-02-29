package com.example.catandlaser;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.DisplayMetrics;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Variables
    // Values/helper variables
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    int width = displayMetrics.widthPixels;
    double height = displayMetrics.heightPixels;
    private int[] location = new int[2];
    
    private Random rand = new Random();
    private Boolean lock1 = false, lock2 = false, lock3 = false;

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor light;

    // Assets on screen
    private TextView laser;
    private ImageView catPawR, catPawL, catHead;
    private ImageView print1, print2, print3;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        laser = findViewById(R.id.laserDot);

        catHead = findViewById(R.id.catHead);
        catPawR = findViewById(R.id.catPawRight);
        catPawL = findViewById(R.id.catPawLeft);

        print1 = findViewById(R.id.pawPrint1);
        print2 = findViewById(R.id.pawPrint2);
        print3 = findViewById(R.id.pawPrint3);

        // Get an instance of the sensor service, then use that to get an instance of the light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else {
            finish();
        }
        // Hide top and bottom phone bars
        hideStatusBar();
    }

    /**
     * Registers a listener for the sensor
     */
    @Override
    protected void onResume() {
        // Register a listener for the sensor
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregisters the sensor when the activity pauses (i.e. outside of the app)
     */
    @Override
    protected void onPause() {
        // Unregister the sensor when the activity pauses
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Detect any change to the sensor itself and react accordingly
     * @param sensor The sensor which is currently being detected
     * @param accuracy The accuracy of the sensor detection(?)
     */
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)  {
        // Do something here if the sensor changes
        // Put this somewhere - if accuracy == 0, then the dot stops moving and disappears
        if (sensor == light) {
            handleSensorData(accuracy);
        }
    }

    /**
     * Detects any change to the sensor's data and react accordingly
     * @param event The event that occurs when the sensor is changed
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Values
        float lux = event.values[0];

        // Do something with the sensor data, lux
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            handleSensorData(lux);
        }
    }

    /**
     * Hides the system and navigation bars of the phone to allow for fullscreen
     */
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                              | View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Gets a view's X coordinate relative to the screen display
     * @param v The view to grab the coordinate from
     * @return The X coordinate of the view
     */
    public int getLocationOfX(View v) {
        v.getLocationOnScreen(location);
        return location[0];
    }

    /**
     * Gets a view's Y coordinate relative to the screen display
     * @param v The view to grab the coordinate from
     * @return The Y coordinate of the view
     */
    public int getLocationOfY(View v) {
        v.getLocationOnScreen(location);
        return location[1];
    }

    /**
     * Handle the sensor data
     * @param lux The light value that the sensor is currently detecting
     */
    public void handleSensorData(float lux) {

        // Check if lux is low enough (i.e. covering it with your finger)
        //if (lux > 0 && lux < 10) {
        if (lux == 0.0) {
            laser.animate().cancel();

            laser.setVisibility(View.INVISIBLE);     // Hide the laser
            strikeLaser();

        } else {
            laser.setVisibility(View.VISIBLE);       // Show the laser
        }
    }

    /**
     * Randomly move the laser around the screen.
     * The laser must stay within the boundaries of the phone.
     */
    public void moveRandomly() {

        // Grab screen metrics
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels * 0.75;        // Use only 2/3 of th screen

        // Get the x and y coordinates of the laser
        int absX = getLocationOfX(laser);
        int absY = getLocationOfY(laser);

        // Variables for animation
        int coinFlip;
        int duration = 750;
        int randX;
        int randY;

        // Check the distance between the laser and the boundaries of the phone
        // We'll check which "quadrant" of the phone the laser is in and move it accordingly
        if (absY < (height /2) && absX < (width /2)) {            // Check TOP LEFT corner
            randX = rand.nextInt(absX);
            randY = rand.nextInt(absY) - 100;

            // Small adjustments
            randX += 50;
            randY += 150;

        } else if (absY < (height /2) && absX > (width /2)) {     // Check TOP RIGHT corner
            randX = -1 * (rand.nextInt(absX));
            randY = rand.nextInt(absY) - 100;

            // Small adjustments
            randY += 150;

        } else if (absY > (height /2) && absX < (width /2)) {      // Check BOTTOM LEFT corner
            randX = rand.nextInt(absX);
            randY = -1 * (rand.nextInt(absY));

            randY += 150;   //less harsh

        } else if (absY > (height /2) && absX > (width /2)) {      // Check BOTTOM RIGHT corner
            randX = -1 * (rand.nextInt(absX));
            randY = -1 * (rand.nextInt(absY));

            randY += 150;   //less harsh

        } else {
            // Initially move laser in any random direction
            coinFlip = rand.nextInt(4);
            if (coinFlip == 0) {
                randX = rand.nextInt(absX);
                randY = rand.nextInt(absY);
            } else if (coinFlip == 1) {
                randX = -1 * rand.nextInt(absX);
                randY = rand.nextInt(absY);
            } else if (coinFlip == 2) {
                randX = rand.nextInt(absX);
                randY = -1 * rand.nextInt(absY);
            } else {
                randX = -1 * rand.nextInt(absX);
                randY = -1 * rand.nextInt(absY);
            }
        }

        // Play animation for laser
        laser.animate().setStartDelay(0).setDuration(duration).translationXBy(randX).translationYBy(randY);

        // Repeat the animation indefinitely
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            laser.animate().withEndAction(new Runnable() {
                //@Override
                public void run() {
                    moveRandomly();
                }
            });
        }
    }

    /**
     * Shake a message on-screen to indicate the user inputted the wrong
     * combination for lock
     */
    public void shakeMessage() {

        // Grab the animation
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        // Find message and animate
        TextView unlockMsg = findViewById(R.id.unlockMessage);
        unlockMsg.startAnimation(shake);

        // Once done, laser moves randomly again
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation anim) { }
            @Override
            public void onAnimationEnd(Animation anim) { }
            @Override
            public void onAnimationRepeat(Animation anim) { }
        });
    }

    /**
     * Play animation of cat striking the position of the laser
     */
    public void strikeLaser() {

        // Values for animation
        final ImageView strikingPaw;

        final float origXLas;
        final float origYLas;

        final float origXPaw;
        final float origYPaw;

        final float resultX;
        final float resultY;

        final int duration = 200;
        final int delay = 200;

        // Get x and y of the laser
        origXLas = getLocationOfX(laser);
        origYLas = getLocationOfY(laser);

        if (origXLas <= (float) width/2) {
            strikingPaw = catPawL;
        } else {
            strikingPaw = catPawR;
        }

        // Get x and y of the paw
        origXPaw = getLocationOfX(strikingPaw);
        origYPaw = getLocationOfY(strikingPaw);

        // Find relative distance from the striking paw and the laser
        if (strikingPaw == catPawL) {
            // Position for left paw
            resultX = (origXPaw - origXLas) + 500;
            resultY = (origYPaw - origYLas) + 350;
        } else {
            // Position for right paw
            resultX = (origXPaw - origXLas) + 75;
            resultY = (origYPaw - origYLas) + 350;
        }

        // Animate the paw striking the laser
        strikingPaw.animate()
                .translationXBy(-resultX)
                .translationYBy(-resultY)
                .setDuration(duration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            strikingPaw.animate().withEndAction(new Runnable() {
                public void run() {

                    // Animate the paw retracting
                    strikingPaw.animate()
                            .translationXBy(resultX)
                            .translationYBy(resultY)
                            .setDuration(duration+100)
                            .setStartDelay(delay);

                    // Check if the paw hit the lock spot
                    checkForOverlap();

                    // Move laser again
                    moveRandomly();
                }
            });
        }
    }

    /**
     * Check whether or not the paw strike lands on one of the "paw-lock" locations.
     * If so, it will spawn a little paw print as an indication that you've successfully hit
     * a paw-lock location.
     */
    public void checkForOverlap() {

        // Values for checking overlap between laser and paw print
        int xLaser = getLocationOfX(laser);
        int yLaser = getLocationOfY(laser);

        int xC1 = getLocationOfX(print1);
        int yC1 = getLocationOfY(print1);

        int xC2 = getLocationOfX(print2);
        int yC2 = getLocationOfY(print2);

        int xC3 = getLocationOfX(print3);
        int yC3 = getLocationOfY(print3);

        // Generating hitboxes for the laser and paw prints
        Rect laserRect = new Rect(xLaser, yLaser, xLaser + laser.getMeasuredWidth(), yLaser + laser.getMeasuredHeight());

        Rect lockRect1 = new Rect(xC1, yC1, xC1 + print1.getMeasuredWidth(), yC1 + print1.getMeasuredHeight());
        Rect lockRect2 = new Rect(xC2, yC2, xC2 + print2.getMeasuredWidth(), yC2 + print2.getMeasuredHeight());
        Rect lockRect3 = new Rect(xC3, yC3, xC3 + print3.getMeasuredWidth(), yC3 + print3.getMeasuredHeight());

        // Check for overlap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if ((Rect.intersects(laserRect,lockRect1)) && !lock1) {
                print1.animate().alpha(1).setDuration(10);
                lock1 = true;
            } else if ((Rect.intersects(laserRect,lockRect2)) && !lock2) {
                print2.animate().alpha(1).setDuration(10);
                lock2 = true;
            } else if ((Rect.intersects(laserRect,lockRect3)) && !lock3) {
                print3.animate().alpha(1).setDuration(10);
                lock3 = true;
            } else {
                shakeMessage();
            }
        }
        // Check if all three lock spots have been hit
        checkCombination();
    }

    /**
     * Check if all three lock spots were hit.
     * If so, close the application to mimic a screen unlocking
     */
    public void checkCombination() {

        // If all three paws are hit
        if (lock1 && lock2 && lock3) {
            new CountDownTimer(250, 750) {
                public void onTick(long s) { }

                public void onFinish() {
                    finish();   // Close app
                }
            }.start();
        }
    }


}
