package app.useful.listapplication.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import app.useful.listapplication.Constants;
import app.useful.listapplication.R;
import app.useful.listapplication.dbconnector.dao.Item;


public class EditItemActivity extends ActionBarActivity {

    Item itemToUpdate;
    EditText editTextDescription;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Constants.ITEM)) {
            itemToUpdate = (Item) intent.getSerializableExtra(Constants.ITEM);
            editTextDescription = ((EditText)findViewById(R.id.item_description_edit));
            ratingBar = ((RatingBar)findViewById(R.id.rating_edit));

            ((TextView) findViewById(R.id.item_name_edit)).setText(itemToUpdate.getItemName());
            editTextDescription.setText(itemToUpdate.getDescription());
            ratingBar.setRating(itemToUpdate.getRating());

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.save:
                System.out.println("... going to set rating value : "+ ratingBar.getRating());
                itemToUpdate.setRating((long) ratingBar.getRating());
                itemToUpdate.setDescription(editTextDescription.getText().toString());
                Item updatedItem = MainActivity.getDBHandler().updateItem(itemToUpdate);
                Intent intent = new Intent(this, SectionViewActivity.class);
                intent.putExtra(Constants.SECTION_NAME, itemToUpdate.getSectionName());
                startActivity(intent);
                return true;
            case R.id.cancel:
                Intent intent2 = new Intent(this, SectionViewActivity.class);
                intent2.putExtra(Constants.SECTION_NAME, itemToUpdate.getSectionName());
                startActivity(intent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent(this, SectionViewActivity.class);
        intent.putExtra(Constants.SECTION_NAME, itemToUpdate.getSectionName());
        return intent;
    }
}
