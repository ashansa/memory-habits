package app.useful.listapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import app.useful.listapplication.Constants;
import app.useful.listapplication.R;
import app.useful.listapplication.dbconnector.DBHandler;
import app.useful.listapplication.dbconnector.dao.Item;


public class SectionViewActivity extends ActionBarActivity {

    ArrayAdapter<Item> itemArrayAdapter;
    private static DBHandler dbHandler;
    ActionMode mActionMode;
    Intent intent;
    String sectionName;

    Item selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(Constants.SECTION_NAME)) {
                sectionName = intent.getStringExtra(Constants.SECTION_NAME);
                this.setTitle(sectionName);
            }
        }

        System.out.println("------- section view --------");
        dbHandler = MainActivity.getDBHandler();

        final List<Item> items = dbHandler.getItemsInSection(sectionName);
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

                selectedItem = items.get(position);

               //action menu
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = SectionViewActivity.this.startActionMode(mActionModeCallback);
                view.setSelected(true);

                return true;
            }
        });

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_context, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    //TODO do u want
                    dbHandler.deleteItem(selectedItem);
                    updateView();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.edit:
                    Intent intent = new Intent(SectionViewActivity.this, EditItemActivity.class);
                    intent.putExtra(Constants.ITEM, selectedItem);
                    startActivity(intent);
                    return true;
                /*case R.id.home:
                    System.out.println("==home +++++========");
                    SectionViewActivity.this.finish();
                    return true;*/
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    private ListView updateView() {
        final List<Item> items = dbHandler.getItemsInSection(sectionName);
        itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.item_listView);
        listView.setAdapter(itemArrayAdapter);
        itemArrayAdapter.notifyDataSetChanged();
        return listView;
    }

    private void showItemDetails(Item item) {
        AlertDialog.Builder itemDetailBuilder = new AlertDialog.Builder(this);
        /*final RatingBar ratingBar = new RatingBar(getApplicationContext());
        System.out.println("++++++++ rating saved : " + item.getRating());
        ratingBar.setRating(item.getRating());
        ratingBar.setStepSize(1);
        ratingBar.setNumStars(5);
        itemDetailBuilder.setView(ratingBar);*/
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
            Item newItem = dbHandler.createItem("aa", "desc", sectionName);
            itemArrayAdapter.add(newItem);*/
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra(Constants.SECTION_NAME, sectionName);
            startActivity(intent);
        } else if(id == R.id.delete_all) {
            recreateTable();
        } else if(id == R.id.populate) {
            for (int i = 0; i < 5; i++) {
                Item newItem = dbHandler.createItem(i +"aa", i+"desc", 1, sectionName);
                itemArrayAdapter.add(newItem);
            }
        }
        itemArrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }


    private void recreateTable() {
        dbHandler.recreateItemTable();
        List<Item> allItems = dbHandler.getItemsInSection(sectionName);
        itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, allItems);
    }
}
