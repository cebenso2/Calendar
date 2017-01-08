package com.cbcb.chris.calendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create new event.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DataBaseHelper db = new DataBaseHelper(this);

// Inserting Shop/Rows
        /*Log.d("Insert: ", "Inserting ..");
        db.addShop(new Shop(1,"Dockers", " 475 Brannan St #330, San Francisco, CA 94107, United States"));
        db.addShop(new Shop(2,"Dunkin Donuts", "White Plains, NY 10601"));
        db.addShop(new Shop(3,"Pizza Porlar", "North West Avenue, Boston , USA"));
        db.addShop(new Shop(4,"Town Bakers", "Beverly Hills, CA 90210, USA"));
*/
// Reading all shops
        Log.d("Reading: ", "Reading all shops..");
        List<Shop> shops = db.getAllShops();
        ArrayList<String> shopList = new ArrayList<String>();
        for (Shop shop : shops) {
            String log = "Id: " + shop.getId() + " ,Name: " + shop.getName() + " ,Address: " + shop.getAddress();
// Writing shops to log
            Log.d("Shop: : ", log);
            shopList.add(shop.getName());

        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, shopList);


        mainListView = (ListView) findViewById( R.id.list_view_main );
        mainListView.setAdapter( listAdapter );
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
