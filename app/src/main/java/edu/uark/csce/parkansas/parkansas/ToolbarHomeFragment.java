package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ToolbarHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toolbar_home, container, false);
    }

    public interface OnToolbarHomeFragmentClickedListener {
        public void onToolbarHomeFragmentClicked(String tag);
    }
    private OnToolbarHomeFragmentClickedListener onToolbarHomeFragmentClickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToolbarHomeFragmentClickedListener = (OnToolbarHomeFragmentClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnToolbarHomeFragmentClickedListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onToolbarHomeFragmentClickedListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                String tag = (String) v.getTag();
                onToolbarHomeFragmentClickedListener.onToolbarHomeFragmentClicked(tag);
            }
        };

        ImageView btn = (ImageView) getView().findViewById(R.id.lotFiltersBtn);
        btn.setOnClickListener(onClickListener);

        ImageView btn2 = (ImageView) getView().findViewById(R.id.centerBtn);
        btn2.setOnClickListener(onClickListener);
    }
}

