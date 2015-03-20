package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ToolbarFiltersFragment  extends Fragment {

    String selected = "none";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toolbar_filters, container, false);
    }

    public interface OnToolbarFiltersFragmentClickedListener {
        public void onToolbarFiltersFragmentClicked(String tag);
    }
    private OnToolbarFiltersFragmentClickedListener onToolbarFiltersFragmentClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToolbarFiltersFragmentClickedListener = (OnToolbarFiltersFragmentClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnToolbarFiltersFragmentClickedListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onToolbarFiltersFragmentClickedListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if(!tag.equals("back")) setSelected(tag);
                onToolbarFiltersFragmentClickedListener.onToolbarFiltersFragmentClicked(tag);
            }
        };

        Button btn = (Button) getView().findViewById(R.id.colorBtn);
        btn.setOnClickListener(onClickListener);

        Button btn2 = (Button) getView().findViewById(R.id.timesBtn);
        btn2.setOnClickListener(onClickListener);

        //Button btn3 = (Button) getView().findViewById(R.id.otherBtn);
        //btn3.setOnClickListener(onClickListener);

        Button btn4 = (Button) getView().findViewById(R.id.backBtn);
        btn4.setOnClickListener(onClickListener);
    }

    private void setSelected(String newSelected){
        if(newSelected.equals(selected)){
            Button btn = (Button) getView().findViewWithTag(selected);
            btn.setTextColor(Color.WHITE);
            selected = "none";
        }else {
            if(!selected.equals("none")) {
                Button oldSelectedBtn = (Button) getView().findViewWithTag(selected);
                oldSelectedBtn.setTextColor(Color.WHITE);
            }
            Button newSelectedBtn = (Button) getView().findViewWithTag(newSelected);
            newSelectedBtn.setTextColor(Color.RED);
            selected = newSelected;
        }
    }
}
