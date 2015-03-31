package fievetthibault.rssfeedreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class Feeds extends Activity implements AdapterView.OnItemClickListener {

    // Init the SQLite DB
    FeedsDB feedsDB;
    ArticlesDB articlesDB;
    SettingsDB settingsDB;
    Settings settings;

    // ListView to print the articles
    private ListView listView;
    private ArrayAdapter<RssItem> articles;
    private ArticlesAdapter adapter;
    private List<RssItem> items = new ArrayList<RssItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        articlesDB = new ArticlesDB(this);
        feedsDB = new FeedsDB(this);

        // Load the settings to display the articles.
        settingsDB = new SettingsDB(this);

        listView = (ListView) findViewById(R.id.article_listview);
        listView.setOnItemClickListener(this);

        articles = new ArrayAdapter<RssItem>(this, R.layout.article_item, R.id.item_title, items);

        settingsDB.open();
        settings = settingsDB.getSettings();
        settingsDB.close();

        articlesDB.open();
        List<RssItem> toPrint = articlesDB.getAllArticles(settings);
        articlesDB.close();

        adapter = new ArticlesAdapter(toPrint);
        listView.setAdapter(adapter);

        articles.clear();
        for (RssItem item : toPrint) {
            articles.add(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // We load the detail activity with the asked article.
        startActivity(ItemDetailActivity.getStartIntent(this, articles.getItem(position)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                // Switch to activity to add feeds
                Intent addFeed = new Intent(Feeds.this, AddFeed.class);
                startActivity(addFeed);
                return true;
            case R.id.menu_refresh:
                // Refresh button.
                new GetFeedTask().execute(); // Download the articles asynchronously.
                return true;
            case R.id.menu_read:
                // "Mark as read" button.
                articlesDB.open();
                articlesDB.updateReadAllArticles();
                articlesDB.close();
                finish();
                startActivity(getIntent());
                return true;
            case R.id.settings_manage:
                // Load the settings activity to manage the feeds.
                Intent manageFeeds = new Intent(Feeds.this, ManageFeeds.class);
                startActivity(manageFeeds);
                return true;
            case R.id.settings_display:
                // Load the settings activity for the display options.
                Intent displayFeeds = new Intent(Feeds.this, DisplaySettings.class);
                startActivity(displayFeeds);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<RssFeed> GetFeeds() {
        // Return all the feeds.
        feedsDB.open();
        List<RssFeed> feeds = feedsDB.getAllFeeds();
        feedsDB.close();

        return feeds;
    }

    // Class to print the articles in the listView.
    public class ArticlesAdapter extends BaseAdapter {

        public class Article {
            String title;
            String date;
            Boolean read;
        }

        List<Article> articlesList;

        public ArticlesAdapter(List<RssItem> items) {
            articlesList = getDataForListView(items);
        }

        @Override
        public int getCount() {
            return articlesList.size();
        }

        @Override
        public Object getItem(int position) {
            return articlesList.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View view, ViewGroup group) {
            if (view==null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.article_item, group, false);
            }

            // Print the article
            TextView title = (TextView)view.findViewById(R.id.item_title);
            TextView date = (TextView)view.findViewById(R.id.item_date);
            CheckBox read = (CheckBox)view.findViewById(R.id.item_read);

            Article article = articlesList.get(position);
            title.setText(article.title);
            date.setText(article.date);
            read.setTag(position);
            read.setChecked(article.read);
            read.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    // If we check the box, mark the article read / unread.
                    int indice = (Integer) buttonView.getTag();
                    articlesDB.open();
                    articlesDB.updateReadArticle(articles.getItem(indice).getId(), isChecked);
                    articlesDB.close();
                    articlesList.get(indice).read = isChecked;

                    settingsDB.open();
                    settings = settingsDB.getSettings();
                    settingsDB.close();

                    if(!settings.isReadArticles()) {
                        // Update the listView.
                        articlesList.remove(articlesList.get(indice));
                        listView.setAdapter(adapter);
                    }
                }
            });

            view.setTag(article);

            return view;
        }

        public List<Article> getDataForListView(List<RssItem> items)
        {
            List<Article> articlesList = new ArrayList<Article>();

            for (RssItem item : items) {
                Article article = new Article();
                article.title = item.getTitle();

                // Print a more comfortable date.
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.US);
                    inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

                    Date date = inputFormat.parse(item.getDate());
                    Date now = new Date();

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM");

                    if (compareToDay(date, now) == 0) {
                        // If the article date is today, print the hour of publication.
                        outputFormat = new SimpleDateFormat("HH:mm");
                    }

                    article.date = outputFormat.format(date);
                } catch (ParseException e) {
                    article.date = "Bad Date";
                    e.printStackTrace();
                }

                article.read = item.getRead();
                articlesList.add(article);
            }

            return articlesList;
        }

        public int compareToDay(Date date1, Date date2) {
            if (date1 == null || date2 == null) {
                return 0;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date1).compareTo(sdf.format(date2));
        }

    }

    private class GetFeedTask extends AsyncTask<Void, Void, List<RssParser.Item>> {
        @Override
        protected List<RssParser.Item> doInBackground(Void... params) {
            // Get articles from feeds.
            try {
                List<RssFeed> feeds = GetFeeds();
                InputStream in;
                List<RssParser.Item> items = new ArrayList<RssParser.Item>();

                for (RssFeed feed : feeds) {
                    URL url = new URL(feed.getURL());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    in = conn.getInputStream();
                    try {
                        List<RssParser.Item> tmp = new RssParser().parse(in);

                        // Search for new articles, then DB update.
                        UpdateArticleBase(tmp, feed.getName());
                        items.addAll(tmp);

                        in.close();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }

                return items;

            } catch(IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<RssParser.Item> listArticles) {
            // Remove the articles oldest than the limit defined in the settings.
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            Calendar c = Calendar.getInstance();

            settingsDB.open();
            settings = settingsDB.getSettings();
            settingsDB.close();

            if(settings.getDeleteDate().equals("1_MONTH"))  c.add(Calendar.MONTH, -1);
            else if(settings.getDeleteDate().equals("6_MONTHS")) c.add(Calendar.MONTH, -6);
            else c.add(Calendar.YEAR, -1);

            articlesDB.open();
            articlesDB.removeOldestArticles(inputFormat.format(c.getTime()));
            articlesDB.close();

            // Articles are ready : put them in the listView.
            List<RssItem> toPrint = getArticles();

            adapter = new ArticlesAdapter(toPrint);
            listView.setAdapter(adapter);

            articles.clear();
            for (RssItem item : toPrint) {
                articles.add(item);
            }
        }

        protected void UpdateArticleBase(List<RssParser.Item> items, String source) {

            articlesDB.open();

            for (RssParser.Item item : items) {
                // Is the article already in the database ?
                RssItem article = articlesDB.getArticleWithTitle(item.title);

                if (article == null) {
                    article = new RssItem();
                    article.setTitle(item.title);
                    article.setLink(item.link);
                    article.setDescription(item.description);
                    article.setDate(item.pubDate);
                    article.setSource(source);
                    article.setRead(false);
                    articlesDB.insertArticle(article);
                }
            }

            articlesDB.close();
        }

        public List<RssItem> getArticles() {
            // Get the articles as defined by the settings.
            settingsDB.open();
            settings = settingsDB.getSettings();
            settingsDB.close();

            articlesDB.open();
            List<RssItem> articles = articlesDB.getAllArticles(settings);
            articlesDB.close();

            return articles;
        }
    }
}
