package fievetthibault.rssfeedreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ArticlesDB {

    private static final int VERSION_DB = 1;
    private static final String NAME_DB = "articles.db";

    private static final String TABLE_ARTICLES = "table_articles";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_TITLE = "title";
    private static final int NUM_COL_TITLE = 1;
    private static final String COL_LINK = "link";
    private static final int NUM_COL_LINK = 2;
    private static final String COL_DESCRIPTION = "description";
    private static final int NUM_COL_DESCRIPTION = 3;
    private static final String COL_DATE = "date";
    private static final int NUM_COL_DATE = 4;
    private static final String COL_SOURCE = "source";
    private static final int NUM_COL_SOURCE = 5;
    private static final String COL_READ = "read";
    private static final int NUM_COL_READ = 6;

    private String[] allColumns = {COL_ID, COL_TITLE, COL_LINK, COL_DESCRIPTION, COL_DATE, COL_SOURCE, COL_READ};

    private SQLiteDatabase bdd;

    private RSSFeedReaderDB dbSQLite;

    public ArticlesDB(Context context){
        dbSQLite = new RSSFeedReaderDB(context, NAME_DB, null, VERSION_DB);
    }

    public void open(){
        bdd = dbSQLite.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public long insertArticle(RssItem article){
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, article.getTitle());
        values.put(COL_LINK, article.getLink());
        values.put(COL_DESCRIPTION, article.getDescription());
        values.put(COL_DATE, article.getDate());
        values.put(COL_SOURCE, article.getSource());
        values.put(COL_READ, article.getRead());

        return bdd.insert(TABLE_ARTICLES, null, values);
    }

    public int updateReadArticle(int id, boolean read){
        ContentValues values = new ContentValues();
        values.put(COL_READ, read ? 1 : 0);

        return bdd.update(TABLE_ARTICLES, values, COL_ID + " = " +id, null);
    }

    public int updateReadAllArticles(){
        ContentValues values = new ContentValues();
        values.put(COL_READ, 1);

        return bdd.update(TABLE_ARTICLES, values, COL_READ + " = 0", null);
    }

    public int updateSourceArticles(String newSource, String oldSource){
        ContentValues values = new ContentValues();
        values.put(COL_SOURCE, newSource);

        return bdd.update(TABLE_ARTICLES, values, COL_SOURCE + " = \'" +oldSource+ "\'", null);
    }

    public List<RssItem> getAllArticles(Settings s) {
        List<RssItem> articles = new ArrayList<RssItem>();

        String where = null;
        if(!s.isReadArticles()) where = COL_READ + " = 0";

        String orderby = COL_DATE + " DESC";
        if(s.getSort().equals("OLDEST_TO_NEWEST")) orderby = COL_DATE + " ASC";

        if(s.isSortSource()) orderby = COL_SOURCE + ", " + orderby;

        Cursor cursor = bdd.query(TABLE_ARTICLES,
                allColumns, where, null, null, null, orderby);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            articles.add(cursorToArticle(cursor));
        }

        cursor.close();
        return articles;
    }

    public int removeArticlesWithSource(String source){
        return bdd.delete(TABLE_ARTICLES, COL_SOURCE + " = \'" +source+ "\'", null);
    }

    public int removeOldestArticles(String date) {
        return bdd.delete(TABLE_ARTICLES, COL_DATE + " < \'" +date+ "\'", null);
    }

    public RssItem getArticleWithTitle(String title){
        String q = "select * from " + TABLE_ARTICLES + " WHERE " + COL_TITLE + " LIKE ?";
        Cursor c = bdd.rawQuery(q, new String[] { title });

        c.moveToFirst();
        RssItem article = cursorToArticle(c);
        c.close();
        return article;
    }

    private RssItem cursorToArticle(Cursor c){
        if (c.getCount() == 0)
            return null;

        RssItem article = new RssItem();

        article.setId(c.getInt(NUM_COL_ID));
        article.setTitle(c.getString(NUM_COL_TITLE));
        article.setLink(c.getString(NUM_COL_LINK));
        article.setDescription(c.getString(NUM_COL_DESCRIPTION));
        article.setDate(c.getString(NUM_COL_DATE));
        article.setSource(c.getString(NUM_COL_SOURCE));
        article.setRead(c.getInt(NUM_COL_READ) == 1);

        return article;
    }
}