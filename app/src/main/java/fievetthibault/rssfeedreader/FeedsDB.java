package fievetthibault.rssfeedreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FeedsDB {

    private static final int VERSION_DB = 1;
    private static final String NAME_DB = "feeds.db";

    private static final String TABLE_FEEDS = "table_feeds";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_URL = "url";
    private static final int NUM_COL_URL = 1;
    private static final String COL_NAME = "name";
    private static final int NUM_COL_NAME = 2;

    private String[] allColumns = {COL_ID, COL_URL, COL_NAME};

    private SQLiteDatabase bdd;

    private RSSFeedReaderDB dbSQLite;

    public FeedsDB(Context context) {
        dbSQLite = new RSSFeedReaderDB(context, NAME_DB, null, VERSION_DB);
    }

    public void open(){
        bdd = dbSQLite.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public long insertFeed(RssFeed feed) {
        ContentValues values = new ContentValues();
        values.put(COL_URL, feed.getURL());
        values.put(COL_NAME, feed.getName());

        return bdd.insert(TABLE_FEEDS, null, values);
    }

    public int updateFeed(int id, RssFeed feed) {
        ContentValues values = new ContentValues();
        values.put(COL_URL, feed.getURL());
        values.put(COL_NAME, feed.getName());

        return bdd.update(TABLE_FEEDS, values, COL_ID + " = " +id, null);
    }

    public List<RssFeed> getAllFeeds() {
        List<RssFeed> feeds = new ArrayList<RssFeed>();

        Cursor cursor = bdd.query(TABLE_FEEDS,
                allColumns, null, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            feeds.add(cursorToFeed(cursor));
        }

        cursor.close();
        return feeds;
    }

    public int removeFeedWithID(int id) {
        return bdd.delete(TABLE_FEEDS, COL_ID + " = " +id, null);
    }

    private RssFeed cursorToFeed(Cursor c) {
        if (c.getCount() == 0)
            return null;

        RssFeed feed = new RssFeed();

        feed.setId(c.getInt(NUM_COL_ID));
        feed.setURL(c.getString(NUM_COL_URL));
        feed.setName(c.getString(NUM_COL_NAME));

        return feed;
    }
}
