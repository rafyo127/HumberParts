/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */

package humberparts.walkingprogrammers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class loadingScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_loading_screen);

        Thread th = new Thread(){

            @Override
            public void run(){
                try{
                    super.run();
                    sleep(5000);
                } catch (Exception e){

                }finally{

                    Intent i = new Intent(loadingScreen.this,
                            MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        th.start();

    }//onCreate
}//class
