package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity {
    EditText usernameText, passwordText;
    Button loginButton, skipButton;
    Intent intent;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeWidgets();
        intent = getIntent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void initializeWidgets(){
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        skipButton = (Button) findViewById(R.id.skipLoginButton);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipClicker(v);
            }
        });
    }

    public void skipClicker(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ActivityUtils.USERNAME, "Guest");
        editor.putBoolean(ActivityUtils.SKIP_LOGIN, true);
        editor.commit();
        finish();
    }

    public void checkCredentials(View view){
        boolean loginSuccess = false;

        if(loginSuccess)
            setUserInfo();

    }

    public void setUserInfo(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.removeItem(R.id.action_login);
        menu.removeItem(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
