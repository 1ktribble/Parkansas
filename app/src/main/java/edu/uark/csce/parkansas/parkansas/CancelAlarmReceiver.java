package edu.uark.csce.parkansas.parkansas;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class CancelAlarmReceiver extends BroadcastReceiver {
    public CancelAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent service = new Intent();
        service.setComponent(new ComponentName(context, ParkansasNotificationService.class));
        context.stopService(service);
    }
}
