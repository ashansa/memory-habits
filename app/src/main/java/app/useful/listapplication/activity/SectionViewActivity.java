package app.useful.listapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    View selectedView;

    MenuItem addMenuItem;
    MenuItem editMenuItem;
    MenuItem deleteMenuItem;
    MenuItem cancelMenuItem;

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
                selectedView = view;

                showEditOptions(true);
                view.setBackgroundColor(Color.parseColor("#8ad5f0"));


               /*//action menu
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = SectionViewActivity.this.startActionMode(mActionModeCallback);
                view.setSelected(true);
                view.setBackgroundColor(Color.parseColor("#8ad5f0"));
                getSupportActionBar().hide();*/
                return true;
            }
        });

    }

    /*private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

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
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            if(selectedView != null) {
                selectedView.setBackgroundColor(Color.TRANSPARENT);
            }
            getSupportActionBar().show();
        }
    };*/

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
        addMenuItem = menu.getItem(0);
        editMenuItem = menu.getItem(1);
        deleteMenuItem = menu.getItem(2);
        cancelMenuItem = menu.getItem(3);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_item:
                Intent intent = new Intent(this, AddItemActivity.class);
                intent.putExtra(Constants.SECTION_NAME, sectionName);
                startActivity(intent);
                break;
            case R.id.delete_item:
                handleDeleteItem(selectedItem);
                return true;
            case R.id.edit_item:
                Intent intent2 = new Intent(SectionViewActivity.this, EditItemActivity.class);
                intent2.putExtra(Constants.ITEM, selectedItem);
                startActivity(intent2);
                return true;
            case R.id.cancel:
                selectedView.setBackgroundColor(Color.TRANSPARENT);
                showEditOptions(false);
                return true;
            case R.id.delete_all:
                recreateTable();
                break;
            case R.id.populate:
                for (int i = 0; i < 5; i++) {
                    Item newItem = dbHandler.createItem(i +"aa", i+"desc", 1, sectionName);
                    itemArrayAdapter.add(newItem);
                }
                break;
        }

        itemArrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    private void handleDeleteItem(final Item itemToDelete) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Delete");
        dialogBuilder.setMessage("Do you want to delete the item?");
        dialogBuilder.setIcon(R.drawable.delete);
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.deleteItem(itemToDelete);
                showEditOptions(false);
                updateView();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedView.setBackgroundColor(Color.TRANSPARENT);
                showEditOptions(false);
            }
        });

        dialogBuilder.show();
    }

    private void showEditOptions(boolean showEditOptions) {
        if(showEditOptions) {
            addMenuItem.setVisible(false);
            editMenuItem.setVisible(true);
            deleteMenuItem.setVisible(true);
            cancelMenuItem.setVisible(true);
        } else {
            addMenuItem.setVisible(true);
            editMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);
            cancelMenuItem.setVisible(false);
        }
    }


    private void recreateTable() {
        dbHandler.recreateItemTable();
        List<Item> allItems = dbHandler.getItemsInSection(sectionName);
        itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, allItems);
    }
}
