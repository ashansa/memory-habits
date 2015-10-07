package app.useful.listapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import app.useful.listapplication.Constants;
import app.useful.listapplication.R;
import app.useful.listapplication.dbconnector.ItemTableHandler;
import app.useful.listapplication.dbconnector.dao.Item;
import app.useful.listapplication.dbconnector.dao.Section;


public class SectionViewActivity extends ActionBarActivity {

    ArrayAdapter<Item> itemArrayAdapter;
    private static ItemTableHandler itemTableHandler;
    Intent intent;
    String sectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_view);

        intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(Constants.SECTION_NAME)) {
                sectionName = intent.getStringExtra(Constants.SECTION_NAME);
                this.setTitle(sectionName);
            }
        }

        System.out.println("------- section view --------");
        try {
            itemTableHandler = new ItemTableHandler(this);
            itemTableHandler.open();

            final List<Item> items = itemTableHandler.getItemsInSection(sectionName);
            itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, items);
            ListView listView = (ListView) findViewById(R.id.item_listView);
            listView.setAdapter(itemArrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Item selectedItem = items.get(position);
                    showItemDetails(selectedItem);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showItemDetails(Item item) {
        AlertDialog.Builder itemDetailBuilder = new AlertDialog.Builder(this);
        itemDetailBuilder.setMessage(item.getDescription());
        itemDetailBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        itemDetailBuilder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_section_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_item) {
            //TODO call add item activity
/*
            Item newItem = itemTableHandler.createItem("aa", "desc", sectionName);
            itemArrayAdapter.add(newItem);*/
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra(Constants.SECTION_NAME, sectionName);
            startActivity(intent);
        } else if(id == R.id.delete_all) {
            recreateTable();
        }
        itemArrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public static ItemTableHandler getItemTableHandler() {
        return itemTableHandler;
    }

    private void recreateTable() {
        itemTableHandler.recreateTable();
        List<Item> allItems = itemTableHandler.getItemsInSection(sectionName);
        itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, allItems);
    }
}
