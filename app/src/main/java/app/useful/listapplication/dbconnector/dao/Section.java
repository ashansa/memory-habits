package app.useful.listapplication.dbconnector.dao;

/**
 * Created by ashansa on 10/6/15.
 */
public class Section {

    private long id;
    private String sectionName;

    public Section(long id, String sectionName) {
        this.id = id;
        this.sectionName = sectionName;
    }

    public long getId() {
        return id;
    }

    public String getSectionName() {
        return sectionName;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return sectionName;
    }
}
