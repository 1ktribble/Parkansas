package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;


public class AlertInfoActivity extends Activity {
    TextView alertNameText, alertDayText, alertTimeText;
    ToggleButton alertPositionButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);

        intent = getIntent();
        alertNameText = (TextView) findViewById(R.id.alertTitle);
        alertDayText = (TextView) findViewById(R.id.dateTextView);
        alertTimeText = (TextView) findViewById(R.id.timeTextView);
        alertPositionButton = (ToggleButton) findViewById(R.id.alertToggle);

        setTextViewsAndButton();
    }

    private void setTextViewsAndButton(){
        String alertName = intent.getStringExtra(ActivityUtils.ALERT_NAME_KEY);
        String alertDay = intent.getStringExtra(ActivityUtils.ALERT_DAY_KEY);
        String alertTime = intent.getStringExtra(ActivityUtils.ALERT_TIME_KEY);
        boolean alertPos = intent.getBooleanExtra(ActivityUtils.ALERT_ON_KEY, false);

        alertNameText.setText(alertName);
        alertDayText.setText(alertDay);
        alertTimeText.setText(alertTime);
        alertPositionButton.setChecked(alertPos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alert_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }
        if(id == R.id.action_alerts){
            finish();
            return true;
        }

        return false;
    }

    public void editAlert(View v){

    }

    public void deleteAlert(View v){
        intent.putExtra("ALERT_POSITION", intent.getExtras().getInt("POSITION"));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this alert?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK, intent);
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
