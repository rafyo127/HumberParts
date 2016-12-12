/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */
package humberparts.walkingprogrammers;

import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    static private ProgressBar spinner;
    DatabaseActivity db;
    EditText student_id, part1;
    String date,all_parts;
    private DatabaseReference mDatabase;

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
        mDatabase = FirebaseDatabase.getInstance().getReference();

        student_id = (EditText)findViewById(R.id.edittextstudentnumber);
        part1 = (EditText)findViewById(R.id.Part);
        date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

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
                }else if(part1.length() ==0){
                    Toast.makeText(this, "You must enter atleast one part", Toast.LENGTH_LONG).show();
                }else{
                    Boolean isAdded = db.insertData(student_id.getText().toString(),date,part1.getText().toString());
                    if (isAdded) {
                        spinner.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Part Added", Toast.LENGTH_SHORT).show();
                        //add data to firebase
                        Cursor res = db.databaseViewer();
                        while(res.moveToNext()){
                            mDatabase.child("users").child(student_id.getText().toString()).child("id").setValue(res.getString(0));
                            mDatabase.child("users").child(student_id.getText().toString()).child("Student_number").setValue(res.getString(1));
                            mDatabase.child("users").child(student_id.getText().toString()).child("Date").setValue(res.getString(2));
                            mDatabase.child("users").child(student_id.getText().toString()).child("Part_number").setValue(res.getString(3));
                        }

                        mDatabase.child("inventory").child("parts").child(part1.getText().toString());
                        mDatabase.keepSynced(true);
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("DATA",dataSnapshot.getChildren().toString());
                                int id = (Integer)dataSnapshot.child("in stock").getValue();
                                id--;
                                mDatabase.child("inventory").child("parts").child(part1.getText().toString()).child("in stock").setValue(id);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

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
        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(false);
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
