package edu.uark.csce.parkansas.parkansas;

import android.app.Service;
import android.text.format.Time;

/**
 * Created by Kai Tribble on 12/5/2014.
 */
public class ActivityUtils {
    public static final String APPTAG = "Capstone: Parkansas";

    public static final String SKIP_LOGIN = "skipLogin";
    public static final String LOGIN_SUCCESS = "successLogin";

    public static final String USERNAME = "prefUserName";
    public static final String PASSWORD = "prefPassword";
    public static final String FIRST_NAME = "prefFirstName";
    public static final String LAST_NAME = "prefLastName";
    public static final String PASS_TYPE = "prefPassType";

    public static final int LOGIN = 1;
    public static final int LOGIN_RETURN = 2;
    public static final int SETTINGS = 8;
    public static final int QUIT = 9;

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String SHARED_PREFERENCES =
            "edu.uark.csce.parkansas.parkansas.SHARED_PREFERENCES";

    private Service mService;

    private static ActivityUtils instance = null;

    private ActivityUtils() {
    }

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    public void setService(Service service) {
        mService = service;
    }

    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }
}
