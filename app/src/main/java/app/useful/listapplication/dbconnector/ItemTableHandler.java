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
public class ItemTableHandler extends SQLiteOpenHelper {

    private static final String TABLE_ITEM = "items";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ITEM = "item";
    private static final String COLUMN_SECTION = "section";
    private static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE = "create table " + TABLE_ITEM + "( " + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_ITEM + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " + COLUMN_SECTION + " text not null );";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEM;

    private String[] allColumns = { COLUMN_ID, COLUMN_ITEM, COLUMN_DESCRIPTION, COLUMN_SECTION};
    private SQLiteDatabase database;


    public ItemTableHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ItemTableHandler.class.getName(), "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public Item createItem(String itemName, String description, String sectionName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, itemName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_SECTION, sectionName);
        long insertId = database.insert(TABLE_ITEM, null, values);
        Cursor cursor = database.query(TABLE_ITEM, allColumns, COLUMN_ID+ " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Item newItem = cursorToSection(cursor);
        cursor.close();
        return newItem;

    }

    public List<Item> getItemsInSection(String sectionName) {
        List<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(TABLE_ITEM, allColumns, COLUMN_SECTION + "=?" ,
                new String[]{sectionName}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToSection(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public void recreateTable() {
        database.execSQL(DROP_TABLE);
        database.execSQL(CREATE_TABLE);
    }

    private Item cursorToSection(Cursor cursor) {
        Item item = new Item(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return item;
    }
}
