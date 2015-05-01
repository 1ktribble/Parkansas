package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.Random;


public class SplashActivity extends Activity {

    TextView factText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(8000);  //Delay of 10 seconds

                } catch (Exception e) {

                } finally {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        factText = (TextView) findViewById(R.id.fact_textView);
        factText.setText(chooseFact());
    }

    private String chooseFact(){
        String[] mArray = getResources().getStringArray(R.array.rand_facts_values);
        Random rand = new Random();

        int arrayLoc = rand.nextInt(mArray.length);
        if(arrayLoc != 0)
            arrayLoc--;

        return mArray[arrayLoc];
    }
}



