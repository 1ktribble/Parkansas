package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
public class ToolbarFilterOptionsFragment extends Fragment {
    String Yes = " ✔";
    String No = " X";

    ArrayList<String> list;
    boolean[] booleans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        list = args.getStringArrayList("list");
        booleans = new boolean[list.size()];
        booleans = args.getBooleanArray("booleans");

        return inflater.inflate(R.layout.fragment_toolbar_filter_options, container, false);
    }
    public interface OnToolbarFilterOptionsFragmentClickedListener {
        public void onToolbarFilterOptionsFragmentClicked(String tag);
    }
    private OnToolbarFilterOptionsFragmentClickedListener onToolbarFilterOptionsFragmentClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToolbarFilterOptionsFragmentClickedListener = (OnToolbarFilterOptionsFragmentClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnToolbarFilterOptionsFragmentClickedListener.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.linearLayout);

        int size = list.size();


        for(int j = 0; j < size; j+=2){
            LinearLayout linearLayout1 = new LinearLayout(getActivity());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.weight = 3.0f;
            linearLayout1.setLayoutParams(params);

            for (int i = j; i < j + 2; i++) {
                if( i >= size) break;
                final Button btn = new Button(getActivity());
                btn.setLayoutParams(params);
                if (booleans[i]) btn.setText(list.get(i) + Yes);
                else btn.setText(list.get(i) + No);
                btn.setTag(list.get(i));
                btn.setTextColor(Color.WHITE);
                btn.setBackgroundColor(Color.BLACK);
                linearLayout1.addView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String tag = btn.getTag().toString();
                        onToolbarFilterOptionsFragmentClickedListener.onToolbarFilterOptionsFragmentClicked(tag);
                        if (btn.getText().equals(tag + Yes)) {
                            btn.setText(tag + No);
                        } else {
                            btn.setText(tag + Yes);
                        }
                    }
                });
            }
            linearLayout.addView(linearLayout1);
        }
    }
}

