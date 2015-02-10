package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    SharedPreferences sharedPreferences;
    Intent intent;

    //logging in variables
    boolean loggedIn, skipLogin, timer;
    String name, date;

    // PassType variable
//    PassType passType;

    // Map Variables
//    MapKey mapKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loggedIn = false;
        skipLogin = sharedPreferences.getBoolean(ActivityUtils.SKIP_LOGIN, false);

        if(!loggedIn && !skipLogin)
            lauchLoginActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ActivityUtils.APPTAG, "[Main Activity] onResume");
        skipLogin = sharedPreferences.getBoolean(ActivityUtils.SKIP_LOGIN, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_login: {
                lauchLoginActivity();
                return true;
            }
            case R.id.action_settings: {
                break;
            }
            case R.id.action_exit: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case ActivityUtils.LOGIN_RETURN: {
                if(resultCode == Activity.RESULT_OK)
                    break;
                else
                    break;
            }
            default:
                break;
        }
    }

    private void lauchLoginActivity()
    {
        intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ActivityUtils.LOGIN_RETURN);
    }
}
