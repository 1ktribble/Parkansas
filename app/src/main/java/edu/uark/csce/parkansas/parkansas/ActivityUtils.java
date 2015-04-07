package edu.uark.csce.parkansas.parkansas;

import android.app.Service;
import android.text.format.Time;

/**
 * Created by Kai Tribble on 12/5/2014.
 */
public class ActivityUtils {
//    public static final String ON_CAMPUS_BOOL = "edu.uark.csce.parkansas.parkansas.ON_CAMPUS_BOOL";
//    public static final String EXTRA_MESSAGE = "edu.uark.csce.parkansas.parkansas.EXTRA_MESSAGE";

    public static final String NOTIFICATION_ID_STRING = "NOTIFICATION_ID";

    public static final String ALERT_NAME_KEY = "edu.uark.csce.parkansas.parkansas.ALERT_NAME_KEY";
    public static final String ALERT_TIME_KEY = "edu.uark.csce.parkansas.parkansas.ALERT_TIME_KEY";
    public static final String ALERT_DAY_KEY = "edu.uark.csce.parkansas.parkansas.ALERT_DAY_KEY";
    public static final String ALERT_ON_KEY = "edu.uark.csce.parkansas.parkansas.ALERT_ON_KEY";
    public static final String HOUR_KEY = "edu.uark.csce.parkansas.parkansas.HOUR_KEY";
    public static final String MINUTE_KEY = "edu.uark.csce.parkansas.parkansas.MINUTE_KEY";
    public static final String ALERT_TIME_SET = "edu.uark.csce.parkansas.parkansas.ALERT_TIME_SET";

    public static final String WAKEUP_ALERT = "edu.uark.csce.parkansas.parkansas.WAKEUP_ALERT";
    public static final String GAMEDAY_ALERT = "edu.uark.csce.parkansas.parkansas.GAMEDAY_ALERT";
    public static final String FREE_PARKING_ALERT = "edu.uark.csce.parkansas.parkansas.FREE_PARKING_ALERT";
    public static final String TIME_EXPIRATION_ALERT = "edu.uark.csce.parkansas.parkansas.TIME_EXPIRATION_ALERT";
    public static final String HARMON_ALERT = "edu.uark.csce.parkansas.parkansas.HARMON_ALERT";

    public static final String HARMON_PASS = "edu.uark.csce.parkansas.parkansas.HARMON_PASS";
    public static final String RESIDENT_RESERVED = "edu.uark.csce.parkansas.parkansas.RESIDENT_RESERVED";
    public static final String RESERVED_PASS = "edu.uark.csce.parkansas.parkansas.RESERVED_PASS";
    public static final String STUDENT_PASS = "edu.uark.csce.parkansas.parkansas.STUDENT_PASS";
    public static final String FACULTY_PASS = "edu.uark.csce.parkansas.parkansas.FACULTY_PASS";
    public static final String REMOTE_PASS = "edu.uark.csce.parkansas.parkansas.REMOTE_PASS";
    public static final String ADA_PASS = "edu.uark.csce.parkansas.parkansas.ADA_PASS";

    public static final String ACTION_SUSPEND = "edu.uark.csce.parkansas.parkansas.ACTION_SUSPEND";
    public static final String ACTION_DISMISS = "edu.uark.csce.parkansas.parkansas.ACTION_DISMISS";
    public static final String CLOCK_VALUE = "edu.uark.csce.parkansas.parkansas.CLOCK_VALUE";
    public static final String TIMER_VALUE = "edu.uark.csce.parkanas.parkansas.TIMER_VALUE";

    public static boolean onCampus = false, serviceOn = false, atLeastOneNotificationChecked = false;

    public static String classificationSelection = "";

    public static final int SNOOZE_DURATION = 20000;
    public static final int DEFAULT_TIMER_DURATION = 10000;

    public static final int NOTIFICATION_ID = 101;
    public static final int ALARM_ID = 90210;
    public static final int TIME_ALERT_ID = 300;

    public static final int ALERT_RETURN_INT = 989;
    public static final int PREFERENCE_RETURN_INT = 898;

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String SHARED_PREFERENCES =
            "edu.uark.csce.parkansas.parkansas.SHARED_PREFERENCES";

    private static ActivityUtils instance = null;

    private ActivityUtils() {
    }

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    public static long currentTimeInMillis() {
        Time time = null;
        time = new Time(time.getCurrentTimezone());
        time.setToNow();
        return time.toMillis(false);
    }
}
