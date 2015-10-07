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
import app.useful.listapplication.dbconnector.dao.Section;

/**
 * Created by ashansa on 10/6/15.
 */
public class SectionTableHandler extends SQLiteOpenHelper {

    private static final String TABLE_SECTION = "sections";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SECTION = "section";

    private static final String CREATE_TABLE = "create table " + TABLE_SECTION + "( " + COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_SECTION + " text not null );";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_SECTION;

    private String[] allColumns = { COLUMN_ID,COLUMN_SECTION};
    private SQLiteDatabase database;


    public SectionTableHandler(Context context) {
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
        Log.w(SectionTableHandler.class.getName(), "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public Section createSection(String sectionName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SECTION, sectionName);
        long insertId = database.insert(TABLE_SECTION, null, values);
        Cursor cursor = database.query(TABLE_SECTION, allColumns, COLUMN_ID+ " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Section newSection = cursorToSection(cursor);
        cursor.close();
        return newSection;

    }

    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<Section>();
        Cursor cursor = database.query(TABLE_SECTION, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Section section = cursorToSection(cursor);
            sections.add(section);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return sections;
    }

    public void recreateTable() {
        database.execSQL(DROP_TABLE);
        database.execSQL(CREATE_TABLE);
    }

    private Section cursorToSection(Cursor cursor) {
        Section section = new Section(cursor.getLong(0),cursor.getString(1) );
        return section;
    }
}
