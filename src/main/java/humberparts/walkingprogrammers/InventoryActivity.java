/**
 * Team Name: The Walking Programmers
 * Team Members: Rafil Yashooa, Masoud Rahguzar, Divesh Oree
 * Date: Oct/17th/2016
 * Project Name: Humber Parts (HP)
 */

package humberparts.walkingprogrammers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    InentoryDatabase inentoryDatabase;
    TextView part_name, part_num,id;
    private List<ModelInventory> mInventory;
    private ListView lv_inventory;
    private InventoryAdapter adapter_inventory;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        inentoryDatabase = new InentoryDatabase(this);

        part_name=(TextView)findViewById(R.id.tv_part_name);
        part_num=(TextView)findViewById(R.id.tv_part_num);
        id = (TextView)findViewById(R.id.tv_id_in);

        Cursor res = inentoryDatabase.databaseViewer();
        mInventory = new LinkedList<>();

        while (res.moveToNext()) {
            ModelInventory inventory = new ModelInventory();
            inventory.setId(res.getString(0));
            inventory.setName(res.getString(1));
            inventory.setStocks(res.getString(2));

            mInventory.add(inventory);
        }

        lv_inventory = (ListView) findViewById(R.id.lv_inventory);
        adapter_inventory = new InventoryAdapter(getBaseContext(), mInventory);
        lv_inventory.setAdapter(adapter_inventory);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private void filter_inventory(String inventoryId, InventoryAdapter adapter_inventory, InentoryDatabase inentoryDatabase, List<ModelInventory> mInventory) {
        mInventory.clear();

        Cursor filters = inentoryDatabase.search(inventoryId);

        while (filters.moveToNext()) {
            ModelInventory inventory = new ModelInventory();
            inventory.setId(filters.getString(0));
            inventory.setName(filters.getString(1));
            inventory.setStocks(filters.getString(2));

            mInventory.add(inventory);
        }

        adapter_inventory.setItems(mInventory);
        adapter_inventory.notifyDataSetChanged();
    }

    private void all_inventory(String inventoryId, InventoryAdapter adapter_inventory, InentoryDatabase inentoryDatabase, List<ModelInventory> mInventory) {
        mInventory.clear();

        Cursor filters = inentoryDatabase.databaseViewer();

        while (filters.moveToNext()) {
            ModelInventory inventory = new ModelInventory();
            inventory.setId(filters.getString(0));
            inventory.setName(filters.getString(1));
            inventory.setStocks(filters.getString(2));

            mInventory.add(inventory);
        }

        adapter_inventory.setItems(mInventory);
        adapter_inventory.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.action_logout);
        MenuItem search = menu.findItem(R.id.action_logout);
        search.setVisible(false);
        if(mFirebaseUser == null){
            register.setVisible(false);
        }else{
            register.setVisible(true);
        }
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

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}

class InventoryAdapter extends BaseAdapter
{
    private final Context mContext;
    private List<ModelInventory> mInventory;
    private final LayoutInflater mLayoutInflater;
    private View mView;

    public InventoryAdapter(Context context, List<ModelInventory> inventories) {
        mContext = context;
        mInventory = inventories;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(List<ModelInventory> inventories) {
        this.mInventory = inventories;
    }
    @Override
    public int getCount() {
        return mInventory.size();
    }

    @Override
    public ModelInventory getItem(int position) {
        return mInventory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mInventory.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position);
    }

    private View createView(int position) {
        mView = mLayoutInflater.inflate(R.layout.view_inventory, null);

        TextView tv_inventoryId = (TextView) mView.findViewById(R.id.tv_inv_id);
        TextView tv_inventoryName = (TextView) mView.findViewById(R.id.tv_inv_name);
        TextView tv_inventoryStock = (TextView) mView.findViewById(R.id.tv_inv_stock);

        tv_inventoryId.setText(mInventory.get(position).getId());
        tv_inventoryName.setText(mInventory.get(position).getName());
        tv_inventoryStock.setText(mInventory.get(position).getStocks());

        return mView;
    }

}