package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ToolbarLotExtraFragment extends Fragment {

    String color;
    String other;
    String times;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        color = getArguments().getString("color");
        other = getArguments().getString("other");
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

        TextView textView3 = (TextView) getView().findViewById(R.id.lotOtherText);
        textView3.setText(other);
    }
}
