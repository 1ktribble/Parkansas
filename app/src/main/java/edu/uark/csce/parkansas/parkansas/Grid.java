package edu.uark.csce.parkansas.parkansas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class Grid extends BaseAdapter {
    private Context mContext;
    private final String[] description;
    private final int[] icon;

    public Grid(Context context,String[] description,int[] icon ) {
        mContext = context;
        this.icon = icon;
        this.description = description;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return description.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.help_grid, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText(description[position]);
            imageView.setImageResource(icon[position]);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}

