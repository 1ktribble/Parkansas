package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ToolbarLotFragment extends Fragment {
    String name;
    String more = "Show More";
    String less = "Show Less";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        name = getArguments().getString("name");

        return inflater.inflate(R.layout.fragment_toolbar_lot, container, false);
    }

    public interface OnToolbarLotFragmentClickedListener {
        public void onToolbarLotFragmentClicked(String tag);
    }
    private OnToolbarLotFragmentClickedListener onToolbarLotFragmentClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToolbarLotFragmentClickedListener = (OnToolbarLotFragmentClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnToolbarLotFragmentClickedListener.");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if(tag.equals("showMore")) flipShow(tag);
                onToolbarLotFragmentClickedListener.onToolbarLotFragmentClicked(tag);
            }
        };

        Button btn = (Button) getView().findViewById(R.id.lotInfoBackBtn);
        btn.setOnClickListener(onClickListener);

        Button btn2 = (Button) getView().findViewById(R.id.showMoreBtn);
        btn2.setOnClickListener(onClickListener);

        TextView textView = (TextView) getView().findViewById(R.id.lotNameText);
        textView.setText(name);
    }

    public void flipShow(String tag){
        Button btn = (Button) getView().findViewWithTag(tag);
        if(btn.getText().equals(less)){
            btn.setText(more);
        }else{
            btn.setText(less);
        }
    }
}
