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
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference reference;

    TextView part_name, part_num,id;
    private List<ModelInventory> mInventory;
    private ListView lv_inventory;
    private InventoryAdapter adapter_inventory;
    private SearchView searchView;

    String  part1_name, part2_name, part3_name,
            part4_name, part5_name, part6_name;

    String part1_stock,part2_stock,part3_stock,
            part4_stock,part5_stock,part6_stock;

    String part1_id,part2_id,part3_id,part4_id,part5_id,part6_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        part_name=(TextView)findViewById(R.id.tv_part_name);
        part_num=(TextView)findViewById(R.id.tv_part_num);
        id = (TextView)findViewById(R.id.tv_id_in);

        mInventory = new LinkedList<>();
        showItems();
        lv_inventory = (ListView) findViewById(R.id.lv_inventory);
        adapter_inventory = new InventoryAdapter(getBaseContext(), mInventory);
        lv_inventory.setAdapter(adapter_inventory);
        //all_inventory(adapter_inventory,mInventory);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    filter_inventory(query,adapter_inventory, mInventory);
                }else {
                    all_inventory(adapter_inventory, mInventory);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    filter_inventory(newText, adapter_inventory, mInventory);
                } else {
                    all_inventory(adapter_inventory, mInventory);
                }
                return false;
            }
        });
        return true;
    }

    private void filter_inventory(String part_search, InventoryAdapter adapter_inventory, final List<ModelInventory> mInventory) {

        try{
            reference= FirebaseDatabase.getInstance().getReference().child("inventory").child("parts").child("part"+part_search);
            reference.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mInventory.clear();

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            try{
                                String id = map.get("id").toString();
                                String stock = map.get("in stock").toString();
                                String name = map.get("name").toString();

                                Log.d("getUser:DATA", id);
                                Log.d("getUser:DATA", stock);
                                Log.d("getUser:DATA", name);

                                ModelInventory inventory_search = new ModelInventory();
                                inventory_search.setId(id);
                                inventory_search.setName(name);
                                inventory_search.setStocks(stock);

                                mInventory.add(inventory_search);
                            }catch (NullPointerException w){
                                //nothing in the list
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("ERR", "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }catch (NullPointerException e){
            //nothing in the list
        }
        adapter_inventory.setItems(mInventory);
        adapter_inventory.notifyDataSetChanged();
    }

    private void all_inventory(InventoryAdapter adapter_inventory, List<ModelInventory> mInventory) {
        mInventory.clear();
        showItems();
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
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        if (mInventory == null) {

        }
    }

    public void showItems(){

        reference= FirebaseDatabase.getInstance().getReference().child("inventory").child("parts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                part1_name = dataSnapshot.child("part1").child("name").getValue().toString();
                part1_stock = dataSnapshot.child("part1").child("in stock").getValue().toString();
                part1_id=dataSnapshot.child("part1").child("id").getValue().toString();

                part2_name = dataSnapshot.child("part2").child("name").getValue().toString();
                part2_stock = dataSnapshot.child("part2").child("in stock").getValue().toString();
                part2_id=dataSnapshot.child("part2").child("id").getValue().toString();

                part3_name = dataSnapshot.child("part3").child("name").getValue().toString();
                part3_stock = dataSnapshot.child("part3").child("in stock").getValue().toString();
                part3_id=dataSnapshot.child("part3").child("id").getValue().toString();

                part4_name = dataSnapshot.child("part4").child("name").getValue().toString();
                part4_stock = dataSnapshot.child("part4").child("in stock").getValue().toString();
                part4_id=dataSnapshot.child("part4").child("id").getValue().toString();

                part5_name = dataSnapshot.child("part5").child("name").getValue().toString();
                part5_stock = dataSnapshot.child("part5").child("in stock").getValue().toString();
                part5_id=dataSnapshot.child("part5").child("id").getValue().toString();

                part6_name = dataSnapshot.child("part6").child("name").getValue().toString();
                part6_stock = dataSnapshot.child("part6").child("in stock").getValue().toString();
                part6_id=dataSnapshot.child("part6").child("id").getValue().toString();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERR", "getUser:onCancelled", databaseError.toException());
            }
        });

        ModelInventory inventory = new ModelInventory();
        inventory.setId(part1_id);
        inventory.setName(part1_name);
        inventory.setStocks(part1_stock);
        mInventory.add(inventory);

        ModelInventory inventory1 = new ModelInventory();
        inventory1.setId(part2_id);
        inventory1.setName(part2_name);
        inventory1.setStocks(part2_stock);
        mInventory.add(inventory1);

        ModelInventory inventory2 = new ModelInventory();
        inventory2.setId(part3_id);
        inventory2.setName(part3_name);
        inventory2.setStocks(part3_stock);
        mInventory.add(inventory2);

        ModelInventory inventory3 = new ModelInventory();
        inventory3.setId(part4_id);
        inventory3.setName(part4_name);
        inventory3.setStocks(part4_stock);
        mInventory.add(inventory3);

        ModelInventory inventory4 = new ModelInventory();
        inventory4.setId(part5_id);
        inventory4.setName(part5_name);
        inventory4.setStocks(part5_stock);
        mInventory.add(inventory4);

        ModelInventory inventory5 = new ModelInventory();
        inventory5.setId(part6_id);
        inventory5.setName(part6_name);
        inventory5.setStocks(part6_stock);
        mInventory.add(inventory5);
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