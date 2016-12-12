/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */
package humberparts.walkingprogrammers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    static private ProgressBar spinner;
    EditText student_id;
    DatabaseActivity db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        db = new DatabaseActivity(this);

        spinner = (ProgressBar)findViewById(R.id.progressBar4);
        spinner.setVisibility(View.GONE);

        student_id =(EditText)findViewById(R.id.ed_del_student_num);
    }
    static void clearSpinner(){
        spinner.setVisibility(View.GONE);
    }
    public void callIntent(View view) throws InterruptedException {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.buttondelete1:
                Integer deleteRow = db.deleteData(student_id.getText().toString());
                if(deleteRow > 0){
                    //delete from firebase
                    mDatabase.child("users").child(student_id.getText().toString()).removeValue();
                    Toast.makeText(DeleteActivity.this, "Student Deleted", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.VISIBLE);
                    startActivity(new Intent(this,AdminActivity.class));
                }else{
                    Toast.makeText(DeleteActivity.this, "Error in deleting", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(false);
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
