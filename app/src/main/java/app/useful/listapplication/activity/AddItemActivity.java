package app.useful.listapplication.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;

import app.useful.listapplication.Constants;
import app.useful.listapplication.R;
import app.useful.listapplication.dbconnector.dao.Item;


public class AddItemActivity extends ActionBarActivity {

    String sectionName;
    static float ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(Constants.SECTION_NAME)) {
                sectionName = intent.getStringExtra(Constants.SECTION_NAME);
                addRatingBarListener();
            }
        }
    }

    private void addRatingBarListener() {
        RatingBar rating = (RatingBar) findViewById(R.id.rating);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = rating;
                System.out.println("----- rating changed : " + rating);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.save) {
            EditText itemName = (EditText) findViewById(R.id.item_name);
            EditText description = (EditText) findViewById(R.id.item_description);
            System.out.println("... going to set rating value : "+ ratingValue);
            Item newItem = MainActivity.getDBHandler().createItem(itemName.getText().toString(),
                    description.getText().toString(), (int) ratingValue, sectionName);
            Intent intent = new Intent(this, SectionViewActivity.class);
            intent.putExtra(Constants.SECTION_NAME, sectionName);
            startActivity(intent);
        } */

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.save:
                EditText itemName = (EditText) findViewById(R.id.item_name);
                EditText description = (EditText) findViewById(R.id.item_description);
                System.out.println("... going to set rating value : " + ratingValue);
                Item newItem = MainActivity.getDBHandler().createItem(itemName.getText().toString(),
                        description.getText().toString(), (int) ratingValue, sectionName);
                Intent intent = new Intent(this, SectionViewActivity.class);
                intent.putExtra(Constants.SECTION_NAME, sectionName);
                startActivity(intent);
                return true;
            case R.id.cancel:
                Intent intent2 = new Intent(this, SectionViewActivity.class);
                intent2.putExtra(Constants.SECTION_NAME, sectionName);
                startActivity(intent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, SectionViewActivity.class);
        intent.putExtra(Constants.SECTION_NAME, sectionName);
        return intent;
    }
}
