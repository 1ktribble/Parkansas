package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Intent intent, intent1;
    private AlertItemAdapter adapter;
    private ArrayList<AlertData> alertList;
    TimePicker timePicker;
    SharedPreferences sharedPreferences;
    AlertData aData;
    DateTime now, dateTime;
    LocalDateTime localDateTime;
    Context context;
//    AlarmService alarmService;
    String alertNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        if(alertList == null) {
            alertList = new ArrayList<AlertData>();
        }
        timePicker = new TimePicker(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        aData = new AlertData(false, 0, "", "", "", "", System.currentTimeMillis(), 0, 0);

        boolean addOnStart = false;
        if(intent != null)
            addOnStart = intent.getBooleanExtra("SetNotification", false);

        if(addOnStart){
            if(sharedPreferences.getBoolean("prefNotificationSwitch", false))
                addAlert();
            else
                promptNotificationSwitch();
        }


        LoadListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);

        if(!(sharedPreferences == null))

        intent = getIntent();

        context = this;
        dateTime = new DateTime();
        now = DateTime.now();
        localDateTime = now.withZone(DateTimeZone.getDefault()).toLocalDateTime();
        //alertConditionals(ActivityUtils.onCampus);
    }

    private void LoadListView(){
        ListView lv = (ListView) findViewById(R.id.alertListView);
        adapter = new AlertItemAdapter(this, R.layout.alarm_view, alertList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ResultActivity.this, AlertInfoActivity.class);
                intent.putExtra("POSITION", alertList.get(position).getAlertId());
                intent.putExtra(ActivityUtils.ALERT_TYPE_KEY, alertList.get(position).getAlertType());
                intent.putExtra(ActivityUtils.ALERT_NAME_KEY, alertList.get(position).getAlertName());
                intent.putExtra(ActivityUtils.ALERT_TIME_KEY, alertList.get(position).getAlertTime());
                intent.putExtra(ActivityUtils.ALERT_DAY_KEY, alertList.get(position).getAlertDay());
                intent.putExtra(ActivityUtils.ALERT_ON_KEY, alertList.get(position).getAlertPos());

                setResult(Activity.RESULT_OK, intent);
                startActivityForResult(intent, ActivityUtils.PREFERENCE_RETURN_INT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
            finish();
//            startActivityForResult(intent, ActivityUtils.PREFERENCE_RETURN_INT);
            return true;
        }
        if(id == R.id.action_add_alert){
            // Alert Dialog to add alert
            if(sharedPreferences.getBoolean("prefNotificationSwitch", false))
                addAlert();
            else
                promptNotificationSwitch();
            return true;
        }

       return false;
    }

    private void addAlert(){

        // open dialog asking what type of alert you want.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] selections = getResources().
                getStringArray(R.array.user_notification_type_values);

        builder.setTitle(getString(R.string.set_alert_type))
                .setItems(selections,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Set Alarm type to this
                                sharedPreferences.edit().putString(ActivityUtils.ALERT_TYPE_KEY,
                                    selections[which].toString()).apply();
                                dialog.dismiss();
                                if(sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY, "").
                                        equals("Wake Up Call"))
                                    openPhoneAlarmSystem();
                                else if(sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY, "").
                                        equals("Pre-GameDay Car Moving"))
                                    checkSchedule();
                                else if(sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY, "").
                                        equals("Free Parking"))
                                    alertFreeParking();
                                else
                                    openCustomizeAlert();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertFreeParking(){
        int day = now.getDayOfWeek();
        String alertMsg = "";
        boolean weekday = day < 7 && day > 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.free_parking);

        if (weekday) {
                if (ActivityUtils.openedFromPARKHERE) {
                    ActivityUtils.openedFromPARKHERE = false;
                    String parkingTime = intent.getStringExtra(ActivityUtils.PARKING_LOT_TIME);
                    String parkingName = intent.getStringExtra(ActivityUtils.PARKING_LOT_NAME);

                    if(parkingTime.equals("7 AM - 5 PM") || parkingTime.equals("7 AM - 8 PM")) {
                        String[] splitString = parkingTime.split("\\s+");
//                          Toast.makeText(this, "" + splitString[3], Toast.LENGTH_SHORT).show();
                        alertMsg = "Free parking will be available for Lot " + parkingName + " at " +
                                splitString[3] + " PM. Set an alert?";
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openCustomizeAlert();
                            }
                        });
                    }
//              Toast.makeText(this, "" + parkingTime, Toast.LENGTH_SHORT).show();
                }else{
                    alertMsg = "Please open from 'Park Here' button on home screen";
                }
        } else {
                alertMsg = "Parking is free on Weekends unless otherwise posted (i.e. RESIDENT " +
                        "RESERVED and Garland Garage).";
        }
        builder.setMessage(alertMsg);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkSchedule(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("We're Sorry")
                .setMessage("The 'Pre-GameDay Car Moving' alert has not been implemented in this" +
                        " version of PARKansas. Please check for later releases and use the 'Other' " +
                        "alert type instead.")
                .setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void promptNotificationSwitch(){

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.check_notifications, null);

        final ToggleButton toggleButton = (ToggleButton) promptView.findViewById(R.id.toggleButton);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(promptView);

        final Button button = (Button) promptView.findViewById(R.id.closeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("prefNotificationSwitch", toggleButton.isChecked())
                        .apply();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void openPhoneAlarmSystem(){
        // find a way to open the user's alarm
        final Context context = this;

        if(!sharedPreferences.getBoolean(ActivityUtils.SHOW_WAKE_UP_ALERT_WARNING, false))
        {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.wake_up_call_dialog, null);

            final CheckBox checkBox = (CheckBox) promptView.findViewById(R.id.checkBox);

            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setView(promptView);

            Button continueButton = (Button) promptView.findViewById(R.id.continueButton);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(openClockIntent);

                    sharedPreferences.edit().putBoolean(ActivityUtils.SHOW_WAKE_UP_ALERT_WARNING,
                            checkBox.isChecked()).apply();

                    alertDialog.dismiss();
                }
            });

            if(!sharedPreferences.getBoolean(ActivityUtils.SHOW_WAKE_UP_ALERT_WARNING, false))
                alertDialog.show();
            } else {
                Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openClockIntent);
        }

    }

    private void openCustomizeAlert(){

        sharedPreferences.edit().putString(ActivityUtils.ALERT_NAME_KEY, "").apply();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.layout_alert_dialog, null);

        final EditText editText = (EditText) promptView.findViewById(R.id.alertEditText);
        final Spinner spinner = (Spinner) promptView.findViewById(R.id.daySpinner);

        final TextView alertTypeTextView = (TextView) promptView.findViewById(R.id.alertType_text);
        final TextView alertTypeDescTextView = (TextView) promptView.findViewById(R.id.alertType_desc);

        final String alertTypeString = sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY,
                getString(R.string.time_expiration));

        alertTypeTextView.setText(alertTypeString);

        String alertMessage = getAlertMsg(alertTypeString);

        alertTypeDescTextView.setText(alertMessage);

        final AlertDialog alertDialogBuilder = new AlertDialog.Builder(this).create();
        alertDialogBuilder.setView(promptView);

        Button setTimeButton = (Button) promptView.findViewById(R.id.mSetTimeButton);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        Button saveAndContinueButton = (Button) promptView.findViewById(R.id.saveAndContinue);
        saveAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertNameString = editText.getText().toString();

                sharedPreferences.edit().putString(ActivityUtils.ALERT_NAME_KEY,
                        alertNameString).apply();

                sharedPreferences.edit().putString(ActivityUtils.ALERT_DAY_KEY,
                        spinner.getSelectedItem().toString()).apply();

                if(!isValidName(sharedPreferences.getString(ActivityUtils.ALERT_NAME_KEY,
                        "New Alert")))
                    editText.setError("Invalid Name");
                else {
                    saveNewAlert();
                    alertDialogBuilder.dismiss();
                }
            }
        });

//        alarmService = new AlarmService(this, alertTypeString, alertNameString);

        alertDialogBuilder.show();
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View promptView = layoutInflater.inflate(R.layout.layout_alert_dialog, null);
//
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.layout_alert_dialog);
//        dialog.setTitle(sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY, ""));
//
//        final EditText editText = (EditText) findViewById(R.id.alertEditText);
//        final Spinner spinner = (Spinner) findViewById(R.id.daySpinner);
//
//        Button setTimeButton = (Button) findViewById(R.id.mSetTimeButton);
//        setTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTimePickerDialog();
//            }
//        });
//
//        Button saveAndContinueButton = (Button) findViewById(R.id.saveAndContinue);
//        saveAndContinueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sharedPreferences.edit().putString(ActivityUtils.ALERT_NAME_KEY,
//                        editText.getText().toString()).apply();
//                sharedPreferences.edit().putString(ActivityUtils.ALERT_DAY_KEY,
//                        spinner.getSelectedItem().toString());
//
//                saveNewAlert();
//                dialog.dismiss();
//            }
//        });
    }

    private String getAlertMsg(String alertType){
        String alertMessage = "";

        switch(alertType){
            case "Pre-GameDay Car Moving":
                alertMessage = getString(R.string.pre_game_day_desc_no_game);
                break;
            case "Free Parking":
                alertMessage = getString(R.string.free_parking_desc);
                break;
            case "Set Time Expiration":
                alertMessage = getString(R.string.set_time_expir_desc);
                break;
            case "Harmon Notification":
                alertMessage = getString(R.string.harmon_time_set_desc);
                break;
            default:
                break;
        }

        return alertMessage;
    }

    private boolean isValidName(String alertName){
        if(alertName != null && alertName.length() > 0) {
            return true;
        }
        return false;
    }

    private void openTimePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY), minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, timePickerListener, hour, minute, false);

        timePickerDialog.setTitle(getString(R.string.set_time));
        timePickerDialog.show();
    }

    private void saveNewAlert(){
        //TODO: Add All this junk below

        int currentDay = now.getDayOfWeek();

        String day = sharedPreferences.getString(ActivityUtils.ALERT_DAY_KEY, "Alert Me Today");
        String name = sharedPreferences.getString(ActivityUtils.ALERT_NAME_KEY, "New Alert");
        int hour = sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0),
            minute = sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0);
        String alertType = sharedPreferences.getString(ActivityUtils.ALERT_TYPE_KEY, "Unknown Type");

        String amOrPm = sharedPreferences.getString(ActivityUtils.AM_PM_KEY, "AM");

        StringBuilder sb = new StringBuilder();

        if(day.equals("Alert Me Today")){
            if(hour == 0)
                hour = 12;
            else if(hour > 12)
                hour = hour - 12;

            sb.append((hour < 10) ? "0" + hour : hour)
                    .append(":")
                    .append((minute < 10) ? "0" + minute : minute)
                    .append(" ")
                    .append(amOrPm);

//            Calendar now = Calendar.getInstance();
//            int currentDay = now.get(Calendar.DAY_OF_WEEK);
            switch(currentDay){
                case 1:
                    day = "Sunday";
                    break;
                case 2:
                    day = "Monday";
                    break;
                case 3:
                    day = "Tuesday";
                    break;
                case 4:
                    day = "Wednesday";
                    break;
                case 5:
                    day = "Thursday";
                    break;
                case 6:
                    day = "Friday";
                    break;
                case 7:
                    day = "Saturday";
                    break;
                default:
                    day = "";
            }
            aData.setAlertDay(day);
            aData.setAlertTime(sb.toString());
            aData.setAlertPos(true);
            aData.setAlertTimeHour(hour);
            aData.setAlertType(alertType);
            aData.setAlertTimeMinute(minute);
            aData.setAlertName(name);
            aData.setDate(System.currentTimeMillis());

            Calendar calendar = Calendar.getInstance();

            calendar.set(localDateTime.getYear(), localDateTime.getMonthOfYear(), localDateTime.getDayOfWeek(),
                    hour, minute, 0);

            Intent myIntent = new Intent(ResultActivity.this, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ResultActivity.this, 0, myIntent,0);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
//            alarmService.startAlarm(day, hour, System.currentTimeMillis(), minute, amOrPm);

        }else{
            switch(day){
                case "Sunday":
                    dateTime = dateTime.withDayOfWeek(1);
                    if(now.getDayOfWeek() > DateTimeConstants.SUNDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Monday":
                    dateTime = dateTime.withDayOfWeek(2);
                    if(now.getDayOfWeek() > DateTimeConstants.MONDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Tuesday":
                    dateTime = dateTime.withDayOfWeek(3);
                    if(now.getDayOfWeek() > DateTimeConstants.TUESDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Wednesday":
                    dateTime = dateTime.withDayOfWeek(4);
                    if(now.getDayOfWeek() > DateTimeConstants.WEDNESDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Thursday":
                    dateTime = dateTime.withDayOfWeek(5);
                    if(now.getDayOfWeek() > DateTimeConstants.THURSDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Friday":
                    dateTime = dateTime.withDayOfWeek(6);
                    if(now.getDayOfWeek() > DateTimeConstants.FRIDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
                case "Saturday":
                    dateTime = dateTime.withDayOfWeek(7);
                    // This should never happen. But who knows, it's Java
                    if(now.getDayOfWeek() > DateTimeConstants.SATURDAY)
                        dateTime = dateTime.plusWeeks(1);
                    break;
            }
            aData.setAlertDay(day);

            sb.append((hour < 10) ? "0" + hour : hour)
                    .append(":")
                    .append((minute < 10) ? "0" + minute : minute)
                    .append(" ")
                    .append(amOrPm);

            aData.setAlertTime(sb.toString());
            aData.setAlertPos(true);
            aData.setAlertTimeHour(hour);
            aData.setAlertTimeMinute(minute);
            aData.setAlertName(name);
            aData.setAlertType(alertType);
            aData.setDate(System.currentTimeMillis());

            dateTime.withTime(hour, minute, 0, 0);

            Calendar calendar = Calendar.getInstance();

            calendar.set(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfWeek(),
                dateTime.getHourOfDay(), dateTime.getMinuteOfDay(), dateTime.getSecondOfDay());

            Intent myIntent = new Intent(ResultActivity.this, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ResultActivity.this, 0, myIntent,0);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
//            alarmService.startAlarm(day, hour, System.currentTimeMillis(), minute, amOrPm);

        }

//        alertList.add(aData);
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
//
        contentValues.put(ParkansasContentProvider.KEY_ALARM_NAME, aData.getAlertName());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TYPE, aData.getAlertType());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME, aData.getAlertTime());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_HOUR, aData.getAlertTimeHour());
        contentValues.put(ParkansasContentProvider.KEY_DATE, aData.getDate());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE, aData.getAlertTimeMinute());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_ON, aData.getAlertPos());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_DAY, aData.getAlertDay());

        contentResolver.insert(ParkansasContentProvider.CONTENT_URI, contentValues);
        getLoaderManager().restartLoader(0, null, this);
//
        sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_ON_KEY, true).apply();
//        sharedPreferences.edit().putBoolean("alertPos", true).apply();
        sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_TIME_SET, true).apply();


    }

//    @Override
//    protected Dialog onCreateDialog(int id){
//        TimePickerDialog timePickerDialog =
//                new TimePickerDialog(this, timePickerListener, hour, minute, false);
//
//        timePickerDialog.setTitle(getString(R.string.wake_up_call));
//        timePickerDialog.setMessage(getString(R.string.wake_up_call_alert));
//        switch(id){
//            case ActivityUtils.ALARM_ID:
//                return timePickerDialog;
//        }
//        return null;
//    }

    public int deleteRow(int list_position){
        ContentResolver cr = getContentResolver();
        int deleted = cr.delete(ParkansasContentProvider.CONTENT_URI,
                ParkansasContentProvider.KEY_ID + " = ?",
                new String[]{Integer.toString(list_position)});
        getLoaderManager().restartLoader(0, null, this);

        return deleted;
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);

        switch(reqCode){
            case(ActivityUtils.PREFERENCE_RETURN_INT):{
                    if(resCode == Activity.RESULT_OK){
                        int rowId = data.getExtras().getInt("ALERT_POSITION");
                        Toast
                            .makeText(this, deleteRow(rowId) + " Alert Removed", Toast.LENGTH_SHORT)
                            .show();
                    }
                break;
            }
//            case(ActivityUtils.ALERT_RETURN_INT):{
//                if(resCode == Activity.RESULT_OK){
//
//                    if(sharedPreferences.getString("alertDaySettings", "").equals("Alert Me Today")){
//                        Calendar now = Calendar.getInstance();
//                        int currentDay = now.get(Calendar.DAY_OF_WEEK);
//                        switch(currentDay){
//                            case 1:
//                                sharedPreferences.edit().putString("alertDaySettings", "Sunday").apply();
//                                break;
//                            case 2:
//                                sharedPreferences.edit().putString("alertDaySettings", "Monday").apply();
//                                break;
//                            case 3:
//                                sharedPreferences.edit().putString("alertDaySettings", "Tuesday").apply();
//                                break;
//                            case 4:
//                                sharedPreferences.edit().putString("alertDaySettings", "Wednesday").apply();
//                                break;
//                            case 5:
//                                sharedPreferences.edit().putString("alertDaySettings", "Thursday").apply();
//                                break;
//                            case 6:
//                                sharedPreferences.edit().putString("alertDaySettings", "Friday").apply();
//                                break;
//                            case 7:
//                                sharedPreferences.edit().putString("alertDaySettings", "Saturday").apply();
//                                break;
//                            default:
//                                sharedPreferences.edit().putString("alertDaySettings", "").apply();
//                        }
//                    }
//
//                    aData.setAlertDay(sharedPreferences.getString("alertDaySettings", ""));
//                    aData.setAlertTime(sharedPreferences.getString("timePickerAlertValue", ""));
//                    aData.setAlertPos(sharedPreferences.getBoolean("alertPos", false));
//                    aData.setAlertTimeHour(sharedPreferences.getInt("timePickerAlertValueHour", 0));
//                    aData.setAlertTimeMinute(sharedPreferences.getInt("timePickerAlertValueMinute", 0));
//                    aData.setAlertName(sharedPreferences.getString("alertNameSettings", ""));
//                    aData.setDate(System.currentTimeMillis());
//
//                    ContentResolver contentResolver = getContentResolver();
//                    ContentValues contentValues = new ContentValues();
//
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_NAME, aData.getAlertName());
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME, aData.getAlertTime());
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_HOUR, aData.getAlertTimeHour());
//                    contentValues.put(ParkansasContentProvider.KEY_DATE, aData.getDate());
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE, aData.getAlertTimeMinute());
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_ON, aData.getAlertPos());
//                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_DAY, aData.getAlertDay());
//
//                    contentResolver.insert(ParkansasContentProvider.CONTENT_URI, contentValues);
//                    getLoaderManager().restartLoader(0, null, this);
//
//                    sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_ON_KEY,
//                            aData.getAlertPos()).apply();
//                }
//            }
            default:
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        CursorLoader loader = new CursorLoader(this,
                ParkansasContentProvider.CONTENT_URI,
                null, null, null, ParkansasContentProvider.KEY_DATE + " DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO Auto-generated method stub
        int keyAlertId = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ID);
        int keyAlertName = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_NAME);
        int keyAlertTime = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME);
        int keyAlertDay = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_DAY);
        int keyAlertType = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TYPE);
        int keyAlertOn = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_ON);
        int keyAlertHour = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_HOUR);
        int keyAlertMinute = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE);
        int keyAlertCreationDate = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_DATE);

//
//        public AlertData(boolean alertOn, int id, String time, String day, String type, String alertName,
//        long c, int hour, int minute)
        alertList.clear();
        while(data.moveToNext()){
            AlertData tempAlert = new AlertData(data.getInt(keyAlertOn) == 0 ? false : true,
                    data.getInt(keyAlertId), data.getString(keyAlertTime),
                    data.getString(keyAlertDay), data.getString(keyAlertType),
                    data.getString(keyAlertName), data.getLong(keyAlertCreationDate),
                    data.getInt(keyAlertHour), data.getInt(keyAlertMinute));
            alertList.add(tempAlert);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        //TODO: fix boolean
        if(!ActivityUtils.mainActivityActive) {
            this.startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        }

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int nMinute) {
            int hour = 0, minute = 0;
            String am_pm = "";
            hour = hourOfDay;
            minute = nMinute;

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hour);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";


            sharedPreferences.edit().putInt(ActivityUtils.HOUR_KEY, hour).apply();
            sharedPreferences.edit().putInt(ActivityUtils.MINUTE_KEY, minute).apply();

            timePicker.setCurrentHour(sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0));
            timePicker.setCurrentMinute(sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0));

//            sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_TIME_SET, true).apply();
            Log.i("Time Set", sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0) + ":" +
                    sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0));
//            Toast.makeText(ResultActivity.this, "Alert set for " +
//                            (sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0) > 12?
//                            sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0) - 12 :
//                            sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0)+ ":" +
//                    ((minute < 10)? "0"+sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0):
//                            sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0))),
//                            Toast.LENGTH_LONG).show();

            if(hour == 0) hour = 12;

            sharedPreferences.edit().putInt(ActivityUtils.HOUR_KEY,
                    hour > 12 ? hour - 12 : hour).apply();
            sharedPreferences.edit().putInt(ActivityUtils.MINUTE_KEY,
                    minute).apply();
            sharedPreferences.edit().putString(ActivityUtils.AM_PM_KEY, am_pm).apply();

        }
    };

//    public void addAlarm(int hour, int minute, boolean alertOn, String day){
//        StringBuilder sb = new StringBuilder();
//
//        if(hour == 0)
//            hour = 12;
//        else if(hour > 12)
//            hour = hour - 12;
//
//        sb.append((hour < 10) ? "0" + hour : hour)
//                .append(":")
//                .append((minute < 10) ? "0" + minute : minute);
//
//        if(day.equals("")){
//            Calendar now = Calendar.getInstance();
//            int currentDay = now.get(Calendar.DAY_OF_WEEK);
//            switch(currentDay){
//                case 1:
//                    day = "Sunday";
//                    break;
//                case 2:
//                    day = "Monday";
//                    break;
//                case 3:
//                    day = "Tuesday";
//                    break;
//                case 4:
//                    day = "Wednesday";
//                    break;
//                case 5:
//                    day = "Thursday";
//                    break;
//                case 6:
//                    day = "Friday";
//                    break;
//                case 7:
//                    day = "Saturday";
//                    break;
//                default:
//                    day = "";
//            }
//        }
//
//        aData.setAlertDay(day);
//        aData.setAlertTime(sb.toString());
//        aData.setAlertPos(alertOn);
//        aData.setAlertTimeHour(hour);
//        aData.setAlertTimeMinute(minute);
//        aData.setAlertName("New Alert");
//        aData.setDate(System.currentTimeMillis());
//
//        ContentResolver contentResolver = getContentResolver();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_NAME, aData.getAlertName());
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME, aData.getAlertTime());
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_HOUR, aData.getAlertTimeHour());
//        contentValues.put(ParkansasContentProvider.KEY_DATE, aData.getDate());
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE, aData.getAlertTimeMinute());
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_ON, aData.getAlertPos());
//        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_DAY, aData.getAlertDay());
//
//        contentResolver.insert(ParkansasContentProvider.CONTENT_URI, contentValues);
//        getLoaderManager().restartLoader(0, null, this);
//
//        sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_ON_KEY, alertOn).apply();
////        sharedPreferences.edit().putBoolean("alertPos", true).apply();
//        sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_TIME_SET, true).apply();
//    }
}
