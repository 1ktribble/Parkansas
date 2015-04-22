package edu.uark.csce.parkansas.parkansas;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ParkansasNotificationService extends Service {

    private NotificationCompat.Builder builder;
    private SharedPreferences sharedPreferences;
    private boolean onCampusCheck, betweenSevenAndFive, betweenSevenAndEight;
    private Intent notificationIntent;
    private PendingIntent setTimeIntent, dismissIntent;
    private ListPreference classificationList;
    private NotificationManager notificationManager;

    public ParkansasNotificationService() {}

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ParkansasNotificationService getService() {
            return ParkansasNotificationService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        classificationList = new ListPreference(this);

        notificationIntent = new Intent(this, ResultActivity.class);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ParkansasService", "Received start id " + startId + ": " + intent);

        Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();

        if(intent != null) {
            onCampusCheck = ActivityUtils.onCampus;
//            Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();

            if (onCampusCheck) {
                if (sharedPreferences.getBoolean(ActivityUtils.WAKEUP_ALERT, false)) {
                    if (sharedPreferences.getBoolean(ActivityUtils.ALERT_TIME_SET, false /*&&
                    between9and6 && user indicated he/she is parked on a campus spot*/)) {
//                        notificationManager.cancel(ActivityUtils.NOTIFICATION_ID);
                    } else {
//                        showWakeUpNotification();
                    }
                }
            }                // TODO: Handle this Notification
                if (sharedPreferences.getBoolean(ActivityUtils.TIME_EXPIRATION_ALERT, false)) {
                    // if the user indicated
                    // 1) he/she parked in Garland Garage, Meadow Avenue, or any Short-Term or
                    // Long-Term lots and
                    // 2) he/she has indicated they want to see the notification.
                    boolean testBool = false;
                    boolean testBool_1 = false;
                    if(!testBool == !testBool_1){
                        showTimeExpirationNotification();
                    }

                }
                // TODO: Handle this Notification
                if (sharedPreferences.getBoolean(ActivityUtils.GAMEDAY_ALERT, false)) {
                    // TODO: Start New Service
                    // 1) Check the schedule
                }

                // Dakota handles these
                if (sharedPreferences.getBoolean(ActivityUtils.FREE_PARKING_ALERT, false)) {

                }
                if (sharedPreferences.getBoolean(ActivityUtils.HARMON_ALERT, false)
                        && !(sharedPreferences.getBoolean(ActivityUtils.HARMON_PASS, false))) {

                }
            }


        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        notificationManager.cancel(ActivityUtils.NOTIFICATION_ID);

        Toast.makeText(this, "Notifications Off", Toast.LENGTH_SHORT).show();
    }

    private void showWakeUpNotification(){

        PendingIntent pendingIntentSetTime = PendingIntent.getActivity(this,
                ActivityUtils.ALARM_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new Notification.BigTextStyle().bigText(getString(R.string.wake_up_call_msg)))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntentSetTime)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_suspend_alert,"Set Time", pendingIntentSetTime)
                .addAction(R.drawable.ic_dismiss_alert, getString(R.string.dismiss_text), pendingIntentSetTime)
                .build();

        notification.defaults |= Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;

        notificationManager.notify(ActivityUtils.NOTIFICATION_ID, notification);
    }

    private boolean checkTime() {
        String five_oClock = "17:00:00";
        String eight_oClock = "08:00:00";
        String seven_oClock = "07:00:00";
        String today = (String) android.text.format.DateFormat.format("HH:mm:ss", new java.util.Date());
        Date todayDate = null, endTimeDateFive = null, endTimeDateEight = null, startTimeDateSeven = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            todayDate = simpleDateFormat.parse(today);
            startTimeDateSeven = simpleDateFormat.parse(seven_oClock);
            endTimeDateFive = simpleDateFormat.parse(five_oClock);
            endTimeDateEight = simpleDateFormat.parse(eight_oClock);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.i("Time. Today: ", todayDate + " 9:00 - " + nineDate + "Comparison: " +
//                Boolean.toString((todayDate).after(nineDate)));

        if (todayDate.after(endTimeDateFive)) {
            return true;
        }
        if(todayDate.before(startTimeDateSeven)){
            return true;
        }
        else
            return false;
    }

    private void showTimeExpirationNotification(){
        setTimeIntent = PendingIntent.getActivity(this,
                ActivityUtils.ALARM_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dismissIntent = DismissAlert.getDismissIntent(ActivityUtils.NOTIFICATION_ID, this);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.time_expiration))
                .setStyle(new Notification.BigTextStyle().bigText(getString(R.string.time_expiration_pre_msg)))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(setTimeIntent)
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;

        notificationManager.notify(ActivityUtils.NOTIFICATION_ID, notification);
    }
}
