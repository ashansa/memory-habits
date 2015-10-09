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

import java.sql.SQLException;
import java.util.List;

import app.useful.listapplication.Constants;
import app.useful.listapplication.R;
import app.useful.listapplication.dbconnector.DBHandler;
import app.useful.listapplication.dbconnector.dao.Section;


public class MainActivity extends ActionBarActivity{

    ArrayAdapter<Section> sectionArrayAdapter;
    private static DBHandler DBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("=============");
        setContentView(R.layout.activity_main);

        try {

            DBHandler = new DBHandler(this);
            DBHandler.open();

            final List<Section> allSections = DBHandler.getAllSections();
            sectionArrayAdapter = new ArrayAdapter<Section>(this, android.R.layout.simple_list_item_1, allSections);
            ListView listView = (ListView)findViewById(R.id.section_listView);
            listView.setAdapter(sectionArrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    Section selectedSection = allSections.get(position);
                    Intent intent = new Intent(MainActivity.this, SectionViewActivity.class);
                    intent.putExtra(Constants.SECTION_NAME, selectedSection.getSectionName());
                    startActivity(intent);
                    System.out.println(selectedSection.toString() + " ************************");
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        System.out.println("menu items");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_section) {
            System.out.println("add section menu");
            addSection();
        } else if (id == R.id.delete_all) {
            recreateTable();
            //TODO check this..........
            this.startActivity(new Intent(this,MainActivity.class));
        }

        sectionArrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    private void addSection() {
        System.out.println("in add section");
        AlertDialog.Builder addSectionBuilder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setHint("\nAdd Section Name");
        //addSectionBuilder.setMessage("Add Section:");
        addSectionBuilder.setView(input);
        addSectionBuilder.setPositiveButton(R.string.add_section, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newSectionName = input.getText().toString();
                //store sections in db
                Section section = DBHandler.createSection(newSectionName);
                sectionArrayAdapter.add(section);

            }
        });
        addSectionBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        addSectionBuilder.show();
    }

    private void recreateTable() {
        DBHandler.recreateSectionsTable();
        List<Section> allSections = DBHandler.getAllSections();
        sectionArrayAdapter = new ArrayAdapter<Section>(this, android.R.layout.simple_list_item_1, allSections);
    }

    public static DBHandler getDBHandler() {
        return DBHandler;
    }
}
