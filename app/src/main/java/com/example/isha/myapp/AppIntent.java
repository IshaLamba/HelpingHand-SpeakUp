package com.example.isha.myapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;



public class AppIntent extends AppCompatActivity  implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    boolean found=false;
    TextToSpeech tts;
    TextView disp;
    private ImageView mImageView;
    String phoneNo;
    String message;
    private static final int RQS_RECOGNITION = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    Spinner spinnerResult;
    String res;
    public String contact;
    private static final String DEBUG_TAG = "Gestures";

    private GestureDetector gd;
    static int counter = 0;
    TextView tv;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_intent);

        disp=(TextView) findViewById(R.id.tvIntent);

        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        mVibrator.vibrate(600);
        tts=new TextToSpeech(AppIntent.this,new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status!= TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    speak("voice command mode activated");
                }
            }
        });

        gd = new GestureDetector(this, this);
        gd.setOnDoubleTapListener(this);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == RQS_RECOGNITION) & (resultCode == RESULT_OK)) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerResult.setAdapter(adapter);
            String selectedResult = result.toString();
            Toast.makeText(AppIntent.this, selectedResult, Toast.LENGTH_SHORT).show();
            recognition(result);
        }
    }

    private void recognition(ArrayList<String> result){
        Log.e("Speech",""+result);
        String res=result.get(0);
        String[] R1=res.split(" ");
        if(R1[0].equals("call")){
            int n=R1.length-1;
            String[] Res2=new String[n];
            System.arraycopy(R1,1,Res2,0,n);
            String num = Arrays.asList(Res2).toString();
            //replace starting "[" and ending "]" and ","
            contact= num.substring(1, num.length()-1).replaceAll(",","");
            Toast.makeText(AppIntent.this, contact, Toast.LENGTH_SHORT).show();
            getNumber(this.getContentResolver(),contact);
            call();
            //speak("message");
            //phoneNo

        }
        else if(R1[0].equals("message")){
            String s=R1[1];
            //contact=R1[1];
            getNumber(this.getContentResolver(),s);
            Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
            int n=R1.length-2;
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);
            String str = Arrays.asList(Res2).toString();
            message=str.substring(1, str.length()-1).replaceAll(",", "");

            Toast.makeText(AppIntent.this, message, Toast.LENGTH_SHORT).show();
            //String num = Arrays.asList(Res2).toString();
            //replace starting "[" and ending "]" and ","
            // phoneNo= num.substring(1, num.length()-1).replaceAll(",", "");
            //String num= Arrays.toString(Res2);
            //Toast.makeText(AppIntent.this, num, Toast.LENGTH_SHORT).show();
            sendSMSMessage();
        }


        else if(R1[0].equals("new")&&R1[1].equals("call")){
            int n=R1.length-2;
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);
            String num = Arrays.asList(Res2).toString();
            //replace starting "[" and ending "]" and ","
            phoneNo= num.substring(1, num.length()-1).replaceAll(",", "");
            //String num= Arrays.toString(Res2);
            Toast.makeText(AppIntent.this, num, Toast.LENGTH_SHORT).show();

            call();
        }
        else if(R1[0].equals("new")&&R1[1].equals("message")){
            speak("message");
            int n=R1.length-2;
            String r="";
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);

            //replace starting "[" and ending "]" and ","
            int i=0,j=0;
            while(i<=10)
            {
                int l=Res2[j].length();
                i=i+l;
                r=r.concat(Res2[j]);
                j++;
            }
            phoneNo=r.substring(0,10);
            int n1=Res2.length-(j-1);
            Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
            String[] msg=new String[n1];
            System.arraycopy(Res2,j-1,msg,0,n1);
            String str = Arrays.asList(msg).toString();
            message=str.substring(1, str.length()-1).replaceAll(",", "");
            //String num= Arrays.toString(Res2);
            Toast.makeText(AppIntent.this, str, Toast.LENGTH_SHORT).show();
            sendSMSMessage();
        }

        else
            speak("unknown");

    }

    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    protected void sendSMSMessage()
    {
        //phoneNo = "9582432043";
        // message = "Hi Himanshi its wrking ";
        //Intent intent=new Intent(getApplicationContext(),AppIntent.class);
        //PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }
    protected void call(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNo));
        if (ActivityCompat.checkSelfPermission(AppIntent.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        startActivity(callIntent);
    }
    protected void call2(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+"9212450164"));
        if (ActivityCompat.checkSelfPermission(AppIntent.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        startActivity(callIntent);
    }
    private void speak(String x){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void getNumber(ContentResolver cr,String n)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.toLowerCase().equals(n.toLowerCase())) {
                phoneNo=ph;
                found=true;
                Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
                break;
            }

        }
        if(!found)
            speak("contact not found");
        phones.close();
        Toast.makeText(AppIntent.this, "done", Toast.LENGTH_SHORT).show();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if(counter<=2){
                counter++;}
            else{
                Toast.makeText(this, "Volume Down Pressed 3 times", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AppIntent.this, MainActivity.class);
                startActivity(i);
                counter=0;
            }
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
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
        call2();
        Toast toast = Toast.makeText(getApplicationContext(), "Service started..!!! please press back button to go back to home screen.", Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        startService(new Intent(getApplicationContext(),AppIntent.class));
        Toast toast = Toast.makeText(getApplicationContext(), "Ambulance called!!!", Toast.LENGTH_SHORT);
        toast.show();
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

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
