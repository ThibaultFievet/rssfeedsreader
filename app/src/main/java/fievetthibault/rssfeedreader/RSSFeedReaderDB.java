package fievetthibault.rssfeedreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class RSSFeedReaderDB extends SQLiteOpenHelper {

    private static final String TABLE_ARTICLES = "table_articles";
    private static final String TABLE_FEEDS = "table_feeds";
    private static final String TABLE_SETTINGS = "table_settings";
    private static final String COL_ID = "id";

    private static final String COL_URL = "url";
    private static final String COL_NAME = "name";

    private static final String COL_TITLE = "title";
    private static final String COL_LINK = "link";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_SOURCE = "source";
    private static final String COL_READ = "read";

    private static final String COL_READ_ARTICLES = "read_articles";
    private static final String COL_SORT = "sort";
    private static final String COL_SORT_SOURCE = "sort_source";
    private static final String COL_DELETE_DATE = "delete_date";

    private static final String CREATE_TABLE_FEEDS = "CREATE TABLE " + TABLE_FEEDS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_URL + " TEXT NOT NULL,"
            + COL_NAME + ");";

    private static final String CREATE_TABLE_ARTICLES = "CREATE TABLE " + TABLE_ARTICLES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TITLE + " TEXT NOT NULL, "
            + COL_LINK + " TEXT NOT NULL, " + COL_DESCRIPTION + " TEXT NOT NULL, " + COL_DATE + " TEXT NOT NULL, "
            + COL_SOURCE + " TEXT NOT NULL, " + COL_READ + " INTEGER);";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_READ_ARTICLES + " INTEGER, "
            + COL_SORT + " TEXT NOT NULL, " + COL_SORT_SOURCE + " INTEGER, "
            + COL_DELETE_DATE + " TEXT NOT NULL);";

    public RSSFeedReaderDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FEEDS);
        db.execSQL(CREATE_TABLE_ARTICLES);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS + ";");
        onCreate(db);
    }

}
