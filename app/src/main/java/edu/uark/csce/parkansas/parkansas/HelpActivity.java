package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class  HelpActivity extends Activity {
//    ImageView alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView contact = (TextView) findViewById(R.id.contactDescription);
        contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "team.m.parkansas@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact from App: ");
                intent.putExtra(Intent.EXTRA_TEXT, "Greeting PARKansas Developers,\n\n");
                startActivity(intent);
            }
        });

//        alert = (ImageView) findViewById(R.id.alertIcon);
//        tonyPic = (ImageView) findViewById(R.id.tonyPic);
//        dakotaPic = (ImageView) findViewById(R.id.dakotaPic);
//        kaiPic = (ImageView) findViewById(R.id.kaiPic);

        setImages();
    }

    private void setImages(){
//        alert.setImageResource(R.drawable.ic_action_suspend_alert_no_trim_midpadding);
//        tonyPic.setImageResource(R.drawable.ic_tony_picture);
//        dakotaPic.setImageResource(R.drawable.ic_dakota_placeholder);
//        kaiPic.setImageResource(R.drawable.ic_kai_picture);



    }
}
