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
    private boolean onCampusCheck, between9and6;
    private Intent notificationIntent, setTimeIntent, dismissIntent;
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
        setTimeIntent = notificationIntent;
        dismissIntent = new Intent(this, CancelAlarmReceiver.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ParkansasService", "Received start id " + startId + ": " + intent);

        Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();

        if(intent != null) {
            onCampusCheck = ActivityUtils.onCampus;
            between9and6 = checkTime();
//            Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();

            if (onCampusCheck) {
                if (/*between9and6 && */ActivityUtils.wakeUpCallOn) {
                    if(sharedPreferences.getBoolean("alertHasBeenSet", false)) {
//                        notificationManager.cancel(ActivityUtils.NOTIFICATION_ID);
                    }else{
//                        showWakeUpNotification();
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

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

//    @Override
//    protected void onStartCommand(Intent intent) {
//        if(intent != null) {
//            onCampusCheck = ActivityUtils.onCampus;
//            between9and6 = checkTime();
//            Toast.makeText(this, "Notifications On", Toast.LENGTH_SHORT).show();
//
//            if (onCampusCheck) {
//                if (between9and6 && ActivityUtils.wakeUpCallOn) {
//                    showWakeUpNotification();
//                }
//            } else {
//                if (ActivityUtils.timeExpirationNotificationOn) {
//
//                }
//                if (ActivityUtils.gameDayNotificationOn) {
//
//                }
//                if (ActivityUtils.freeParkingNotificationOn) {
//
//                }
//                if (ActivityUtils.harmonNotificationOn && !ActivityUtils.hasHarmonPass) {
//
//                }
//            }
//        }
//    }

    @Override
    public void onDestroy(){
        notificationManager.cancel(ActivityUtils.NOTIFICATION_ID);

        Toast.makeText(this, "Notifications Off", Toast.LENGTH_SHORT).show();
    }

    private void showWakeUpNotification(){

        PendingIntent pendingIntentSetTime = PendingIntent.getActivity(this,
                ActivityUtils.ALARM_ID, setTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        String nine_oClock = "21:00:00";
        String six_oClock = "06:00:00";
        String today = (String) android.text.format.DateFormat.format("HH:mm:ss", new java.util.Date());
        Date todayDate = null, nineDate = null, sixDate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            todayDate = simpleDateFormat.parse(today);
            nineDate = simpleDateFormat.parse(nine_oClock);
            sixDate = simpleDateFormat.parse(six_oClock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Time. Today: ", todayDate + " 9:00 - " + nineDate + "Comparison: " +
                Boolean.toString((todayDate).after(nineDate)));

        if (todayDate.after(nineDate)) {
            return true;
        }
        if(todayDate.before(sixDate)){
            return true;
        }
        else
            return false;
    }

}
