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
    TextView alertTypeText, alertNameText, alertDayText, alertTimeText;
    ToggleButton alertPositionButton;
    Intent intent, intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);

        intent = getIntent();
        intent1 = new Intent(AlertInfoActivity.this, ResultActivity.class);
        alertTypeText = (TextView) findViewById(R.id.alertType);
        alertNameText = (TextView) findViewById(R.id.alertTitle);
        alertDayText = (TextView) findViewById(R.id.dateTextView);
        alertTimeText = (TextView) findViewById(R.id.timeTextView);
        alertPositionButton = (ToggleButton) findViewById(R.id.alertToggle);

        setTextViewsAndButton();
    }

    private void setTextViewsAndButton(){
        String alertType = intent.getStringExtra(ActivityUtils.ALERT_TYPE_KEY);
        String alertName = intent.getStringExtra(ActivityUtils.ALERT_NAME_KEY);
        String alertDay = intent.getStringExtra(ActivityUtils.ALERT_DAY_KEY);
        String alertTime = intent.getStringExtra(ActivityUtils.ALERT_TIME_KEY);
        boolean alertPos = intent.getBooleanExtra(ActivityUtils.ALERT_ON_KEY, false);

        alertTypeText.setText(alertType);
        alertNameText.setText(alertName);
        alertDayText.setText(alertDay);
        alertTimeText.setText(alertTime);
        alertPositionButton.setChecked(alertPos);
    }

    public void editAlert(View v){

    }

    public void deleteAlert(View v){

        intent1.putExtra("ALERT_POSITION", intent.getExtras().getInt("POSITION"));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this alert?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setResult(RESULT_OK, intent1);
                        finish();
//                        startActivity(intent1);
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
