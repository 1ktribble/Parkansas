package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class ResultActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Intent intent;
    private AlertItemAdapter adapter;
    private ArrayList<AlertData> alertList;
    private int hour, minute;
    TimePicker timePicker;
    SharedPreferences sharedPreferences;
    AlertData aData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if(alertList == null) {
            alertList = new ArrayList<AlertData>();
        }
        timePicker = new TimePicker(this);
        ActivityUtils.alarmTimeSet = false;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        aData = new AlertData(false, "", "", "", "", System.currentTimeMillis(), 0, 0);

        LoadListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);

        alertConditionals(ActivityUtils.onCampus);
    }

    private void LoadListView(){
        ListView lv = (ListView) findViewById(R.id.alertListView);
        adapter = new AlertItemAdapter(this, R.layout.alarm_view, alertList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ResultActivity.this, AlertInfoActivity.class);
                intent.putExtra("POSITION", position);
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

       return false;
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);

        switch(reqCode){
            case(ActivityUtils.PREFERENCE_RETURN_INT):{
                    if(resCode == Activity.RESULT_OK){
                        ContentResolver cr = getContentResolver();
                        int list_position = intent.getExtras().getInt("ALERT_POSITION");
                        String tempName = alertList.get(list_position).getAlertName();
                        String tempTime = alertList.get(list_position).getAlertTime();
                        String tempDay = alertList.get(list_position).getAlertDay();

                        int deleted = cr.delete(ParkansasContentProvider.CONTENT_URI,
                                      ParkansasContentProvider.KEY_ALARM_NAME + " = ? AND " +
                                      ParkansasContentProvider.KEY_ALARM_TIME + " = ? AND " +
                                      ParkansasContentProvider.KEY_ALARM_TIME_DAY + " = ?",
                                new String[]{Integer.toString(list_position), tempName, tempTime,
                                        tempDay});
                        Toast.makeText(this, deleted + " Alert Removed", Toast.LENGTH_SHORT).show();
                    }
                break;
            }
            case(ActivityUtils.ALERT_RETURN_INT):{
                if(resCode == Activity.RESULT_OK){
//                    StringBuilder sb = new StringBuilder();
//
//                    sb.append((hour < 10) ? "0" + hour : hour)
//                            .append(":")
//                            .append((minute < 10) ? "0" + minute : minute);
                    if(sharedPreferences.getString("alertDaySettings", "").equals("Alert Me Today")){
                        Calendar now = Calendar.getInstance();
                        int currentDay = now.get(Calendar.DAY_OF_WEEK);
                        switch(currentDay){
                            case 1:
                                sharedPreferences.edit().putString("alertDaySettings", "Sunday").apply();
                                break;
                            case 2:
                                sharedPreferences.edit().putString("alertDaySettings", "Monday").apply();
                                break;
                            case 3:
                                sharedPreferences.edit().putString("alertDaySettings", "Tuesday").apply();
                                break;
                            case 4:
                                sharedPreferences.edit().putString("alertDaySettings", "Wednesday").apply();
                                break;
                            case 5:
                                sharedPreferences.edit().putString("alertDaySettings", "Thursday").apply();
                                break;
                            case 6:
                                sharedPreferences.edit().putString("alertDaySettings", "Friday").apply();
                                break;
                            case 7:
                                sharedPreferences.edit().putString("alertDaySettings", "Saturday").apply();
                                break;
                            default:
                                sharedPreferences.edit().putString("alertDaySettings", "").apply();
                        }
                    }

                    aData.setAlertDay(sharedPreferences.getString("alertDaySettings", ""));
                    aData.setAlertTime(sharedPreferences.getString("timePickerAlertValue", ""));
                    aData.setAlertPos(sharedPreferences.getBoolean("alertPos", false));
                    aData.setAlertTimeHour(sharedPreferences.getInt("timePickerAlertValueHour", 0));
                    aData.setAlertTimeMinute(sharedPreferences.getInt("timePickerAlertValueMinute", 0));
                    aData.setAlertName(sharedPreferences.getString("alertNameSettings", ""));
                    aData.setDate(System.currentTimeMillis());

                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(ParkansasContentProvider.KEY_ALARM_NAME, aData.getAlertName());
                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME, aData.getAlertTime());
                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_HOUR, aData.getAlertTimeHour());
                    contentValues.put(ParkansasContentProvider.KEY_DATE, aData.getDate());
                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE, aData.getAlertTimeMinute());
                    contentValues.put(ParkansasContentProvider.KEY_ALARM_ON, aData.getAlertPos());
                    contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_DAY, aData.getAlertDay());

                    contentResolver.insert(ParkansasContentProvider.CONTENT_URI, contentValues);
                    getLoaderManager().restartLoader(0, null, this);

                    sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_ON_KEY,
                            aData.getAlertPos()).apply();
                }
            }
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
        int keyAlertName = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_NAME);
        int keyAlertTime = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME);
        int keyAlertDay = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_DAY);
        int keyAlertType = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TYPE);
        int keyAlertOn = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_ON);
        int keyAlertHour = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_HOUR);
        int keyAlertMinute = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE);
        int keyAlertCreationDate = data.getColumnIndexOrThrow(ParkansasContentProvider.KEY_DATE);


        //TODO: create keys for longitude and latitudes.
        alertList.clear();
        while(data.moveToNext()){
            AlertData tempWorkout = new AlertData(data.getInt(keyAlertOn) == 0 ? false : true,
                    data.getString(keyAlertTime), data.getString(keyAlertDay),
                    data.getString(keyAlertName), data.getString(keyAlertType),
                    data.getLong(keyAlertCreationDate), data.getInt(keyAlertHour),
                    data.getInt(keyAlertMinute));
            alertList.add(tempWorkout);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO Auto-generated method stub

    }

    private void alertConditionals(boolean onCampusCheck){

            if (onCampusCheck) {
                if (ActivityUtils.wakeUpCallOn) {
//                    showWakeUpNotification();

                    if(!(sharedPreferences.getBoolean("alertHasBeenSet", false))) {
   //                         showDialog(ActivityUtils.ALARM_ID);
  //                          Log.i("Time Set", hour + ":" + minute);
                        }
                }
            } else {
                if (ActivityUtils.timeExpirationNotificationOn) {

                }
                if (ActivityUtils.gameDayNotificationOn) {

                }
                if (ActivityUtils.freeParkingNotificationOn) {

                }
                if (ActivityUtils.harmonNotificationOn && !ActivityUtils.hasHarmonPass) {

                }
            }
    }

    @Override
    protected Dialog onCreateDialog(int id){
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, timePickerListener, hour, minute, false);

        timePickerDialog.setTitle(getString(R.string.wake_up_call));
        timePickerDialog.setMessage(getString(R.string.wake_up_call_alert));
        switch(id){
            case ActivityUtils.ALARM_ID:
                return timePickerDialog;
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int nMinute) {
            hour = hourOfDay;
            minute = nMinute;

            sharedPreferences.edit().putInt(ActivityUtils.HOUR_KEY, hour).apply();
            sharedPreferences.edit().putInt(ActivityUtils.MINUTE_KEY, minute).apply();

            timePicker.setCurrentHour(sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0));
            timePicker.setCurrentMinute(sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0));

            ActivityUtils.alarmTimeSet = true;
            Log.i("Time Set", sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0) + ":" +
                    sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0));
            Toast.makeText(ResultActivity.this, "Wake Up Call set for " +
                    sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0)+ ":" +
                    ((minute < 10)? "0"+sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0):
                            sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0)),
                            Toast.LENGTH_LONG).show();

            addAlarm(hour, minute, true, "");
        }
    };

    public void addAlarm(int hour, int minute, boolean alertOn, String day){
        StringBuilder sb = new StringBuilder();

        sb.append((hour < 10) ? "0" + hour : hour)
                .append(":")
                .append((minute < 10) ? "0" + minute : minute);

        if(day.equals("")){
            Calendar now = Calendar.getInstance();
            int currentDay = now.get(Calendar.DAY_OF_WEEK);
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
        }

        aData.setAlertDay(day);
        aData.setAlertTime(sb.toString());
        aData.setAlertPos(alertOn);
        aData.setAlertTimeHour(hour);
        aData.setAlertTimeMinute(minute);
        aData.setAlertName("New Alert");
        aData.setDate(System.currentTimeMillis());

        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ParkansasContentProvider.KEY_ALARM_NAME, aData.getAlertName());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME, aData.getAlertTime());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_HOUR, aData.getAlertTimeHour());
        contentValues.put(ParkansasContentProvider.KEY_DATE, aData.getDate());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_MINUTE, aData.getAlertTimeMinute());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_ON, aData.getAlertPos());
        contentValues.put(ParkansasContentProvider.KEY_ALARM_TIME_DAY, aData.getAlertDay());

        contentResolver.insert(ParkansasContentProvider.CONTENT_URI, contentValues);
        getLoaderManager().restartLoader(0, null, this);

        sharedPreferences.edit().putBoolean(ActivityUtils.ALERT_ON_KEY, alertOn).apply();
        sharedPreferences.edit().putBoolean("alertPos", true).apply();
        sharedPreferences.edit().putBoolean("alertHasBeenSet", true).apply();
    }
}
