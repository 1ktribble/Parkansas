package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class DeveloperInfoActivity extends Activity {
    ImageView codyPic, tonyPic, dakotaPic, kaiPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

        codyPic = (ImageView) findViewById(R.id.codyPic);
        tonyPic = (ImageView) findViewById(R.id.tonyPic);
        dakotaPic = (ImageView) findViewById(R.id.dakotaPic);
        kaiPic = (ImageView) findViewById(R.id.kaiPic);

        setImages();
    }

    private void setImages(){
        codyPic.setImageResource(R.drawable.ic_cody_picture);
        tonyPic.setImageResource(R.drawable.ic_tony_picture);
        dakotaPic.setImageResource(R.drawable.ic_dakota_placeholder);
        kaiPic.setImageResource(R.drawable.ic_kai_picture);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_developer_info, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
