package edu.uark.csce.parkansas.parkansas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kai Tribble on 3/28/2015.
 */
public class AlertItemAdapter extends ArrayAdapter<AlertData>{

    int resource;

    public AlertItemAdapter(Context context, int resource, ArrayList<AlertData> alertList){
        super(context, resource, alertList);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout alertView;

        AlertData item = getItem(position);

        String alertName = item.getAlertName();
        String alertTime = item.getAlertTime();
        String alertDay = item.getAlertDay();

        if(convertView == null){
            alertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(inflater);
            layoutInflater.inflate(resource, alertView, true);
        }
        else{
            alertView = (LinearLayout) convertView;
        }

        TextView alertNameView = (TextView) alertView.findViewById(R.id.alarmTitle),
                alertTimeView = (TextView) alertView.findViewById(R.id.timeText),
                alertDayView = (TextView) alertView.findViewById(R.id.dayText);

        alertNameView.setText(alertName);
        alertTimeView.setText(alertTime);
        alertDayView.setText(alertDay);

        return alertView;
    }

}
