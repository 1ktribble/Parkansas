package edu.uark.csce.parkansas.parkansas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kai Tribble on 3/28/2015.
 */
public class AlertData implements Parcelable{
    String alertName, alertTime, alertDay, alertType;
    int alertTimeHour, alertTimeMinute;
    boolean alertOn;
    long createdDate;

    public AlertData() { }

    public String getAlertName() {
        return alertName;
    }
    public String getAlertTime() {
        return alertTime;
    }
    public String getAlertDay() {
        return alertDay;
    }
    public String getAlertType() { return alertType;}
    public int getAlertTimeHour() {return alertTimeHour;}
    public int getAlertTimeMinute() {return alertTimeMinute;}
    public boolean getAlertPos() {return alertOn;}
    public long getDate() {return createdDate;}

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }
    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }
    public void setAlertDay(String alertDay) {
        this.alertDay = alertDay;
    }
    public void setAlertType(String alertType) {
        this.alertDay = alertType;
    }
    public void setAlertTimeHour(int timeHour){ this.alertTimeHour = timeHour; }
    public void setAlertTimeMinute(int timeMinute){ this.alertTimeMinute = timeMinute; }
    public void setAlertPos(boolean timePos){ this.alertOn = timePos; }
    public void setDate(long l) {this.createdDate = l;}

    public AlertData(Parcel p){
        readFromParcel(p);
    }
    private void readFromParcel(Parcel p) {
        // TODO Auto-generated method stub
        alertName = p.readString();
        alertTime = p.readString();
        alertDay = p.readString();
        alertType = p.readString();

        alertTimeHour = p.readInt();
        alertTimeMinute = p.readInt();

        alertOn = p.readByte() != 0;
    }
    public AlertData(boolean alertOn, String time, String day) {
        super();
        this.alertOn = alertOn;
        this.alertTime = time;
        this.alertDay = day;
        this.createdDate = System.currentTimeMillis();
    }

    public AlertData(boolean alertOn, String time, String day, String type, String alertName,
                     long c, int hour, int minute)
    {
        super();
        this.createdDate = c;
        this.alertOn = alertOn;
        this.alertTime = time;
        this.alertDay = day;
        this.alertType = type;
        this.alertName = alertName;
        this.alertTimeHour = hour;
        this.alertTimeMinute = minute;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(alertName);
        dest.writeString(alertTime);
        dest.writeString(alertDay);
        dest.writeString(alertType);

        dest.writeByte((byte) (alertOn ? 1 : 0));

        dest.writeInt(alertTimeHour);
        dest.writeInt(alertTimeMinute);
    }

    public static final Parcelable.Creator<AlertData> CREATOR = new Creator<AlertData>() {
        public AlertData createFromParcel(Parcel in) {
            AlertData aData = new AlertData();

            aData.alertName = in.readString();
            aData.alertTime = in.readString();
            aData.alertDay = in.readString();
            aData.alertType = in.readString();

            aData.alertOn = in.readByte() != 0;

            aData.alertTimeHour = in.readInt();
            aData.alertTimeMinute = in.readInt();

            return aData;
        }

        public AlertData[] newArray(int size) {
            return new AlertData[size];
        }
    };
}
