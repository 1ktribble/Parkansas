package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Kai Tribble on 4/7/2015.
 */
public class DismissAlert extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(ActivityUtils.NOTIFICATION_ID);
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }
    public static PendingIntent getDismissIntent(int notificationId, Context context){
        Intent intent = new Intent(context, ResultActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ActivityUtils.NOTIFICATION_ID_STRING, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return dismissIntent;
    }
}
