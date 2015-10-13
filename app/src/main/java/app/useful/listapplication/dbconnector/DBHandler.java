package app.useful.listapplication.dbconnector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.useful.listapplication.Constants;
import app.useful.listapplication.dbconnector.dao.Item;
import app.useful.listapplication.dbconnector.dao.Section;

/**
 * Created by ashansa on 10/6/15.
 */
public class DBHandler extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SECTION = "section";


    // information used only for section table
    private static final String TABLE_SECTIONS = "sections";

    private static final String CREATE_SECTIONS_TABLE = "create table " + TABLE_SECTIONS + "( " + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_SECTION + " text not null );";
    private static final String DROP_SECTIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SECTIONS;

    private String[] sectionTableAllColumns = { COLUMN_ID,COLUMN_SECTION};


    // information used only for item table
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM = "item";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RATING = "rating";

    private static final String CREATE_ITEMS_TABLE = "create table " + TABLE_ITEMS + "( " + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_ITEM + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " + COLUMN_RATING + " integer not null, " + COLUMN_SECTION + " text not null );";

    private static final String DROP_ITEMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;

    private String[] itemTableAllColumns = { COLUMN_ID, COLUMN_ITEM, COLUMN_DESCRIPTION, COLUMN_RATING, COLUMN_SECTION};


    public DBHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SECTIONS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHandler.class.getName(), "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DROP_SECTIONS_TABLE);
        db.execSQL(DROP_ITEMS_TABLE);
        onCreate(db);
    }

    //operations in section table

    public Section createSection(String sectionName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SECTION, sectionName);
        long insertId = database.insert(TABLE_SECTIONS, null, values);
        Cursor cursor = database.query(TABLE_SECTIONS, sectionTableAllColumns, COLUMN_ID+ " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Section newSection = cursorToSection(cursor);
        cursor.close();
        return newSection;
    }

    public void deleteSection(Section section) {
        System.out.println("Deleting section with id: " + section.getId());
        List<Item> items = getItemsInSection(section.getSectionName());
        database.delete(TABLE_SECTIONS, COLUMN_ID + " = " + section.getId(), null);
        for (Item item : items) {
            deleteItem(item);
        }
    }

    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<Section>();
        Cursor cursor = database.query(TABLE_SECTIONS, sectionTableAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Section section = cursorToSection(cursor);
            sections.add(section);
            cursor.moveToNext();
        }
        cursor.close();
        return sections;
    }

    public void recreateSectionsTable() {
        database.execSQL(DROP_SECTIONS_TABLE);
        database.execSQL(CREATE_SECTIONS_TABLE);
    }

    private Section cursorToSection(Cursor cursor) {
        Section section = new Section(cursor.getLong(0),cursor.getString(1) );
        return section;
    }


    //operations in item table

    public Item createItem(String itemName, String description, int rating, String sectionName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, itemName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_RATING, rating);
        values.put(COLUMN_SECTION, sectionName);
        long insertId = database.insert(TABLE_ITEMS, null, values);
        Cursor cursor = database.query(TABLE_ITEMS, itemTableAllColumns, COLUMN_ID+ " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Item newItem = cursorToItem(cursor);
        cursor.close();
        return newItem;
    }

    public void updateItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, item.getItemName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_RATING, item.getRating());
        values.put(COLUMN_SECTION, item.getSectionName());
        database.update(TABLE_ITEMS, values, COLUMN_ID + "=" + item.getId(), null);
        System.out.println("...... in db update method ..");
    }

    public void deleteItem(Item item) {
        long id = item.getId();
        System.out.println("Deleting item with id: " + id);
        database.delete(TABLE_ITEMS, COLUMN_ID + " = " + id, null);
    }

    public List<Item> getItemsInSection(String sectionName) {
        List<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(TABLE_ITEMS, itemTableAllColumns, COLUMN_SECTION + "=?" ,
                new String[]{sectionName}, null, null, COLUMN_RATING + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public void recreateItemTable() {
        database.execSQL(DROP_ITEMS_TABLE);
        database.execSQL(CREATE_ITEMS_TABLE);
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getString(4));
        return item;
    }
}
