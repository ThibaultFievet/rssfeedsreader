package fievetthibault.rssfeedreader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import java.util.ArrayList;
import java.util.List;


public class ManageFeeds extends Activity implements AdapterView.OnItemClickListener  {

    private FeedsDB feedsDB;
    private ArrayAdapter<RssFeed> feeds;
    private List<RssFeed> items = new ArrayList<RssFeed>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feeds);

        feedsDB = new FeedsDB(this);

        List<RssFeed> tmp = getFeeds();

        FeedsAdapter adapter = new FeedsAdapter(tmp);
        ListView listView = (ListView) findViewById(R.id.feed_listview);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        feeds = new ArrayAdapter<RssFeed>(this, R.layout.rss_feed_item, R.id.item_name, items);

        for (RssFeed t : tmp) {
            feeds.add(t);
        }
    }

    public List<RssFeed> getFeeds() {
        feedsDB.open();
        List<RssFeed> feeds = feedsDB.getAllFeeds();
        feedsDB.close();

        return feeds;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_feeds, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent home = new Intent(ManageFeeds.this, Feeds.class);
                startActivity(home);
                return true;
            case R.id.settings_display:
                // Comportement du bouton "Paramètres"
                Intent displayFeeds = new Intent(ManageFeeds.this, DisplaySettings.class);
                startActivity(displayFeeds);
                return true;
            case R.id.menu_add:
                Intent addFeed = new Intent(ManageFeeds.this, AddFeed.class);
                startActivity(addFeed);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(EditFeed.getStartIntent(this, feeds.getItem(position)));
    }

    public class Feed {
        String url;
        String name;
        boolean delete;
    }

    public class FeedsAdapter extends BaseAdapter {

        List<Feed> feedsList;

        public FeedsAdapter(List<RssFeed> items) {
            feedsList = getDataForListView(items);
        }

        @Override
        public int getCount() {
            return feedsList.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View view, ViewGroup group) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.rss_feed_item, group, false);
            }

            TextView url = (TextView) view.findViewById(R.id.item_url);
            TextView name = (TextView) view.findViewById(R.id.item_name);
            CheckBox delete = (CheckBox) view.findViewById(R.id.item_delete);

            Feed feed = feedsList.get(position);

            url.setText(feed.url);
            name.setText(feed.name);
            delete.setChecked(feed.delete);
            delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                int position;
                String name;

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    ArticlesDB articlesDB = new ArticlesDB(getBaseContext());
                    articlesDB.open();
                    articlesDB.removeArticlesWithSource(name);
                    articlesDB.close();

                    feedsDB.open();
                    feedsDB.removeFeedWithID(position);
                    feedsDB.close();
                    finish();
                    startActivity(getIntent());
                }

                private CompoundButton.OnCheckedChangeListener init(int p, String n) {
                    position = p;
                    name = n;
                    return this;
                }
            }.init(feeds.getItem(position).getId(), feed.name));

            return view;
        }
    }

    public List<Feed> getDataForListView(List<RssFeed> items)
    {
        List<Feed> feedsList = new ArrayList<Feed>();

        for (RssFeed item : items) {
            Feed feed = new Feed();
            feed.url = item.getURL();
            feed.name = item.getName();
            feed.delete = false;
            feedsList.add(feed);
        }

        return feedsList;
    }
}
