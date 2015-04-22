package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.math.BigDecimal;
import java.util.Calendar;


public class ToolbarLotExtraFragment extends Fragment {

    String color;
    String times;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        color = getArguments().getString("color");
        times = getArguments().getString("times");

        return inflater.inflate(R.layout.fragment_toolbar_lot_extra, container, false);
    }

    public interface OnToolbarLotExtraFragmentClickedListener {
        public void onToolbarLotExtraFragmentClicked(String tag);
    }
    private OnToolbarLotExtraFragmentClickedListener onToolbarLotExtraFragmentClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToolbarLotExtraFragmentClickedListener = (OnToolbarLotExtraFragmentClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnToolbarLotExtraFragmentClickedListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onToolbarLotExtraFragmentClickedListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                String tag = (String) v.getTag();
                onToolbarLotExtraFragmentClickedListener.onToolbarLotExtraFragmentClicked(tag);
            }
        };

        Button btn = (Button) getView().findViewById(R.id.directionsBtn);
        btn.setOnClickListener(onClickListener);

        TextView textView1 = (TextView) getView().findViewById(R.id.lotColorText);
        textView1.setText(color);

        TextView textView2 = (TextView) getView().findViewById(R.id.lotTimesText);
        textView2.setText(times);

        View.OnClickListener onParkClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View diaView = inflater.inflate(R.layout.dialog_meter, null);

                builder.setView(diaView);
                final AlertDialog dialog = builder.create();
                dialog.show();
                final TimePicker pickerTime = (TimePicker) diaView.findViewById(R.id.timePicker);

                pickerTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        TextView textView = (TextView) diaView.findViewById(R.id.costText);

                        Calendar rightNow = Calendar.getInstance();

                        double cost = 1.00;
                        int hours = hourOfDay - rightNow.get(Calendar.HOUR_OF_DAY);
                        int minutes = minute - rightNow.get(Calendar.MINUTE);
                        double totalCost = hours * cost + (minutes * cost)/60;
                        totalCost = new BigDecimal(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        if(totalCost  < 0) totalCost = 0;
                        textView.setText("$" + String.valueOf(totalCost));
                    }
                });

                Button btn = (Button) diaView.findViewById(R.id.costYes);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String t1 = String.valueOf(pickerTime.getCurrentHour());
                        String t2 = String.valueOf(pickerTime.getCurrentMinute());
                        if(t2.length() == 1){
                            t2 = '0' + t2;
                        }

                        onToolbarLotExtraFragmentClickedListener.onToolbarLotExtraFragmentClicked(t1+':'+t2);
                        dialog.dismiss();
                    }
                });
                Button btn2 = (Button) diaView.findViewById(R.id.costNo);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        };

        Button parkHere = (Button) getView().findViewById(R.id.parkHereButton);
        parkHere.setOnClickListener(onParkClickListener);
    }
}
