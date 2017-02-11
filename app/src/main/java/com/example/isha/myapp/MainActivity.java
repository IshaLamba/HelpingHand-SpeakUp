package com.example.isha.myapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements   GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    TextToSpeech tts;

    Prefs prefs;
   // Button b1;
    private static final String DEBUG_TAG = "Gestures";
    //creates a Gesture Detector
    private GestureDetector gd;

    public static boolean isServiceRunning = false;
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts=new TextToSpeech(MainActivity.this,new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status!= TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    speak("Double tap to start the service.");
                }
            }
        });
                gd = new GestureDetector(this, this);
                gd.setOnDoubleTapListener(this);
            }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gd.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        startService(new Intent(getApplicationContext(),ShakeService.class));
        Toast toast = Toast.makeText(getApplicationContext(), "Service started..!!! please press back button to go back to home screen.", Toast.LENGTH_SHORT);
        toast.show();
        speak("Service started. please press back button to go back to home screen");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String x){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("InlinedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stop) {
            if (isServiceRunning == true) {
                stopService(new Intent(this, ShakeService.class));
                finish();
            } else {
                finish();
            }
            return true;
        }
        if (id == R.id.action_notification) {
            Intent intent = new Intent();
            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) { // above 2.3
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(SCHEME, getPackageName(), null);
                intent.setData(uri);
            } else { // below 2.3
                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                        : APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                        APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, getPackageName());
            }
            startActivity(intent);
            Toast.makeText(this,
                    "Check 2 times Show notifications and keep this checked",
                    Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_rate) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + getPackageName().toString()));
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}
