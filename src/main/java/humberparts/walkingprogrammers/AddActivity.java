/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */
package humberparts.walkingprogrammers;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    static private ProgressBar spinner;
    DatabaseActivity db;
    EditText student_id, part1, part2, part3, part4, part5;
    String student, part_1, part_2, part_3, part_4, part_5;
    String date,all_parts;
    StringBuilder buffer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseActivity(this);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        spinner = (ProgressBar)findViewById(R.id.progressBar3);
        spinner.setVisibility(View.GONE);

        student_id = (EditText)findViewById(R.id.edittextstudentnumber);
        part1 = (EditText)findViewById(R.id.Part);
        part2 = (EditText)findViewById(R.id.Part2);
        part3 = (EditText)findViewById(R.id.Part3);
        part4 = (EditText)findViewById(R.id.Part4);
        part5 = (EditText)findViewById(R.id.Part5);
        date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        student = student_id.getText().toString();
        part_1 = part1.getText().toString();
        part_2 = part2.getText().toString();
        part_3 = part3.getText().toString();
        part_4 = part4.getText().toString();
        part_5 = part5.getText().toString();

        buffer2 = new StringBuilder();
        buffer2.append(part1);
        buffer2.append(part2);
        buffer2.append(part3);
        buffer2.append(part4);
        buffer2.append(part5);

        all_parts = buffer2.toString();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }
    void clearSpinner(){
        spinner.setVisibility(View.GONE);
    }
    public void callIntentAdd(View view) throws InterruptedException {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.buttonadd1:
                if(student_id.length() == 0){
                    Toast.makeText(this, "Student field can't be empty", Toast.LENGTH_LONG).show();
                }else if(all_parts.length() ==0){
                    Toast.makeText(this, "You must enter atleast one part", Toast.LENGTH_LONG).show();
                }else{
                    Boolean isAdded = db.insertData(student_id.getText().toString(),date,all_parts);
                    if (isAdded) {
                        spinner.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Part Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,AdminActivity.class));
                    } else {
                        Toast.makeText(this, "Error in adding ", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.action_logout);
        if(mFirebaseUser == null){
            register.setVisible(false);
        }else{
            register.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, settingActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_logout){
            mFirebaseAuth.signOut();
            startActivity(new Intent(this,MainActivity.class));
            Toast.makeText(this, "User Logged out !", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        MainActivity.clearSpinner();
        super.onBackPressed();  // optional depending on your needs
    }


}
