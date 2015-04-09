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

    ArrayList<String> list;
    ArrayList<Button> buttons = new ArrayList<>();
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

        Button btn1 = (Button) getView().findViewById(R.id.allBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                onToolbarFilterOptionsFragmentClickedListener.onToolbarFilterOptionsFragmentClicked(tag);
                for(Button btnT : buttons){
                    btnT.setTextColor(Color.WHITE);
                }
            }
        });

        Button btn2 = (Button) getView().findViewById(R.id.noneBtn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                onToolbarFilterOptionsFragmentClickedListener.onToolbarFilterOptionsFragmentClicked(tag);
                for(Button btnT : buttons){
                    btnT.setTextColor(Color.RED);
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.linearLayout);

        int size = list.size();

        for(int j = 0; j < size; j+=3){
            LinearLayout linearLayout1 = new LinearLayout(getActivity());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.weight = 3.0f;
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout1.setMinimumHeight(0);

            for (int i = j; i < j + 3; i++) {
                if( i >= size) break;
                final Button btn = new Button(getActivity());
                btn.setLayoutParams(params);
                btn.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Small);
                if (booleans[i]) btn.setTextColor(Color.WHITE);
                else btn.setTextColor(Color.RED);
                btn.setText(list.get(i));
                btn.setTag(list.get(i));
                btn.setMinimumHeight(0);

                btn.setBackgroundColor(Color.BLACK);
                linearLayout1.addView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String tag = btn.getTag().toString();
                        onToolbarFilterOptionsFragmentClickedListener.onToolbarFilterOptionsFragmentClicked(tag);

                        if (btn.getCurrentTextColor() == Color.WHITE) {
                            btn.setTextColor(Color.RED);
                        } else {
                            btn.setTextColor(Color.WHITE);
                        }
                    }
                });
                buttons.add(btn);
            }
            linearLayout.addView(linearLayout1);
        }
    }
}

