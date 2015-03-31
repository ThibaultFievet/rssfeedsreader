package fievetthibault.rssfeedreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDB {

    private static final int VERSION_DB = 1;
    private static final String NAME_DB = "settings.db";

    private static final String TABLE_SETTINGS = "table_settings";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_READ_ARTICLES = "read_articles";
    private static final int NUM_COL_READ_ARTICLES = 1;
    private static final String COL_SORT = "sort";
    private static final int NUM_COL_SORT = 2;
    private static final String COL_SORT_SOURCE = "sort_source";
    private static final int NUM_COL_SORT_SOURCE = 3;
    private static final String COL_DELETE_DATE = "delete_date";
    private static final int NUM_COL_DELETE_DATE = 4;

    private SQLiteDatabase bdd;

    private RSSFeedReaderDB dbSQLite;

    public SettingsDB(Context context) {
        dbSQLite = new RSSFeedReaderDB(context, NAME_DB, null, VERSION_DB);
    }

    public void open(){
        bdd = dbSQLite.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public long insertSettings(Settings s) {
        ContentValues values = new ContentValues();
        values.put(COL_READ_ARTICLES, s.isReadArticles() ? 1 : 0);
        values.put(COL_SORT, s.getSort());
        values.put(COL_SORT_SOURCE, s.isSortSource() ? 1 : 0);
        values.put(COL_DELETE_DATE, s.getDeleteDate());

        return bdd.insert(TABLE_SETTINGS, null, values);
    }

    public int updateSettings(Settings s) {
        ContentValues values = new ContentValues();
        values.put(COL_READ_ARTICLES, s.isReadArticles() ? 1 : 0);
        values.put(COL_SORT, s.getSort());
        values.put(COL_SORT_SOURCE, s.isSortSource() ? 1 : 0);
        values.put(COL_DELETE_DATE, s.getDeleteDate());

        return bdd.update(TABLE_SETTINGS, values, COL_ID + " = " + s.getId(), null);
    }

    public Settings getSettings() {
        String q = "select * from " + TABLE_SETTINGS;
        Cursor c = bdd.rawQuery(q, new String[] {});

        c.moveToFirst();
        Settings s = cursorToSettings(c);
        c.close();

        if(s == null) {
            s = new Settings(false, "NEWEST_TO_OLDEST", false, "6_MONTHS");
            insertSettings(s);
            return getSettings();
        }
        return s;
    }

    private Settings cursorToSettings(Cursor c) {
        if (c.getCount() == 0)
            return null;

        Settings s = new Settings();

        s.setId(c.getInt(NUM_COL_ID));
        s.setReadArticles(c.getInt(NUM_COL_READ_ARTICLES) == 1);
        s.setSort(c.getString(NUM_COL_SORT));
        s.setSortSource(c.getInt(NUM_COL_SORT_SOURCE) == 1);
        s.setDeleteDate(c.getString(NUM_COL_DELETE_DATE));

        return s;
    }
}

