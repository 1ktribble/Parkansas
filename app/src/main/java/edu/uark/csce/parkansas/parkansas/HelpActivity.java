package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;


public class  HelpActivity extends Activity {
    GridView grid;
    String[] description = {
            "Alert Activity",
            "Refresh Map",
            "Filter Option",
            "Parking Garages",
            "Search",
            "Center Map"};
    int[] icon = {
            R.drawable.ic_action_suspend_alert_no_trim_midpadding,
            R.drawable.ic_refresh_map_no_trim_midpadding,
            R.drawable.filters,
            R.drawable.g,
            R.drawable.ic_search,
            R.drawable.center_map};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Grid adapter = new Grid(HelpActivity.this, description, icon);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(adapter);

        TextView contact = (TextView) findViewById(R.id.contactDescription);
        contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "team.m.parkansas@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact from App: ");
                intent.putExtra(Intent.EXTRA_TEXT, "Greeting PARKansas Developers,\n\n");
                startActivity(intent);
            }
        });
    }
}
